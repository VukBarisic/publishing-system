package com.code10.xml.util;

import com.code10.xml.controller.exception.BadRequestException;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XPathUtil {

    public static Object evaluate(String expression, Document document, QName returnType) {
        final XPathFactory xPathFactory = XPathFactory.newInstance();
        final XPath xPath = xPathFactory.newXPath();

        try {
            final XPathExpression xPathExpression = xPath.compile(expression);
            return xPathExpression.evaluate(document, returnType);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }
}
