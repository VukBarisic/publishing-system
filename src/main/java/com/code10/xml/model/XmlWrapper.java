package com.code10.xml.model;

import com.code10.xml.controller.exception.BadRequestException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlWrapper {

    private String xml;

    private Document dom;

    public XmlWrapper() {
    }

    public XmlWrapper(String xml) {
        this.xml = xml;
        updateDom();
    }

    public XmlWrapper(Document dom) {
        this.dom = dom;
        updateXml();
    }

    public XmlWrapper(String xml, Document dom) {
        this.xml = xml;
        this.dom = dom;
    }

    public void updateXml() {
        final DOMSource domSource = new DOMSource(dom);
        final StringWriter writer = new StringWriter();
        final StreamResult result = new StreamResult(writer);
        final TransformerFactory factory = TransformerFactory.newInstance();

        try {
            final Transformer transformer = factory.newTransformer();
            transformer.transform(domSource, result);
            xml = writer.toString();
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }

    public void updateDom() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            dom = builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Document getDom() {
        return dom;
    }

    public void setDom(Document dom) {
        this.dom = dom;
    }
}
