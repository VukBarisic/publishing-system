package com.code10.xml.repository;

import com.code10.xml.model.RdfTriple;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.RdfConstants;
import com.code10.xml.repository.base.XmlRepository;
import com.code10.xml.util.search.RdfQueryBuilder;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewRepository extends XmlRepository {

    @Autowired
    public ReviewRepository(XMLDocumentManager documentManager, QueryManager queryManager, DatabaseClient databaseClient) {
        super(documentManager, queryManager, databaseClient);
        collectionRef = "reviews";
    }

    public List<XmlWrapper> findByPaper(String id) {
        final SPARQLQueryManager queryManager = databaseClient.newSPARQLQueryManager();

        final SPARQLQueryDefinition query = new RdfQueryBuilder(new RdfTriple(null, RdfConstants.REVIEW_OF, id),
                null, null, null)
                .makeQuery(queryManager, RdfConstants.REVIEW_GRAPH_URI);

        return findBySparql(queryManager, query, true);
    }
}
