package cn.chenhuanming.octopus.config;


import cn.chenhuanming.octopus.util.StringUtils;
import org.apache.poi.util.IOUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author chenhuanming
 * Created at 2018/12/17
 */
public abstract class AbstractXMLConfigFactory extends AbstractConfigFactory {

    protected final ByteArrayInputStream is;


    public AbstractXMLConfigFactory(InputStream is) {
        try {
            this.is = new ByteArrayInputStream(IOUtils.toByteArray(is));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected void validateXML(Source source, String schemaUri) throws Exception {
        is.reset();
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        if (StringUtils.isEmpty(schemaUri)) {
            //default use xsd in this jar
            schema = schemaFactory.newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream("octopus.xsd")));
        } else {
            if (schemaUri.startsWith("http:") || schemaUri.startsWith("https:")) {
                schema = schemaFactory.newSchema(new URL(schemaUri));
            } else if (schemaUri.startsWith("file:")) {
                schema = schemaFactory.newSchema(new File(schemaUri));
            } else {
                schema = schemaFactory.newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(schemaUri)));
            }
        }
        Validator validator = schema.newValidator();
        validator.validate(source);
        is.reset();
    }

    protected String getAttribute(Node node, String name) {
        if (node == null) {
            return null;
        }
        NamedNodeMap attributes = node.getAttributes();
        Node item = attributes.getNamedItem(name);
        if (item == null) {
            return null;
        }
        return item.getNodeValue();
    }


    /**
     * xml config constant
     */
    protected interface XmlNode {

        interface Root {
            String nodeName = "Root";

            interface Attribute {
                String CLASS = "class";
            }
        }

        interface Header {
            String nodeName = "Header";

            interface Attribute {
                String NAME = "name";
                String DESCRIPTION = "description";
                String HEADER_FONT_SIZE = "header-font-size";
                String HEADER_COLOR = "header-color";
                String IS_HEADER_BOLD = "header-is-bold";
                String HEADER_FOREGROUND_COLOR = "header-foreground-color";
                String HEADER_BORDER = "header-border";
                String HEADER_BORDER_COLOR = "header-border-color";
            }
        }

        interface Field {
            String nodeName = "Field";

            interface Attribute extends Header.Attribute {
                String FONT_SIZE = "font-size";
                String COLOR = "color";
                String IS_BOLD = "is-bold";
                String FOREGROUND_COLOR = "foreground-color";
                String BORDER = "border";
                String BORDER_COLOR = "border-color";
                String WIDTH = "width";

                String DATE_FORMAT = "date-format";
                String FORMATTER = "formatter";
                String IS_BLANKABLE = "is-blankable";
                String REGEX = "regex";
                String OPTIONS = "options";
            }
        }

        interface Formatters {
            String nodeName = "Formatters";

            interface Formatter {
                String nodeName = "Formatter";

                interface Attribute {
                    String TARGET = "target";
                    String CLASS = "class";
                }
            }

            interface Attribute {
                String DATE_FORMAT = "date-format";
            }
        }
    }
}
