import com.rabbitmq.client.Envelope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import crm.Consumer;

public class ConsumerTest {
    private Consumer consumer;

    @BeforeEach
    public void setup() throws IOException {
        consumer = new Consumer();
    }

    @Test
    public void testHandleDelivery_ParticipantCreate() throws IOException, JAXBException {
        String participantXml = "<participant><uuid>123</uuid><method>create</method></participant>";
        consumer.handleDelivery("tag", new Envelope(), null, participantXml.getBytes());

    }

    @Test
    public void testHandleDelivery_ParticipantUpdate() throws IOException, JAXBException {
        String participantXml = "<participant><uuid>123</uuid><method>update</method></participant>";
        consumer.handleDelivery("tag", new Envelope(), null, participantXml.getBytes());

    }

    @Test
    public void testHandleDelivery_ParticipantDelete() throws IOException, JAXBException {
        String participantXml = "<participant><uuid>123</uuid><method>delete</method></participant>";
        consumer.handleDelivery("tag", new Envelope(), null, participantXml.getBytes());

    }


}
