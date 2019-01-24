package za.co.neslotech.apigee.commands.proxy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import picocli.CommandLine;
import za.co.neslotech.apigee.commands.Command;
import za.co.neslotech.apigee.helpers.CommandHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command(name = "import", description = "import an Apigee API Proxy in a ZIP file")
public class Import extends Command implements Runnable {

    @CommandLine.Option(names = {"-n", "--name"}, description = "name of the proxy to import", required = true)
    private String name;

    @CommandLine.Option(names = {"-l", "--location"}, description = "location of the proxy ZIP to import", required = true)
    private String location;

    @CommandLine.Option(names = {"-t", "--token"}, description = "token to authenticate the request", required = true)
    private String token;

    @Override
    public void run() {
        String endpoint = CommandHelper.retrieveEndpoint(this, token);
        String trimmedToken = CommandHelper.extractToken(token);

        Path path = Paths.get(location);
        File file = new File(path.toUri());

        System.out.println("Importing a proxy from the following location: " + file.getAbsolutePath());

        try {
            if (!file.exists()) {
                throw new FileNotFoundException("The file location supplied cannot be found: " + file.getAbsolutePath());
            }

            if (!file.getName().endsWith(".zip")) {
                throw new IllegalArgumentException("The file supplied needs to be in ZIP format.");
            }

            HttpClient httpClient = CommandHelper.createClient();
            if (host != null) {
                httpClient = CommandHelper.createClient(host, port);
            }

            URIBuilder uriBuilder = new URIBuilder(endpoint);
            uriBuilder.addParameter("action", "import");
            uriBuilder.addParameter("name", name);
            uriBuilder.addParameter("validate", "true");

            URI importURI = uriBuilder.build();

            HttpEntity fileEntity = EntityBuilder.create().setFile(file).build();
            HttpPost httpPost = new HttpPost(importURI);
            httpPost.setHeader("Authorization", "Basic " + trimmedToken);
            httpPost.setEntity(fileEntity);

            System.out.println("SENDING PROXY IMPORT REQUEST");
            System.out.println("=================");
            System.out.println("Endpoint URL: " + httpPost.getMethod() + " " + importURI.toString());

            System.out.println();
            System.out.println();

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int code = httpResponse.getStatusLine().getStatusCode();

            if (code == 400) {
                throw new InvalidObjectException("The proxy bundle supplied is invalid.");
            } else if (code == 401) {
                throw new AuthenticationException("The token supplied is invalid.");
            }

            System.out.println("RECEIVED PROXY IMPORT RESPONSE");
            System.out.println("=================");
            System.out.println("SUCCESS");
            System.out.println(EntityUtils.toString(httpResponse.getEntity()));
        } catch (IOException | URISyntaxException | AuthenticationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
