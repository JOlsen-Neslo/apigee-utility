package za.co.neslotech.apigee.commands.app;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import picocli.CommandLine;
import za.co.neslotech.apigee.commands.Command;
import za.co.neslotech.apigee.helpers.CommandHelper;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@CommandLine.Command(name = "delete", description = "delete an app from a developer")
public class Delete extends Command implements Runnable {

    @CommandLine.Option(names = {"-e", "--email"}, description = "email of the developer to delete the app", required = true)
    private String email;

    @CommandLine.Option(names = {"-n", "--name"}, description = "name of the app to delete", required = true)
    private String name;

    @CommandLine.Option(names = {"-t", "--token"}, description = "token to authenticate the request", required = true)
    private String token;

    @Override
    public void run() {
        String endpoint = CommandHelper.retrieveEndpoint(this, token);
        String trimmedToken = CommandHelper.extractToken(token);

        HttpClient httpClient = CommandHelper.createClient();
        if (host != null) {
            httpClient = CommandHelper.createClient(host, port);
        }

        try {
            URIBuilder uriBuilder = new URIBuilder(CommandHelper.encodeURL(endpoint + "/" + email + "/apps/" + name));
            URI deleteUri = uriBuilder.build();

            HttpDelete httpDelete = new HttpDelete(deleteUri);
            httpDelete.setHeader("Authorization", "Basic " + trimmedToken);

            System.out.println("SENDING APP DELETE REQUEST");
            System.out.println("=================");
            System.out.println("Endpoint URL: " + httpDelete.getMethod() + " " + deleteUri.toString());

            System.out.println();
            System.out.println();

            HttpResponse httpResponse = httpClient.execute(httpDelete);
            int code = httpResponse.getStatusLine().getStatusCode();
            String response = EntityUtils.toString(httpResponse.getEntity());

            if (code == 500) {
                throw new IllegalArgumentException("The email address " + email + " is invalid.");
            } else if (code == 404) {
                if (response.contains("developer.service.AppDoesNotExist")) {
                    throw new IllegalArgumentException("An app with the name " + name + " does not exist.");
                } else if (response.contains("developer.service.DeveloperDoesNotExist")
                        || response.contains("developer.service.DeveloperIdDoesNotExist")) {
                    throw new IllegalArgumentException("A developer with an email address of " + email + " does not exist.");
                }
                throw new IllegalArgumentException("A developer with an email address of " + email + " does not exist in organization");
            } else if (code == 401) {
                throw new AuthenticationException("The token supplied is invalid.");
            }

            System.out.println("RECEIVED APP DELETE RESPONSE");
            System.out.println("=================");
            System.out.println("SUCCESS " + code);
            System.out.println();
        } catch (IOException | AuthenticationException | URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
