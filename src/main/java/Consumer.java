import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.util.Base64;
import com.force.api.ApiConfig;
import com.force.api.ForceApi;
import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;


public class Consumer {

    private final String host = "10.2.160.9";
    private final String queueName = "inschrijving_crm_queue";
    private final String exchangeName = "inschrijving_exchange";
    private final String routingKey = "inschrijving";

    private Channel channel;

    //we create a connection within the constructor
    public Consumer(){

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);

        try{
            Connection connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, exchangeName,routingKey); //use of the routing key to bind the queue to the exchange



        }catch (Exception e){

            e.getMessage();
            e.printStackTrace();
        }



    }


    public void startConsuming() throws IOException {
        // instance of Defaultconsumer + create an innerclass to customize handleDelivery at instantiation
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            //callback method from rabbbitmq client that handles messages sent to the consumer
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8"); //convert byte array in string
                System.out.println(" [x] Received '" + message + "'");
            }
        };

        // start consuming messages from the queue
        channel.basicConsume(queueName, true, consumer);
        System.out.println("ready to consumer");
    }



    public static class SalesforceIntegration {
        public static void main(String[] args) {
            String loginUrl = "https://login.salesforce.com/services/oauth2/token";
            String clientId = "3MVG9PwZx9R6_Urc1GPWYVjQmwHmXKY1pQ8t_W_Ql4VXOFeo_9tKJW3O8nLf0JJoMjrOuii6wZ8XdpCcJfOOA";
            String clientSecret = "BA7D5B9E3434948E1751C3C5B51BC366B8FD9165E3DCBD95A62AEF4D06B5C4C9";
            String username = "ehberasmus@gmail.com";
            String password = "Event5431";
            String tokenUrl = "https://ehb-dev-ed.develop.my.salesforce.com/services/apexrest/Account/";

            // Step 1: Authenticate and obtain access token
            String accessToken = authenticate(loginUrl, clientId, clientSecret, username, password);

            // Step 2: Make RESTful API call to Salesforce
            if (accessToken != null) {
                createDeelnemer(tokenUrl, accessToken);
            } else {
                System.out.println("Failed to obtain access token. Authentication failed.");
            }
        }

        private static String authenticate(String loginUrl, String clientId, String clientSecret, String username, String password) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(loginUrl);
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                String credentials = clientId + ":" + clientSecret;
                String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
                httpPost.setHeader("Authorization", "Basic " + encodedCredentials);
                StringEntity params = new StringEntity("grant_type=password&username=" + username + "&password=" + password);
                httpPost.setEntity(params);

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        String responseBody = EntityUtils.toString(response.getEntity());
                        // Controleer of de reactie de verwachte structuur heeft
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.has("access_token")) {
                            return jsonResponse.getString("access_token");
                        } else {
                            System.err.println("Fout bij verkrijgen van toegangstoken. JSON-reactie van Salesforce bevat geen toegangstoken.");
                        }
                    } else {
                        System.err.println("Fout bij authenticatie. Ongeldige statuscode ontvangen: " + statusCode);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Fout bij authenticatie. Kan toegangstoken niet verkrijgen.");
            }
            return null;
        }

        private static void createDeelnemer(String tokenUrl, String accessToken) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(tokenUrl);
                httpPost.setHeader("Authorization", "Bearer " + accessToken);
                httpPost.setHeader("Content-Type", "application/json");

                // Gegevens van deelnemer
                Map<String, Object> deelnemerFields = new HashMap<>();
                deelnemerFields.put("Name", "Mike Tyson");
                deelnemerFields.put("Leeftijd__c", 25);
                deelnemerFields.put("Nummertelefoon__c", "0485009987");
                deelnemerFields.put("Email__c", "miketyson@gmail.com");
                deelnemerFields.put("Bedrijf__c", "erasmus");

                StringEntity params = new StringEntity(new JSONObject(deelnemerFields).toString());
                httpPost.setEntity(params);

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        System.out.println("Deelnemer succesvol toegevoegd aan Salesforce.");
                    } else {
                        System.out.println("Fout bij toevoegen deelnemer aan Salesforce. Status code: " + statusCode);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Fout bij toevoegen deelnemer aan Salesforce.");
            }
        }
    }
}








