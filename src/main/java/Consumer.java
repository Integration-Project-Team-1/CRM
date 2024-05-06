import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;


import com.force.api.ApiConfig;
import com.force.api.ForceApi;
import com.rabbitmq.client.*;
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




            public static class connectToSalesforceAndSendData() {

                // Inloggegevens Salesforce
                private static final String USERNAME = "ehberasmus@gmail.com";
                private static final String PASSWORD = "Event5431";
                private static final String SECURITY_TOKEN = "H1ODJnJb4guxN7Lwq5vIWdpWH";
                private static final String CONSUMER_KEY = "3MVG9PwZx9R6_Urc1GPWYVjQmwHmXKY1pQ8t_W_Ql4VXOFeo_9tKJW3O8nLf0JJoMjrOuii6wZ8XdpCcJfOOA";
                private static final String CONSUMER_SECRET = "BA7D5B9E3434948E1751C3C5B51BC366B8FD9165E3DCBD95A62AEF4D06B5C4C9";
                private static final String INSTANCE_URL = "https://ehb-dev-ed.develop.my.salesforce.com";

                public static void main(String[] args) {
                    SalesforceConnection salesforceConnection = new SalesforceConnection();
                    salesforceConnection.connectAndSendData();
                }

                static class SalesforceConnection {

                    public void connectAndSendData() {
                        // Inloggen bij Salesforce
                        ApiConfig config = new ApiConfig()
                                .setUsername(USERNAME)
                                .setPassword(PASSWORD)
                                .setClientId(CONSUMER_KEY)
                                .setClientSecret(CONSUMER_SECRET)
                                .setSecurityToken(SECURITY_TOKEN)
                                .setLoginEndpoint(INSTANCE_URL);

                        ForceApi api = new ForceApi(config);

                        // Gegevens van deelnemer toevoegen
                        Map<String, Object> deelnemerFields = new HashMap<>();
                        deelnemerFields.put("Name", "Mike Tyson");
                        deelnemerFields.put("Leeftijd__c", 25);
                        deelnemerFields.put("Nummertelefoon__c", "0485009987");
                        deelnemerFields.put("Email__c", "miketyson@gmail.com");
                        deelnemerFields.put("Bedrijf__c", "erasmus");

                        // Nieuwe deelnemer toevoegen aan Salesforce
                        try {
                            api.createSObject("Deelnemer__c", deelnemerFields);
                            System.out.println("Deelnemer succesvol toegevoegd aan Salesforce.");
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Fout bij toevoegen deelnemer aan Salesforce.");
                        }
                    }
                }
            }
}








