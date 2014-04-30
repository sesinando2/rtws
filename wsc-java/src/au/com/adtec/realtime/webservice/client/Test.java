package au.com.adtec.realtime.webservice.client;

import org.json.simple.parser.ParseException;

public class Test {
    public static void main(String... args) throws ParseException {
        WebServiceClient client = new WebServiceClient("http://localhost:8080/ws/", "admin", "admin:)");
        String token = client.requestUploadToken();
        System.out.println(token);
    }
}
