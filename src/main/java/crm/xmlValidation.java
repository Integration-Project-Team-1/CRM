package crm;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class xmlValidation {

    //method to validate xml

    public static boolean validateXML(String xml) {

        String xsdPath = "src/main/resources/include.template.xsd";
        try {
            System.out.println("Creating SchemaFactory instance."); //debug
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); //instance of schemafactory for xml validation
            System.out.println("Loading schema from file: " + xsdPath); //debug
            Schema schema = factory.newSchema(new File(xsdPath)); //instance of schema by parsing the xsd file

            Validator validator=schema.newValidator();
            System.out.println("Validating XML against the schema.");  // Debug
            validator.validate(new StreamSource(new StringReader(xml))); //validating the xml against the xsd using streamsource object created from stringreader containing the xml
            System.out.println("XML is valid.");  // Debug statement
        }catch (IOException | SAXException e){
            System.out.println("XML validation failed" + e.getMessage());
            return false;
        }

        return true;
    }
}
