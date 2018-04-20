package com.code10.xml.repository;

import com.code10.xml.controller.exception.NotFoundException;
import com.code10.xml.model.RdfTriple;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.RdfConstants;
import com.code10.xml.repository.base.XmlRepository;
import com.code10.xml.util.XPathUtil;
import com.code10.xml.util.search.RdfQueryBuilder;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import java.util.List;
import java.util.UUID;

@Component
public class UserRepository extends XmlRepository {

    @Autowired
    public UserRepository(XMLDocumentManager documentManager, QueryManager queryManager, DatabaseClient databaseClient) {
        super(documentManager, queryManager, databaseClient);
        collectionRef = "users";
    }

    public String create(XmlWrapper wrapper) {
        final String id = UUID.randomUUID().toString();
        final Element element = wrapper.getDom().getDocumentElement();
        element.setAttribute("id", id);
        wrapper.updateXml();

        final String username = ((Node) XPathUtil.evaluate("/user/username", wrapper.getDom(), XPathConstants.NODE)).getTextContent();
        write(username, wrapper);
        return id;
    }

    public boolean isAssignedTo(String username, String paperId) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = queryManager
                .newQueryDefinition("ASK WHERE { ?s ?p ?o }")
                .withBinding("s", username)
                .withBinding("p", RdfConstants.ASSIGNED_TO)
                .withBinding("o", paperId);

        return queryManager.executeAsk(query);
    }

    public boolean isReviewing(String username, String paperId) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = queryManager
                .newQueryDefinition("ASK WHERE { ?s ?p ?o }")
                .withBinding("s", username)
                .withBinding("p", RdfConstants.REVIEWING)
                .withBinding("o", paperId);

        return queryManager.executeAsk(query);
    }

    public XmlWrapper findByPaperId(String paperId) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();
        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.CREATED, paperId))
                .makeQuery(queryManager, RdfConstants.PAPER_GRAPH_URI);

        final List<XmlWrapper> users = findBySparql(queryManager, query, true);

        if (users.size() == 0) {
            throw new NotFoundException("Paper not found");
        }

        return users.get(0);
    }

    public List<XmlWrapper> findReviewingByPaper(String paperId) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();
        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.REVIEWING, paperId))
                .makeQuery(queryManager, RdfConstants.REVIEW_GRAPH_URI);

        return findBySparql(queryManager, query, true);
    }

    public List<XmlWrapper> findReviewedByPaper(String paperId) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();
        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.REVIEWED, paperId))
                .makeQuery(queryManager, RdfConstants.REVIEW_GRAPH_URI);

        return findBySparql(queryManager, query, true);
    }

    public List<XmlWrapper> findAssignedByPaper(String paperId) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.ASSIGNED_TO, paperId))
                .makeQuery(queryManager, RdfConstants.REVIEW_GRAPH_URI);

        return findBySparql(queryManager, query, true);
    }
}
