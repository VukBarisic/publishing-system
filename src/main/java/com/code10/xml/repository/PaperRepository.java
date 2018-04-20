package com.code10.xml.repository;

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
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Component
public class PaperRepository extends XmlRepository {

    @Autowired
    public PaperRepository(XMLDocumentManager documentManager, QueryManager queryManager, DatabaseClient databaseClient) {
        super(documentManager, queryManager, databaseClient);
        collectionRef = "papers";
    }

    public String create(XmlWrapper wrapper) {
        final String id = UUID.randomUUID().toString();
        final Element element = wrapper.getDom().getDocumentElement();
        element.setAttribute("pp:id", id);

        wrapper.updateXml();
        write(id, wrapper);
        return id;
    }

    public List<XmlWrapper> findByCreator(String username) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(username, RdfConstants.CREATED, null))
                .makeQuery(queryManager, RdfConstants.PAPER_GRAPH_URI);

        return findBySparql(queryManager, query, false);
    }

    public List<XmlWrapper> findSubmitted() {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.SUBMITTED, null))
                .makeQuery(queryManager, RdfConstants.PAPER_GRAPH_URI);

        return findBySparql(queryManager, query, false);
    }

    public String findUsernameByPaper(String paperId) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.SUBMITTED, paperId))
                .makeQuery(queryManager, RdfConstants.PAPER_GRAPH_URI);

        final List<RdfTriple> triples = findTriples(queryManager, query);
        return triples.size() > 0 ? triples.get(0).getSubject() : "";
    }

    public List<String> findAuthorsByPaper(String paperId) {
        final HashSet<String> usernames = new HashSet<>();
        final XmlWrapper paper = findById(paperId);

        final NodeList authors = (NodeList) XPathUtil.evaluate("/paper/authors/*", paper.getDom(), XPathConstants.NODESET);

        for (int i = 0; i < authors.getLength(); i++) {
            final Element author = (Element) authors.item(i);
            usernames.add(author.getAttribute("username"));
        }

        usernames.add(findUsernameByPaper(paperId));

        return new ArrayList<>(usernames);
    }

    public List<XmlWrapper> findAssigned(String username) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(username, RdfConstants.ASSIGNED_TO, null))
                .makeQuery(queryManager, RdfConstants.REVIEW_GRAPH_URI);

        return findBySparql(queryManager, query, false);
    }

    public List<XmlWrapper> findAccepted(String username) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(username, RdfConstants.REVIEWING, null))
                .makeQuery(queryManager, RdfConstants.REVIEW_GRAPH_URI);

        return findBySparql(queryManager, query, false);
    }

    public List<XmlWrapper> findPublishedByCreator(String username) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(username, RdfConstants.CREATED, null))
                .and(new RdfTriple(username, RdfConstants.PUBLISHED, null))
                .makeQuery(queryManager, RdfConstants.PAPER_GRAPH_URI);

        return findBySparql(queryManager, query, false);
    }

    public List<XmlWrapper> findPublished() {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.PUBLISHED, null))
                .makeQuery("?o", queryManager, RdfConstants.PAPER_GRAPH_URI);

        return findBySparql(queryManager, query, false);
    }

    public List<XmlWrapper> findByMetadata(RdfQueryBuilder builder) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = builder.makeQuery(queryManager, RdfConstants.PAPER_GRAPH_URI);

        return findBySparql(queryManager, query, true);
    }

    public boolean isPublished(String id) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = queryManager
                .newQueryDefinition("ASK WHERE { ?s ?p ?o }")
                .withBinding("p", RdfConstants.PUBLISHED)
                .withBinding("o", id);

        return queryManager.executeAsk(query);
    }

    public boolean isAuthorOfPaper(String username, String paperId) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = queryManager
                .newQueryDefinition("ASK WHERE { ?s ?p ?o }")
                .withBinding("s", username)
                .withBinding("p", RdfConstants.SUBMITTED)
                .withBinding("o", paperId);

        return queryManager.executeAsk(query);
    }

    public List<XmlWrapper> findPapersByUserAndKeyword(String username, String keyword) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();
        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.HAS_KEYWORD, keyword.toLowerCase()))
                .and(new RdfTriple(username, RdfConstants.PUBLISHED, null), null, null, "s")
                .makeQuery(queryManager, RdfConstants.PAPER_GRAPH_URI);

        return findBySparql(queryManager, query, true);
    }
}
