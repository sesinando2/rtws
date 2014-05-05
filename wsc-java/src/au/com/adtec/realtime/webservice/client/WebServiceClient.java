package au.com.adtec.realtime.webservice.client;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServiceClient {

    public static final String TOKEN_REVOKE_URL = "token/api/revoke";
    public static final String TOKEN_REQUEST_URL = "token/api/request";
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
        this(baseUrl);
        this.client.addFilter(new HTTPBasicAuthFilter(username, password));
    }

    public WebServiceClient(String baseUrl) {
        this.baseUrl = baseUrl;
        parser = new JSONParser();
        this.client = Client.create();
    }

    /**
     * Request an upload token from the server.
     * @return  Value of the token
     */
    public String requestUploadToken() {
        JSONObject body = new JSONObject();
        body.put("authority", "ROLE_REPO_UPLOAD");
        WebResource resource = client.resource(baseUrl + TOKEN_REQUEST_URL);
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
    public String[] requestDownloadTokens(int amount, Integer...fileIds) {
        if (amount <= 0) amount = 1;
        JSONArray ids = new JSONArray();
        for (int id : fileIds) { ids.add(id); }

        WebResource resource = client.resource(baseUrl + TOKEN_REQUEST_URL);
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
        WebResource resource = client.resource(baseUrl + TOKEN_REVOKE_URL);
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

    public String download(String token, int fileId) throws IOException {
        return download(token, fileId, null);
    }

    public String download(String token, int fileId, String fileName) throws IOException {
        FileOutputStream fos = null;
        try {
            HttpResponse<InputStream> response = Unirest.get(baseUrl + "repo/web/download/" + fileId).header("X-Auth-Token", token).asBinary();
            InputStream body = response.getBody();

            if (fileName == null) {
                fileName = getFileName(response);
                while(new File(fileName).exists()) {
                    String regexp = ".*(\\d+)\\.(\\w+)$";
                    System.out.println(fileName + ":" + regexp);
                    int number = 1;
                    if (fileName.matches(regexp)) {
                        Pattern pattern = Pattern.compile(regexp);
                        Matcher matcher = pattern.matcher(fileName);
                        if (matcher.matches() && matcher.groupCount() >= 2) {
                            System.out.println(matcher.group(1));
                            System.out.println(matcher.group(2));
                        }
                    }

                    Pattern pattern = Pattern.compile("(.*)(\\d*)\\.(\\w+)$");
                    Matcher matcher = pattern.matcher(fileName);
                    if (matcher.matches() && matcher.groupCount() >= 3) {
                        String file = matcher.group(1);
                        String extension = matcher.group(3);
                        fileName = file + number + "." + extension;
                    }
                }
            }

            System.out.println(fileName);

            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024 * 32];
            int length;
            while ((length = body.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
        } catch (UnirestException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (fos != null) fos.close();
            Unirest.shutdown();
        }
        return null;
    }

    private String getContentType(HttpResponse response) {
        Object contentType = response.getHeaders().get("content-type");
        if (isNonEmptyArrayList(contentType)) {
            return ((ArrayList) contentType).get(0).toString();
        }
        return null;
    }

    private String getFileName(HttpResponse response) {
        Object contentDisposition = response.getHeaders().get("content-disposition");
        if (isNonEmptyArrayList(contentDisposition) ) {
            String fileName = ((ArrayList) contentDisposition).get(0).toString();
            if (fileName.contains("filename=")) fileName = fileName.replace("filename=", "");
            return fileName;
        }
        return null;
    }

    private boolean isNonEmptyArrayList(Object contentType) {
        return contentType != null && contentType instanceof ArrayList && ((ArrayList) contentType).size() > 0;
    }
}
