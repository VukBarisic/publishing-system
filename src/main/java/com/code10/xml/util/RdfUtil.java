package com.code10.xml.util;

import com.code10.xml.controller.exception.BadRequestException;
import com.code10.xml.model.RdfTriple;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RdfUtil {

    @Value("classpath:xsl/grddl.xsl")
    private static Resource xsltFile;

    public static void extractMetadata(InputStream in, OutputStream out) {
        final TransformerFactory factory = TransformerFactory.newInstance();
        final StreamSource source = new StreamSource(in);
        final StreamResult result = new StreamResult(out);

        try {
            final StreamSource transformSource = new StreamSource(new File(xsltFile.getURI().getPath()));
            final Transformer transformer = factory.newTransformer(transformSource);

            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }

    public static void writeMetadata(RdfTriple triple, DatabaseClient databaseClient, String graphURI) {
        final GraphManager graphManager = databaseClient.newGraphManager();
        graphManager.setDefaultMimetype(RDFMimeTypes.RDFXML);
        final StringHandle handle = new StringHandle().with(triple.toString()).withMimetype(RDFMimeTypes.NTRIPLES);
        graphManager.merge(graphURI, handle);
    }

    public static List<RdfTriple> parseResults(JacksonHandle resultsHandle) {
        final JsonNode triples = resultsHandle.get().path("results").path("bindings");
        final List<RdfTriple> list = new ArrayList<>();

        for (JsonNode row : triples) {
            final String subject = row.path("s").path("value").asText();
            final String predicate = row.path("p").path("value").asText();
            final String object = row.path("o").path("value").asText();

            list.add(new RdfTriple(subject, predicate, object));
        }

        return list;
    }
}
