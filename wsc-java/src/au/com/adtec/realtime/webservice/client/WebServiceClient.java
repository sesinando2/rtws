package au.com.adtec.realtime.webservice.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Arrays;
import java.util.List;

public class WebServiceClient {

    private String baseUrl;

    JSONParser parser;
    private Client client;

    /**
     * Client for Real Time Web Service
     * @param baseUrl   URL where the web service is deployed. Should end with a '/'.
     * @param username  Username to access the web service. Make sure to use the admin user.
     * @param password  Password for the admin user.
     */
    public WebServiceClient(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        parser = new JSONParser();
        this.client = Client.create();
        this.client.addFilter(new HTTPBasicAuthFilter(username, password));
    }

    /**
     * Request an upload token from the server.
     * @return  Value of the token
     */
    public String requestUploadToken() {
        JSONObject body = new JSONObject();
        body.put("authority", "ROLE_REPO_UPLOAD");
        WebResource resource = client.resource(baseUrl + "token/api/request");
        resource.type("application/json");
        String response = resource.post(String.class, body.toJSONString());
        try {
            JSONArray tokens = (JSONArray) parser.parse(response);
            return (String) tokens.get(0);
        } catch (ParseException e) {
            System.out.println("Exception on WebServiceClient.requestUploadToken while trying to parse response: " + response);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Request download tokens for a particular resource in the server.
     * @param amount    Amount of token to generate.
     * @param fileIds   Ids of the resource file.
     * @return  Array of the generated tokens.
     */
    public String[] requestDownloadTokens(int amount, int...fileIds) {
        if (amount <= 0) amount = 1;
        JSONArray ids = new JSONArray();
        for (int id : fileIds) { ids.add(id); }

        WebResource resource = client.resource(baseUrl + "token/api/request");
        resource.type("application/json");
        String response = resource.post(String.class, "{\"authority\":\"ROLE_REPO_READ\",\"amount\":" + amount + ",\"id\":" + ids.toJSONString() + "}");
        try {
            JSONArray tokens = (JSONArray) parser.parse(response);
            String[] tokensArray = new String[tokens.size()];
            tokens.toArray(tokensArray);
            return tokensArray;
        } catch (ParseException e) {
            System.out.println("Exception on WebServiceClient.requestUploadToken while trying to parse response: " + response);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Revoke tokens
     * @param tokens Tokens to revoke
     * @return  Whether the tokens has been successfully revoked of not.
     */
    public boolean revokeToken(String...tokens) {
        JSONArray tokensJSON = new JSONArray();
        for (String token : tokens) { tokensJSON.add(token); }
        WebResource resource = client.resource(baseUrl + "token/api/revoke");
        resource.type("application/json");
        String response = resource.post(String.class, tokensJSON.toJSONString());
        try {
            JSONObject jsonResult = (JSONObject) parser.parse(response);
            return (Boolean) jsonResult.get("success");
        } catch (ParseException e) {
            System.out.println("Exception on WebServiceClient.revokeToken while trying to parse response: " + response);
            e.printStackTrace();
        }
        return false;
    }
}
