package za.co.neslotech.apigee.commands.proxy;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import picocli.CommandLine;
import za.co.neslotech.apigee.commands.Command;
import za.co.neslotech.apigee.helpers.CommandHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@CommandLine.Command(name = "delete", description = "delete an Apigee API Proxy")
public class Delete extends Command implements Runnable {

    @CommandLine.Option(names = {"-n", "--name"}, description = "name of the proxy to delete", required = true)
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
            URIBuilder uriBuilder = new URIBuilder(CommandHelper.encodeURL(endpoint + "/" + name));
            URI deleteUri = uriBuilder.build();

            HttpDelete httpDelete = new HttpDelete(deleteUri);
            httpDelete.setHeader("Authorization", "Basic " + trimmedToken);

            System.out.println("SENDING PROXY DELETE REQUEST");
            System.out.println("=================");
            System.out.println("Endpoint URL: " + httpDelete.getMethod() + " " + deleteUri.toString());

            System.out.println();
            System.out.println();

            HttpResponse httpResponse = httpClient.execute(httpDelete);
            int code = httpResponse.getStatusLine().getStatusCode();

            if (code == 400) {
                throw new IllegalArgumentException("Cannot delete the APIProxy named " + name + ". Undeploy the proxy and try again.");
            } else if (code == 404) {
                throw new IllegalArgumentException("An APIProxy named " + name + " does not exist in organization");
            } else if (code == 401) {
                throw new AuthenticationException("The token supplied is invalid.");
            }

            System.out.println("RECEIVED PROXY DELETE RESPONSE");
            System.out.println("=================");
            System.out.println("SUCCESS");
            System.out.println(EntityUtils.toString(httpResponse.getEntity()));
        } catch (IOException | AuthenticationException | URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
