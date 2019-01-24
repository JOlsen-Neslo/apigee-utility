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

@CommandLine.Command(name = "undeploy", description = "undeploy an Apigee API Proxy revision from an environment")
public class Undeploy extends Command implements Runnable {

    @CommandLine.Option(names = {"-n", "--name"}, description = "name of the proxy to undeploy", required = true)
    private String name;

    @CommandLine.Option(names = {"-e", "--env"}, description = "environment to undeploy the revision", required = true)
    private String env;

    @CommandLine.Option(names = {"-r", "--revision"}, description = "revision number to undeploy", required = true)
    private String revision;

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
            URIBuilder uriBuilder = new URIBuilder(CommandHelper.encodeURL(endpoint + "/environments/" + env
                    + "/apis/" + name
                    + "/revisions/" + revision
                    + "/deployments"));
            URI undeployUri = uriBuilder.build();

            HttpDelete httpDelete = new HttpDelete(undeployUri);
            httpDelete.setHeader("Authorization", "Basic " + trimmedToken);

            System.out.println("SENDING PROXY UNDEPLOY REQUEST");
            System.out.println("=================");
            System.out.println("Endpoint URL: " + httpDelete.getMethod() + " " + undeployUri.toString());

            System.out.println();
            System.out.println();

            HttpResponse httpResponse = httpClient.execute(httpDelete);
            int code = httpResponse.getStatusLine().getStatusCode();
            String response = EntityUtils.toString(httpResponse.getEntity());

            if (code == 400) {
                throw new IllegalArgumentException("Revision " + revision
                        + " of APIProxy " + name
                        + " is not deployed to the environment " + env + " in the organization madapi");
            } else if (code == 404) {
                if (response.contains("messaging.config.beans.ApplicationDoesNotExist")) {
                    throw new IllegalArgumentException("An APIProxy named " + name + " does not exist in organization");
                } else if (response.contains("messaging.config.beans.ApplicationRevisionDoesNotExist")) {
                    throw new IllegalArgumentException("An APIProxy revision " + revision + " does not exist for APIProxy "
                            + name + " in organization");
                } else if (response.contains("messaging.config.beans.EnvironmentDoesNotExist")) {
                    throw new IllegalArgumentException("Environment: " + env + " does not exist in organization");
                }
            } else if (code == 401) {
                throw new AuthenticationException("The token supplied is invalid.");
            }

            System.out.println("RECEIVED PROXY UNDEPLOY RESPONSE");
            System.out.println("=================");
            System.out.println("SUCCESS");
            System.out.println(response);
        } catch (URISyntaxException | IOException | AuthenticationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
