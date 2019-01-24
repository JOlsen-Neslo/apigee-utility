package za.co.neslotech.apigee.helpers;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import za.co.neslotech.apigee.commands.Command;
import za.co.neslotech.apigee.commands.Login;
import za.co.neslotech.apigee.commands.product.Create;
import za.co.neslotech.apigee.commands.proxy.Delete;
import za.co.neslotech.apigee.commands.proxy.Deploy;
import za.co.neslotech.apigee.commands.proxy.Import;
import za.co.neslotech.apigee.commands.proxy.Undeploy;

public class CommandHelper {

    private static final String AUTH_ENDPOINT = "https://api.enterprise.apigee.com/v1/organizations/{org}";
    private static final String PROXY_ENDPOINT = "https://api.enterprise.apigee.com/v1/organizations/{org}/apis";
    private static final String PRODUCT_ENDPOINT = "https://api.enterprise.apigee.com/v1/organizations/{org}/apiproducts";
    private static final String DEVELOPER_ENDPOINT = "https://api.enterprise.apigee.com/v1/organizations/{org}/developers";

    public static String retrieveEndpoint(Command command, String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("The token supplied is not complete i.e. {org}:{token}");
        }

        String org = extractOrganization(token);

        String endpoint = "";
        if (command instanceof Login) {
            endpoint = AUTH_ENDPOINT;
        } else if (command instanceof Import || command instanceof Delete) {
            endpoint = PROXY_ENDPOINT;
        } else if (command instanceof Deploy || command instanceof Undeploy) {
            endpoint = AUTH_ENDPOINT;
        } else if (command instanceof Create || command instanceof za.co.neslotech.apigee.commands.product.Delete) {
            endpoint = PRODUCT_ENDPOINT;
        } else if (command instanceof za.co.neslotech.apigee.commands.developer.Create
                || command instanceof za.co.neslotech.apigee.commands.developer.Delete
                || command instanceof za.co.neslotech.apigee.commands.app.Create
                || command instanceof za.co.neslotech.apigee.commands.app.Delete) {
            endpoint = DEVELOPER_ENDPOINT;
        }

        return endpoint.replace("{org}", org);
    }

    private static String extractOrganization(String token) {
        int colonIndex = token.indexOf(":");
        if (colonIndex == -1) {
            return token;
        }

        return token.substring(0, colonIndex);
    }

    public static String extractToken(String token) {
        int colonIndex = token.indexOf(":");
        return token.substring(colonIndex + 1);
    }

    public static HttpClient createClient(String host, String port) {
        String amendedHost = host.replace("http://", "");
        return HttpClients
                .custom()
                .setProxy(new HttpHost(amendedHost, Integer.valueOf(port)))
                .build();
    }

    public static HttpClient createClient() {
        return HttpClients
                .createDefault();
    }

    public static String encodeURL(String url) {
        return url.replace(" ", "%20");
    }

}
