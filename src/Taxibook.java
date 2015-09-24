import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by Katerina on 9/20/15.
 */
public class Taxibook {


    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ";";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {
        Taxibook parser = new Taxibook();
        String townsResponce = parser.allTowns("http://api.poputka.ua/MobileService.svc/getcities/");
        Towns towns=parser.parseTowns(townsResponce);
        FileWriter writeTowns = new FileWriter("allTowns.csv");
        FileWriter serviceWriter = new FileWriter("allTaxi.csv", true);
        FileWriter additionalServiceWriter = new FileWriter("additionalService.csv", true);
        parser.writeTowns(writeTowns, towns);
        writeTowns.flush();
        writeTowns.close();
        for (CitiesTown oneCity : towns.getCities()) {
            String responce = parser.allTaxiPostRequest("http://api.poputka.ua/MobileService.svc/getservicesbycitywithfilter/", oneCity.getCityid());
            AllTaxi allTaxi = parser.parseAllTaxi(responce);
            int counter = 0;
            for (AllTaxiServices service : allTaxi.getServices()) {
                parser.writeTaxis(serviceWriter, service, oneCity.getCityid());
                String serviceResponce = parser.servicePostRequest("http://api.poputka.ua/MobileService.svc/getservicedetails/", service.getServiceid());
                AboutOneTaxi parsedService = parser.parseOneService(serviceResponce);
                parser.writeAdditionalServices(additionalServiceWriter, parsedService);
                counter++;
                System.out.println(counter + " of additional services of " + allTaxi.getServices().size() + " are parsed");
            }
        }
        additionalServiceWriter.flush();
        additionalServiceWriter.close();
        serviceWriter.flush();
        serviceWriter.close();

    }


    public void writeTaxis(FileWriter serviceWriter, AllTaxiServices service, String id) {
        try {
            printTextValue(serviceWriter, id);
            printTextValue(serviceWriter, service.getAndroidapp());
            printTextValue(serviceWriter, service.getCommentscount());
            printTextValue(serviceWriter, service.getHasonlineorder());
            printTextValue(serviceWriter, service.getIosapp());
            printTextValue(serviceWriter, service.getNightrate());
            printTextValue(serviceWriter, service.getNightseatprice());
            printTextValue(serviceWriter, service.getPriority());
            printTextValue(serviceWriter, service.getRate());
            printTextValue(serviceWriter, service.getRating());
            printTextValue(serviceWriter, service.getSeatprice());
            printTextValue(serviceWriter, service.getServiceid());
            printTextValue(serviceWriter, service.getServicename());
            printTextValue(serviceWriter, service.getServicetypeid());
            printListValues(serviceWriter, service.getPhones());
            serviceWriter.append(NEW_LINE_SEPARATOR);
        } catch (IOException e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        }
    }


    public void writeTowns(FileWriter serviceWriter, Towns towns) {
        for (CitiesTown town: towns.getCities()) {
            try {
                printTextValue(serviceWriter, town.getCityid());
                printTextValue(serviceWriter, town.getCityname());
                printTextValue(serviceWriter, town.getLatitude());
                printTextValue(serviceWriter, town.getLongitude());
                printTextValue(serviceWriter, town.getPriority());
                printTextValue(serviceWriter, town.getTrans());
                printTextValue(serviceWriter, town.getPriority());
                serviceWriter.append(NEW_LINE_SEPARATOR);
            } catch (IOException e) {
                System.out.println("Error in CsvFileWriter !!!");
                e.printStackTrace();
            }
        }

    }

    public void writeAdditionalServices(FileWriter writer, AboutOneTaxi oneTaxi) {
        try {
            printTextValue(writer, oneTaxi.getServiceid());
            printTextValue(writer, oneTaxi.getServicetypeid());
            printTextValue(writer, oneTaxi.getServicename());
            printListValues(writer, oneTaxi.getPhones());
            printListValues(writer, oneTaxi.getWorkingcities());
            printTextValue(writer, oneTaxi.getAdditional_services());
            printTextValue(writer, oneTaxi.getRating());
            printTextValue(writer, oneTaxi.getAdditional_services());
            printTextValue(writer, oneTaxi.getAndroidapp());
            printTextValue(writer, oneTaxi.getCar_park());
            printTextValue(writer, oneTaxi.getCommentscount());
            printTextValue(writer, oneTaxi.getDaymindistance());
            printTextValue(writer, oneTaxi.getDayoutcityrate());
            printTextValue(writer, oneTaxi.getDayrate());
            printTextValue(writer, oneTaxi.getDayseatprice());
            printTextValue(writer, oneTaxi.getDescription());
            printTextValue(writer, oneTaxi.getEmail());
            printTextValue(writer, oneTaxi.getErrorcode());
            printTextValue(writer, oneTaxi.getFeatures());
            printTextValue(writer, oneTaxi.getFullday());
            printTextValue(writer, oneTaxi.getHasonlineorder());
            printTextValue(writer, oneTaxi.getIntercity());
            printTextValue(writer, oneTaxi.getInternational());
            printTextValue(writer, oneTaxi.getIosapp());
            printTextValue(writer, oneTaxi.getNightmindistance());
            printTextValue(writer, oneTaxi.getNightoutcityrate());
            printTextValue(writer, oneTaxi.getNightrate());
            printTextValue(writer, oneTaxi.getNightseatprice());
            printTextValue(writer, oneTaxi.getPayment_methods());
            printTextValue(writer, oneTaxi.getPreliminary());
            printTextValue(writer, oneTaxi.getWebsite());
            printTextValue(writer, oneTaxi.getWeekends());
            printListValues(writer, oneTaxi.getWorkingcities());

            writer.append(NEW_LINE_SEPARATOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printTextValue(FileWriter fileWriter, String value) {
        try {
            String printValue = null;
            if (value == null || value.equals("")) {
                printValue = "{{";
            } else {
                printValue = "{" + value + "{";
            }
            fileWriter.append(printValue);
            fileWriter.append(COMMA_DELIMITER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printListValues(FileWriter fileWriter, List<String> phones) {
        StringBuffer phoneResult = new StringBuffer();
        for (String phone : phones) {
            phoneResult.append(phone);
            phoneResult.append(", ");
        }
        phoneResult.delete(phoneResult.length() - 2, phoneResult.length());
        printTextValue(fileWriter, phoneResult.toString());
    }

    public String servicePostRequest(String url, String serviceId) {
        StringBuffer response = new StringBuffer();
        try {


            String postData = "{\"serviceid\":" + serviceId + "}";
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
            System.out.println("\nSending service 'POST' request to URL : " + url);
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

    public String allTaxiPostRequest(String url, String cityId) {
        StringBuffer response = new StringBuffer();
        try {


            String postData = "{\"skip\":0,\"pagesize\":900,\"servicetypeid\":1,\"cityid\":"+cityId+"}";
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
            System.out.println("\nSending taxi 'POST' request to URL : " + url);
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


    public String allTowns(String url) {
        StringBuffer response = new StringBuffer();
        try {


            String postData = "{}";
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
            System.out.println("\nSending taxi 'POST' request to URL : " + url);
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

    public Towns parseTowns(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Towns parsedTowns = mapper.readValue(text, Towns.class);
        return parsedTowns;


    }

    public AboutOneTaxi parseOneService(String text) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AboutOneTaxi parsedService = mapper.readValue(text, AboutOneTaxi.class);
        return parsedService;
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


