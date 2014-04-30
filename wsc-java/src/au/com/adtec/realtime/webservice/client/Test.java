package au.com.adtec.realtime.webservice.client;

import org.json.simple.parser.ParseException;

public class Test {
    public static void main(String... args) throws ParseException {
        WebServiceClient client = new WebServiceClient("http://localhost:8080/ws/", "admin", "admin:)");
        client.revokeToken("2ou0mv04gbkfekh337nqq0j3ks8ad3pm", "tgq6jm5vfcpss43p23snl72ed843f70e");
    }
}
