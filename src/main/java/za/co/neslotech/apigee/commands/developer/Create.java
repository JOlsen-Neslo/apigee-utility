package za.co.neslotech.apigee.commands.developer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import picocli.CommandLine;
import za.co.neslotech.apigee.commands.Command;
import za.co.neslotech.apigee.helpers.CommandHelper;

import javax.naming.AuthenticationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command(name = "create", description = "create a developer for an organization from a JSON file")
public class Create extends Command implements Runnable {

    @CommandLine.Option(names = {"-l", "--location"}, description = "location of the developer JSON file", required = true)
    private String location;

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

        Path path = Paths.get(location);
        File file = new File(path.toUri());

        try {
            if (!file.exists()) {
                throw new FileNotFoundException("The file location supplied cannot be found: " + file.getAbsolutePath());
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));

            if (!file.getName().endsWith(".json")) {
                throw new IllegalArgumentException("The file supplied needs to be in JSON format.");
            }

            URI createUri = URI.create(endpoint);

            HttpPost httpPost = new HttpPost(createUri);
            httpPost.setHeader("Authorization", "Basic " + trimmedToken);

            HttpEntity entity = EntityBuilder.create()
                    .setContentType(ContentType.APPLICATION_JSON)
                    .setText(jsonObject.toJSONString())
                    .build();
            httpPost.setEntity(entity);

            System.out.println("SENDING DEVELOPER CREATE REQUEST");
            System.out.println("=================");
            System.out.println("Endpoint URL: " + httpPost.getMethod() + " " + createUri.toString());

            System.out.println();
            System.out.println();

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int code = httpResponse.getStatusLine().getStatusCode();
            String response = EntityUtils.toString(httpResponse.getEntity());

            if (code == 400) {
                if (response.contains("developer.service.InvalidEmail")) {
                    throw new IllegalArgumentException("The JSON supplied is invalid. Email address " + jsonObject.get("email") + " is invalid.");
                } else if (response.contains("developer.service.CreateFieldsAbsent")) {
                    throw new IllegalArgumentException("The JSON supplied is invalid. Following elements are required: Email, User Name, First Name, Last Name.");
                }
            } else if (code == 409) {
                throw new IllegalArgumentException("Developer named " + jsonObject.get("email") + " already exists in organization");
            } else if (code == 401) {
                throw new AuthenticationException("The token supplied is invalid.");
            }

            System.out.println("RECEIVED DEVELOPER CREATE RESPONSE");
            System.out.println("=================");
            System.out.println("SUCCESS");
            System.out.println(response);
        } catch (IOException | AuthenticationException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ParseException e) {
            throw new RuntimeException("The JSON file provided cannot be parsed.", e);
        }
    }

}
