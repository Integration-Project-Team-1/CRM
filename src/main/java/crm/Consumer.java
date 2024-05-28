package crm;

import com.force.api.ApiConfig;
import com.force.api.ForceApi;
import com.force.api.QueryResult;
import com.rabbitmq.client.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consumer {
    private final String CONSUMING_QUEUE = System.getenv("CONSUMING_QUEUE");
    private final String EXCHANGE = System.getenv("EXCHANGE");

    private final String ROUTINGKEY_USER = System.getenv("ROUTINGKEY_USER");
    private final String ROUTINGKEY_CONSUMPTION = System.getenv("ROUTINGKEY_CONSUMPTION");
    private final String ROUTINGKEY_BUSINESS = System.getenv("ROUTINGKEY_BUSINESS");
    private final String HOST = System.getenv("DEV_HOST");
    private final String RABBITMQ_USERNAME = System.getenv("RABBITMQ_USERNAME");
    private final String RABBITMQ_PASSWORD = System.getenv("RABBITMQ_PASSWORD");
    private final int RABBITMQ_PORT = Integer.parseInt(System.getenv("RABBITMQ_PORT"));
    private Salesforce salesforce = new Salesforce();

    private Channel channel;

    //we create a connection within the constructor
    public Consumer() throws IOException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername(RABBITMQ_USERNAME);
        factory.setPassword(RABBITMQ_PASSWORD);
        factory.setPort(RABBITMQ_PORT);

        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(CONSUMING_QUEUE, false, false, false, null);
            channel.queueBind(CONSUMING_QUEUE, EXCHANGE, ROUTINGKEY_USER);
            channel.queueBind(CONSUMING_QUEUE, EXCHANGE, ROUTINGKEY_BUSINESS);
            //channel.queueBind(CONSUMING_QUEUE, EXCHANGE, ROUTINGKEY_CONSUMPTION);


        } catch (Exception e) {

            e.getMessage();
            e.printStackTrace();
        }

        startConsuming();

    }


    public void startConsuming() throws IOException {
        // instance of Defaultconsumer + create an innerclass to customize handleDelivery at instantiation
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            //callback method from rabbbitmq client that handles messages sent to the consumer
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8"); //convert byte array in string
                System.out.println(" [x] Received '" + message + "'");
                String xsd = "src/main/resources/include.template.xsd";

                try {

                    if (message.contains("</participant>") && !message.contains("<service>crm</service>")) {
                        message.replace("xmlns=\"http://ehb.local\"","");
                        Participant participant = (Participant) unmarshalParticipant(message);
                        System.out.println(participant.toString());
                        System.out.println("participant unmarshalled");

                        if (Objects.equals(participant.getMethod(), "create")) {

                            salesforce.createDeelnemer(participant);
                            System.out.println("particpant created");

                        } else if (Objects.equals(participant.getMethod(), "update")) {
                          salesforce.updateDeelnemer(participant.getUuid(),participant);

                        }else if(Objects.equals(participant.getMethod(), "delete")){
                            salesforce.deleteDeelnemer(participant.getUuid());
                            System.out.println("particpant deleted");
                        }

                    } else if (message.contains("access_code")&& !message.contains("<service>crm</service>") ) {
                        message.replace("<business xmlns=\"http://ehb.local\">","<business>");
                        Business business1 = (Business) unmarshalBusiness(message);
                        System.out.println(business1.toString());
                        System.out.println("business unmarshalled");
                        if (Objects.equals(business1.getMethod(), "create")) {

                            salesforce.createBusiness(business1);
                            System.out.println("business created");
                        }else if(Objects.equals(business1.getMethod(), "update")){
                            salesforce.updateBusiness(business1.getUuid(),business1);
                            System.out.println("business updated");
                        }else if(Objects.equals(business1.getMethod(), "delete")){

                            salesforce.deleteBusinessByUUID(business1.getUuid());
                            System.out.println("business deleted");

                        }


                    } else if (message.contains("<consumption>")) {
                        Consumption consumption1 = (Consumption) unmarshalConsumption(message);
                        System.out.println(consumption1.toString());
                       salesforce.createConsumption(consumption1);
                        System.out.println("consumption created");
                    }


                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }

        };

        // start consuming messages from the queue
        channel.basicConsume(CONSUMING_QUEUE, true, consumer);
    }

    // Unmarshall crm.Participant-object van XML-string
    public Participant unmarshalParticipant(String xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Participant.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Participant participant = (Participant) jaxbUnmarshaller.unmarshal(inputStream);
        String dateOfBirth = extractDateOfBirthFromXml(xml);
        participant.setDateOfBirth(dateOfBirth);

        return participant;

    }

    //Unmarshall crm.Consumption-object van XML-string
    public Consumption unmarshalConsumption(String xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Consumption.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return (Consumption) jaxbUnmarshaller.unmarshal(inputStream);
    }

    // Unmarshall crm.Business-object van XML-string
    public Business unmarshalBusiness(String xml) throws JAXBException {
        System.out.println(xml);
        JAXBContext jaxbContext = JAXBContext.newInstance(Business.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        return (Business) jaxbUnmarshaller.unmarshal(inputStream);
    }

    public static String extractDateOfBirthFromXml(String xml) {
        // Regular expression to match the <date_of_birth> element and extract its value
        String regex = "<date_of_birth>(.*?)</date_of_birth>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(xml);

        // If <date_of_birth> element found, retrieve its value
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null; // Return null if date of birth not found
        }
    }


}









