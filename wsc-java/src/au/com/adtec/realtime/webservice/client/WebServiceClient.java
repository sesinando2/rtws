package au.com.adtec.realtime.webservice.client;

import au.com.adtec.realtime.webservice.client.model.FileDetail;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServiceClient {

    public static final String TOKEN_REVOKE_URL = "token/api/revoke";
    public static final String TOKEN_REQUEST_URL = "token/api/request";
    public static final String REQUEST_TRACKED_TOKEN_URL = TOKEN_REQUEST_URL + "/tracked/download";
    public static final String REPO_DETAILS_URL = "repo/api/details";
    public static final String REPO_DOWNLOAD_URL = "repo/web/download/";

    private static Log log = LogFactory.getLog(WebServiceClient.class);

    private String baseUrl;

    private String username;
    private String password;

    /**
     * Client for Real Time Web Service
     * @param baseUrl   URL where the web service is deployed. Should end with a '/'.
     * @param username  Username to access the web service. Make sure to use the admin user.
     * @param password  Password for the admin user.
     */
    public WebServiceClient(String baseUrl, String username, String password) {
        this(baseUrl);
        this.username = username;
        this.password = password;
    }

    public WebServiceClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Request an upload token from the server.
     * @return  Value of the token
     */
    public String requestUploadToken() {
        JSONObject body = new JSONObject();
        body.put("authority", "ROLE_REPO_UPLOAD");
        try {
            HttpResponse<JsonNode> response = Unirest.post(baseUrl + TOKEN_REQUEST_URL).basicAuth(username, password).body(body.toString()).asJson();
            return (String) response.getBody().getArray().get(0);
        } catch (UnirestException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Request download tokens for a particular resource in the server.
     * @param amount        Amount of token to generate.
     * @param accessCount   Number of times the token can be used to access the resource.
     * @param fileIds       ID's of the resource files.
     * @return              Array of generated tokens.
     */
    public String[] requestDownloadTokens(int amount, int accessCount, Integer... fileIds) {
        if (amount <= 0) amount = 1;
        JSONArray idJsonArray = new JSONArray(fileIds);
        JSONObject body = new JSONObject();
        body.put("authority", "ROLE_REPO_READ").put("amount", amount).put("accessCount", accessCount).put("id", idJsonArray);
        try {
            HttpResponse<JsonNode> response = Unirest.post(baseUrl + TOKEN_REQUEST_URL).basicAuth(username, password).body(body.toString()).asJson();
            JSONArray tokensJsonArray = response.getBody().getArray();
            String[] tokensArray = new String[tokensJsonArray.length()];
            for (int i = 0; i < tokensJsonArray.length(); i++) tokensArray[i] = (String) tokensJsonArray.get(i);
            return tokensArray;
        } catch (UnirestException e) {
            log.error(e);
        }
        return null;
    }

    public JsonNode requestTrackedDownloadToken(int incidentId, int instanceId, int messageType, String messageContent,
                                                Integer downloadCount,
                                                Integer readCount,
                                                Integer responseCount,
                                                Integer[] memberIds,
                                                Integer[] fileIds) {
        try {
            HttpResponse<JsonNode> response = Unirest.post(baseUrl + REQUEST_TRACKED_TOKEN_URL).basicAuth(username, password).
                    field("incidentId", String.valueOf(incidentId)).
                    field("instanceId", String.valueOf(instanceId)).
                    field("messageType", String.valueOf(messageType)).
                    field("messageContent", messageContent).
                    field("downloadCount", downloadCount.toString()).
                    field("readCount", readCount.toString()).
                    field("responseCount", responseCount.toString()).
                    field("membersIdCsv", StringUtils.join(memberIds, ",")).
                    field("fileIdCsv", StringUtils.join(fileIds, ",")).
                    asJson();
            return response.getBody();
        } catch (UnirestException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Revoke tokens
     * @param tokens Tokens to revoke
     * @return  Whether the tokens has been successfully revoked of not.
     */
    public boolean revokeToken(String...tokens) {
        JSONArray tokenJSON = new JSONArray(tokens);

        try {
            HttpResponse<JsonNode> response = Unirest.post(baseUrl + TOKEN_REVOKE_URL).basicAuth(username, password).body(tokenJSON.toString()).asJson();
            JSONObject result = response.getBody().getObject();
            return (Boolean) result.get("success");
        } catch (UnirestException e) {
            log.error(e);
        }
        return false;
    }

    public Collection<FileDetail> getFileDetails(int... fileIds) throws IOException {
        List<String> fileIdStringList = new ArrayList<String>();
        for (int id : fileIds) fileIdStringList.add(String.valueOf(id));
        String url = MessageFormat.format("{0}{1}/{2}", baseUrl, REPO_DETAILS_URL, StringUtils.join(fileIdStringList, ","));
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(url).basicAuth(username, password).asJson();
            JSONObject response = jsonResponse.getBody().getObject();
            Collection<FileDetail> detailsList = new ArrayList<FileDetail>();
            for (Object key : response.keySet()) detailsList.add(new FileDetail((JSONObject) response.get((String) key)));
            return detailsList;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject addCannedMessage(int incidentId, int instanceId, int fromAgentId, int fromMemberId, String messageContent, String cannedResponse, int responseTypeId, int tokenCount, int accessCount) throws IOException {
        String url = MessageFormat.format("{0}{1}", baseUrl, "message/api/canned/add");
        try {
            HttpResponse<JsonNode> response = Unirest.post(url).basicAuth(username, password).field("incidentId", incidentId).
                    field("instanceId", String.valueOf(instanceId)).field("fromAgentId", String.valueOf(fromAgentId)).
                    field("fromMemberId", String.valueOf(fromMemberId)).field("messageContent", String.valueOf(messageContent)).
                    field("messageType", String.valueOf(5)).field("responseTypeId", String.valueOf(responseTypeId)).
                    field("tokenCount", String.valueOf(tokenCount)).field("accessCount", String.valueOf(accessCount)).
                    field("responseCount", "1").field("response", cannedResponse).asJson();
            return response.getBody().getObject();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject addCannedMessage(int incidentId, int instanceId, int fromAgentId, int fromMemberId, String messageContent, String cannedResponse, int responseTypeId) throws IOException {
        return addCannedMessage(incidentId, instanceId, fromAgentId, fromMemberId, messageContent, cannedResponse, responseTypeId, 1, 0);
    }

    public File download(String token, int fileId) throws IOException {
        return download(token, fileId, null);
    }

    public File download(String token, int fileId, String fileName) throws IOException {
        FileOutputStream fos = null;
        try {
            HttpResponse<InputStream> response = Unirest.get(baseUrl + REPO_DOWNLOAD_URL + fileId).header("X-Auth-Token", token).asBinary();
            InputStream body = response.getBody();

            if (fileName == null) fileName = getFileName(response);
            fileName = generateFileName(fileName);

            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024 * 32];
            int length;
            while ((length = body.read(buffer)) > 0) fos.write(buffer, 0, length);
            fos.flush();
            return file;
        } catch (UnirestException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (fos != null) fos.close();
        }
    }

    private String generateFileName(String fileName) {
        if (fileName == null) fileName = "temp.tmp";
        while (new File(fileName).exists()) {
            if (fileName == null) fileName = "temp";

            String name = null;
            int number = 1;
            String ext = null;

            Pattern pattern = Pattern.compile("(.+)\\.(\\w+?)$");
            Matcher matcher = pattern.matcher(fileName);

            if (matcher.matches()) {
                name = matcher.group(1);
                ext = matcher.group(2);
                number = 1;

                pattern = Pattern.compile("(.*?)(\\d+)");
                matcher = pattern.matcher(name);

                if (matcher.matches()) {
                    name = matcher.group(1);
                    number = Integer.parseInt(matcher.group(2)) + 1;
                }
            }

            fileName = name + number + "." + ext;
        }
        return fileName;
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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Unirest.shutdown();
    }
}
