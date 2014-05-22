package au.com.adtec.realtime.webservice.client.model;

import org.json.JSONObject;

public class FileDetail {

    private String fileName;
    private String contentType;
    private long contentLength;

    public FileDetail(JSONObject json) {
        fileName = json.getString("fileName");
        contentType = json.getString("contentType");
        contentLength = json.getLong("contentLength");
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }
}
