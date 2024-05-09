import java.io.IOException;

import com.force.api.ApiConfig;
import com.force.api.ForceApi;
import com.rabbitmq.client.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;


public class Consumer {

    private final String host = "10.2.160.9";
    private final String queueName = "inschrijving_crm_queue";
    private final String exchangeName = "inschrijving_exchange";
    private final String routingKey = "inschrijving";

    private Channel channel;

    //we create a connection within the constructor
    public Consumer() {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);


        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey); //use of the routing key to bind the queue to the exchange


        } catch (Exception e) {

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





        public static void connectToSalesforce() {
            String SALESFORCE_USERNAME = "ehberasmus@gmail.com";
            String SALESFORCE_PASSWORD = "Event5431";
            String SALESFORCE_SECURITY_TOKEN = "S4lOdXADEdHNLYorrabi2mLg";
            String LOGIN_URL = "https://ehb-dev-ed.develop.my.salesforce.com";
            String CONSUMER_KEY = "3MVG9PwZx9R6_Urc1GPWYVjQmwHmXKY1pQ8t_W_Ql4VXOFeo_9tKJW3O8nLf0JJoMjrOuii6wZ8XdpCcJfOOA";
            String CONSUMER_SECRET = "BA7D5B9E3434948E1751C3C5B51BC366B8FD9165E3DCBD95A62AEF4D06B5C4C9";

            // Combineer wachtwoord en beveiligingstoken
            String loginPassword = SALESFORCE_PASSWORD + SALESFORCE_SECURITY_TOKEN;

            // Configureer de API-configuratie
            ApiConfig config = new ApiConfig()
                    .setClientId(CONSUMER_KEY)
                    .setClientSecret(CONSUMER_SECRET)
                    .setUsername(SALESFORCE_USERNAME)
                    .setPassword(loginPassword)
                    .setLoginEndpoint(LOGIN_URL);

            ForceApi api = new ForceApi(config);
//            createDeelnemer(api);
//            createBusiness(api);
//            createConsumption(api);

        }

    public static void createDeelnemer(ForceApi api) {
        Map<String, Object> deelnemerFields = new HashMap<>();
        deelnemerFields.put("Name", "marcelo");
        deelnemerFields.put("Leeftijd__c", 32);
        deelnemerFields.put("Phone__c", "0485009999");
        deelnemerFields.put("Email__c", "marcelo@gmail.com");
        deelnemerFields.put("Bedrijf__c", "real madrid");

        // Maak de Deelnemer aan in Salesforce
        api.createSObject("Deelnemer__c", deelnemerFields);
    }

    public static void createBusiness(ForceApi api) {
        Map<String, Object> businessFields = new HashMap<>();
        businessFields.put("Name", "business soso");
        businessFields.put("VAT__c", "123456999");
        businessFields.put("Email__c", "soso@bedrijf.com");
        businessFields.put("Access_Code__c", 2455);
        businessFields.put("Address__c", "sosostraat 128");

        // Maak het Business object aan in Salesforce
        api.createSObject("Business__c", businessFields);
    }

    public static void createConsumption(ForceApi api) {
        Map<String, Object> consumptionFields = new HashMap<>();
        consumptionFields.put("Timestamp__c", new Date());
        consumptionFields.put("Name", "food");
        consumptionFields.put("Products__c", "hotdog");
        consumptionFields.put("Consumer__c", "het is lekker "); // Voorbeeld UUID

        // Maak het Consumption object aan in Salesforce
        api.createSObject("Consumption__c", consumptionFields);
    }

}








