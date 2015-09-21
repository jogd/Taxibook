import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Katerina on 9/20/15.
 */
public class Taxibook {


    private final String USER_AGENT = "Mozilla/5.0";
    private static final int PAGE_SIZE = 50;

    public static void main(String[] args) throws Exception {
        Taxibook parser = new Taxibook();
        String responce = parser.simplePostRequest("http://api.poputka.ua/MobileService.svc/getservicesbycitywithfilter/");
        System.out.println("end");
        AllTaxi rubricList = parser.parseAllTaxi(responce);
        System.out.println(rubricList);

    }




    public String simplePostRequest(String url) {
        StringBuffer response = new StringBuffer();
        try {


            String postData = "{\"skip\":0,\"pagesize\":900,\"servicetypeid\":1,\"cityid\":0}";
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            con.setDoOutput(true);
            con.getOutputStream().write(postDataBytes);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            System.out.println("BAD REQUEST to " + url);
            e.printStackTrace();
            return null;
        }
        return response.toString();
    }


    public String postRequest(String url) {
        // configure the SSLContext with a TrustManager
        SSLContext ctx = null;
        StringBuffer response = new StringBuffer();
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);


            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

            // optional default is GET
            con.setRequestMethod("POST");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/json");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (Exception e) {
            System.out.println("BAD REQUEST to " + url);
            e.printStackTrace();
            return null;
        }


        //print result
        //        System.out.println(response.toString());
        return response.toString();
    }

    public AllTaxi parseAllTaxi(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AllTaxi parsedSite = mapper.readValue(text, AllTaxi.class);
        return parsedSite;


    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}


