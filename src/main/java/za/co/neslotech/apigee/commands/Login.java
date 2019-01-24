package za.co.neslotech.apigee.commands;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import za.co.neslotech.apigee.helpers.CommandHelper;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;

@CommandLine.Command(name = "login", description = "login to the Apigee API")
public class Login extends Command implements Runnable {

    @Option(names = {"-o", "--org"}, description = "organization to access", required = true)
    private String organization;

    @Option(names = {"-e", "--email"}, description = "email address to login with", required = true)
    private String email;

    @Option(names = {"-p", "--password"}, description = "password to login with", required = true, interactive = true)
    private String password;

    @Override
    public void run() {
        String endpoint = CommandHelper.retrieveEndpoint(this, organization);

        HttpClient httpClient = CommandHelper.createClient();
        if (host != null) {
            httpClient = CommandHelper.createClient(host, port);
        }

        String passphrase = email + ":" + password;
        String encodedPassphrase = Base64.getEncoder().encodeToString(passphrase.getBytes());

        URI authUri = URI.create(endpoint);
        HttpGet httpGet = new HttpGet(authUri);
        httpGet.addHeader("Authorization", "Basic " + encodedPassphrase);

        System.out.println("SENDING LOGIN REQUEST");
        System.out.println("=================");
        System.out.println("Endpoint URL: " + httpGet.getMethod() + " " + authUri.toString());

        System.out.println();
        System.out.println();

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int code = httpResponse.getStatusLine().getStatusCode();

            System.out.println("RECEIVED LOGIN RESPONSE");
            System.out.println("=================");

            if (code == 401 || code == 403) {
                throw new AuthenticationException("The email address and password does not exist in organization " + organization);
            }

            System.out.println("SUCCESS");
            System.out.println("AUTHENTICATED TOKEN: " + organization + ":" + encodedPassphrase);
            System.out.println("Please make sure you include the full token in future requests i.e. {org}:{token}");
        } catch (IOException | AuthenticationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
