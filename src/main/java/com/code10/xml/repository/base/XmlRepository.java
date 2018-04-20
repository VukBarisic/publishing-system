package com.code10.xml.repository.base;

import com.code10.xml.model.RdfTriple;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.util.RdfUtil;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public abstract class XmlRepository {

    protected final XMLDocumentManager documentManager;

    protected final QueryManager queryManager;

    protected final DatabaseClient databaseClient;

    protected String collectionRef;

    @Autowired
    public XmlRepository(XMLDocumentManager documentManager, QueryManager queryManager, DatabaseClient databaseClient) {
        this.documentManager = documentManager;
        this.queryManager = queryManager;
        this.databaseClient = databaseClient;
    }

    public String create(XmlWrapper wrapper) {
        final String id = UUID.randomUUID().toString();
        final Element element = wrapper.getDom().getDocumentElement();
        element.setAttribute("id", id);

        wrapper.updateXml();
        write(id, wrapper);
        return id;
    }

    public String update(String id, XmlWrapper wrapper) {
        write(id, wrapper);
        return id;
    }

    public XmlWrapper findById(String id) {
        final StringHandle handle = new StringHandle();
        final StringHandle result = documentManager.read(getDocumentName(id), handle);

        return new XmlWrapper(result.get());
    }

    public List<XmlWrapper> findAll() {
        final StructuredQueryBuilder builder = queryManager.newStructuredQueryBuilder();
        final StructuredQueryDefinition criteria = builder.collection(String.format("%s", collectionRef));

        final SearchHandle result = new SearchHandle();
        queryManager.search(criteria, result);

        final List<XmlWrapper> results = new ArrayList<>();
        Arrays.stream(result.getMatchResults()).forEach(match -> results.add(findByPath(match.getUri())));

        return results;
    }

    public void delete(String id) {
        documentManager.delete(getDocumentName(id));
    }

    public XmlWrapper findByPath(String path) {
        final StringHandle handle = new StringHandle();
        final StringHandle result = documentManager.read(path, handle);

        return new XmlWrapper(result.get());
    }

    public boolean exists(String id) {
        return documentManager.exists(getDocumentName(id)) != null;
    }

    public void writeMetadata(RdfTriple triple) {
        RdfUtil.writeMetadata(triple, databaseClient, String.format("%s/metadata", collectionRef));
    }

    public void removeMetadata(String s, String p, String o, String graphUri) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = queryManager
                .newQueryDefinition("DELETE WHERE { GRAPH <" + graphUri + "> { ?s ?p ?o } }");

        if (!TextUtils.isEmpty(s)) {
            query.withBinding("s", s);
        }
        if (!TextUtils.isEmpty(p)) {
            query.withBinding("p", p);
        }
        if (!TextUtils.isEmpty(o)) {
            query.withBinding("o", o);
        }

        queryManager.executeUpdate(query);
    }

    protected void write(String id, XmlWrapper wrapper) {
        final DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.getCollections().add(String.format("%s", collectionRef));
        final InputStreamHandle handle = new InputStreamHandle(new ByteArrayInputStream(wrapper.getXml().getBytes()));
        documentManager.write(getDocumentName(id), metadata, handle);
    }

    protected List<XmlWrapper> findBySparql(SPARQLQueryManager queryManager, SPARQLQueryDefinition query, boolean bySubject) {
        JacksonHandle resultsHandle = new JacksonHandle();
        resultsHandle.setMimetype(SPARQLMimeTypes.SPARQL_JSON);

        resultsHandle = queryManager.executeSelect(query, resultsHandle);
        final List<XmlWrapper> results = new ArrayList<>();
        RdfUtil.parseResults(resultsHandle)
                .forEach(triple -> results.add(findById(bySubject ? triple.getSubject() : triple.getObject())));

        return results;
    }

    protected List<RdfTriple> findTriples(SPARQLQueryManager queryManager, SPARQLQueryDefinition query) {
        JacksonHandle resultsHandle = new JacksonHandle();
        resultsHandle.setMimetype(SPARQLMimeTypes.SPARQL_JSON);

        resultsHandle = queryManager.executeSelect(query, resultsHandle);

        return RdfUtil.parseResults(resultsHandle);
    }

    public List<XmlWrapper> findByText(String text) {
        final StringQueryDefinition query = queryManager.newStringDefinition();
        query.setCollections(collectionRef);
        query.setCriteria(text);

        final SearchHandle result = new SearchHandle();
        queryManager.search(query, result);
        final List<XmlWrapper> results = new ArrayList<>();
        Arrays.stream(result.getMatchResults()).forEach(match -> results.add(findByPath(match.getUri())));

        return results;
    }

    protected String getDocumentName(String id) {
        return String.format("%s/%s.xml", collectionRef, id);
    }
}
