package au.com.adtec.realtime.webservice.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebServiceClient {

    private String baseUrl;

    private Client client;

    public WebServiceClient(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.client = Client.create();
        this.client.addFilter(new HTTPBasicAuthFilter(username, password));
    }

    public String requestUploadToken() throws ParseException {
        System.out.println(baseUrl + "token/api/request");
        WebResource tokenResource = client.resource(baseUrl + "token/api/request");
        tokenResource.type("application/json");
        String result = tokenResource.post(String.class, "{\"authority\":\"ROLE_REPO_UPLOAD\"}");
        JSONParser parser = new JSONParser();
        JSONArray tokens = (JSONArray) parser.parse(result);
        return (String) tokens.get(0);
    }
}
