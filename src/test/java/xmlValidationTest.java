import org.testng.annotations.Test;
import crm.xmlValidation;
import static org.junit.Assert.*;
import static org.testng.AssertJUnit.assertTrue;

public class xmlValidationTest {

   // @Test
    //    public void testValidXML() {
    //        String validXML = "<root><child>Test</child></root>";
    //        assertTrue(xmlValidation.validateXML(validXML)); // deze moet werken omdat het wel overeenkomt met XSD
    //    }

    @Test
    public void testInvalidXML() {
        String invalidXML = "<root><child>Test</child>";
        assertFalse(xmlValidation.validateXML(invalidXML)); // deze moet false zijn om te werken omdat het niet overeenkomt met het XSD
    }
}
