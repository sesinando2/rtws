package au.com.adtec.realtime.webservice.client;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Test {
    public static void main(String... args) throws ParseException {
//        WebServiceClient client = new WebServiceClient("http://localhost:8080/ws/", "admin", "admin:)");
        String jsonString = "{\"13\":\"http://ictest.adtec.com.au/ws/repo/web/download/13\",\"15\":\"http://ictest.adtec.com.au/ws/repo/web/download/15\"}";
        System.out.println(jsonString);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(jsonString);

        for (Object o : json.keySet()) {
            System.out.println(o);
            System.out.println(o.getClass().toString());
            Integer.valueOf((String) o);
            Object v = json.get(o);
            System.out.println(v);
            System.out.println(v.getClass().toString());
        }

        JSONObject message = new JSONObject();
        message.put("token", "asdasdasdasd");
        message.put("content", json);

        System.out.println(message.toJSONString().replaceAll("\\\\/", "/"));
    }
}
