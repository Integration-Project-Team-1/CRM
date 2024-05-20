import org.testng.annotations.Test;
import crm.xmlValidation;
import static org.junit.Assert.*;
import static org.testng.AssertJUnit.assertTrue;

public class xmlValidationTest {

    @Test
    public void testValidXML() {
        String validXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><heartbeat xmlns=\"http://ehb.local\"><service>crm</service><timestamp>123456789</timestamp><status>OK</status></heartbeat>";
        assertTrue(xmlValidation.validateXML(validXML));
    }


    @Test
    public void testInvalidXML() {
        String invalidXML = "<root><child>Test</child>";
        assertFalse(xmlValidation.validateXML(invalidXML)); // deze moet false zijn om te werken omdat het niet overeenkomt met het XSD
    }
}
