import org.testng.annotations.Test;
import crm.xmlValidation;
import static org.junit.Assert.*;
import static org.testng.AssertJUnit.assertTrue;

public class xmlValidationTest {

    @Test
    public void testValidXML() {
        String validXML = "<root><child>Test</child></root>";
        assertTrue(xmlValidation.validateXML(validXML));
    }

    @Test
    public void testInvalidXML() {
        String invalidXML = "<root><child>Test</child>"; // Dit XML-fragment is onvolledig en zal niet overeenkomen met het XSD-schema.
        assertFalse(xmlValidation.validateXML(invalidXML));
    }
}
