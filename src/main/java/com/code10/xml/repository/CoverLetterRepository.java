package com.code10.xml.repository;

import com.code10.xml.repository.base.XmlRepository;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.query.QueryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoverLetterRepository extends XmlRepository {

    @Autowired
    public CoverLetterRepository(XMLDocumentManager documentManager, QueryManager queryManager, DatabaseClient databaseClient) {
        super(documentManager, queryManager, databaseClient);
        collectionRef = "coverLetters";
    }
}
