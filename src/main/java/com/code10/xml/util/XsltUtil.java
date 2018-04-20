package com.code10.xml.util;

import com.code10.xml.controller.exception.BadRequestException;
import com.code10.xml.model.XmlWrapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class XsltUtil {

    public static String transform(XmlWrapper wrapper, Resource xsl) {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final StringWriter writer = new StringWriter();
        final DOMSource source = new DOMSource(wrapper.getDom());
        final StreamResult result = new StreamResult(writer);

        try {
            final StreamSource transformSource = new StreamSource(xsl.getFile());
            final Transformer transformer = factory.newTransformer(transformSource);

            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.transform(source, result);

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }

    public static InputStreamResource toPdf(XmlWrapper wrapper, Resource xsl) {
        final Document document = new Document();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PdfWriter writer;

        try {
            writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(transform(wrapper, xsl).getBytes(StandardCharsets.UTF_8)));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();

        return new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));
    }
}
