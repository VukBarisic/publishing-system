package com.code10.xml.util;

import com.code10.xml.controller.exception.BadRequestException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.FileInputStream;
import java.io.StringReader;

public class XsdUtil {

    public static void validate(String xml, String path) {
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            final Schema schema = factory.newSchema(new StreamSource(new FileInputStream(path)));
            final Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("XSD validation failed!");
        }
    }
}
