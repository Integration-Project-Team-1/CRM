package crm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;

import static crm.xmlValidation.validateXML;

public class Rabbitmq {

    private static final String HOST = System.getenv("DEV_HOST");
    private static final String RABBITMQ_USERNAME = System.getenv("RABBITMQ_USERNAME");
    private static final String RABBITMQ_PASSWORD = System.getenv("RABBITMQ_PASSWORD");
    private static final int RABBITMQ_PORT = Integer.parseInt(System.getenv("RABBITMQ_PORT"));
    private static String EXCHANGE = System.getenv("EXCHANGE");
    private static String ROUTINGKEY = System.getenv("ROUTINGKEY");
    private static String ROUTINGKEY_BUSINESS = System.getenv("ROUTINGKEY_BUSINESS");

    private static Connection connection;
    private static Channel channel;

    // Static block to initialize the connection and channel once
    static {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setUsername(RABBITMQ_USERNAME);
            factory.setPassword(RABBITMQ_PASSWORD);
            factory.setPort(RABBITMQ_PORT);

            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize RabbitMQ connection and channel: " + e.getMessage());
        }
    }

    public static void sendToExchange(String message) {
        if (!validateXML(message)) {
            System.out.println("XML validation failed. participant not sent");
            return; // if validation fails the method stops and participant is not sent
        }

        if (channel == null) {
            System.err.println("RabbitMQ channel is not initialized. Message not sent.");
            return;
        }

        try {
            // Publish the message to the topic exchange with the specified routing key
            channel.basicPublish(EXCHANGE, ROUTINGKEY, null, message.getBytes());
            System.out.println(" [x] Sent '" + message);
            sendLog(EXCHANGE,ROUTINGKEY,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void sendBusinessToExchange(String message) {
        if (!validateXML(message)) {
            System.out.println("XML validation failed. participant not sent");
            return; // if validation fails the method stops and participant is not sent
        }

        if (channel == null) {
            System.err.println("RabbitMQ channel is not initialized. Message not sent.");
            return;
        }

        try {
            // Publish the message to the topic exchange with the specified routing key
            channel.basicPublish(EXCHANGE, ROUTINGKEY_BUSINESS, null, message.getBytes());
            System.out.println(" [x] Sent '" + message);
            sendLog(EXCHANGE,ROUTINGKEY_BUSINESS,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void sendLog(String exchange,String routingKey,String innerXmlData) {

        String logType = "rabbitmq";
        String service = "crm";
        String message = "<log>" +
                "<timestamp>" + (int) (Instant.now().getEpochSecond()) + "</timestamp>" +
                "<log_type>" + logType + "</log_type>" +
                "<sender>" + service + "</sender>" +
                "<exchange>" + exchange + "</exchange>" +
                "<routing_key>" + routingKey + "</routing_key>" +
                innerXmlData +
                "</log>";

        try {

            channel.basicPublish("", "logging_queue", null, message.getBytes("UTF-8"));
            System.out.println("log sent to monitoring");

        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("logging could not be sent: " + e.getMessage());
        }

    }

    public static Channel getChannel() {
        return channel;
    }
}
