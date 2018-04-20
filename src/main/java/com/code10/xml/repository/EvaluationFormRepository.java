package com.code10.xml.repository;

import com.code10.xml.repository.base.XmlRepository;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.query.QueryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EvaluationFormRepository extends XmlRepository {

    @Autowired
    public EvaluationFormRepository(XMLDocumentManager documentManager, QueryManager queryManager, DatabaseClient databaseClient) {
        super(documentManager, queryManager, databaseClient);
        collectionRef = "evaluationForms";
    }
}
