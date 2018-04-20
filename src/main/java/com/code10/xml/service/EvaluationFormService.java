package com.code10.xml.service;

import com.code10.xml.model.RdfTriple;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.RdfConstants;
import com.code10.xml.repository.EvaluationFormRepository;
import com.code10.xml.repository.PaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluationFormService {

    private final EvaluationFormRepository evaluationFormRepository;

    private final PaperRepository paperRepository;

    @Autowired
    public EvaluationFormService(EvaluationFormRepository evaluationFormRepository, PaperRepository paperRepository) {
        this.evaluationFormRepository = evaluationFormRepository;
        this.paperRepository = paperRepository;
    }

    public String create(XmlWrapper wrapper, String documentId) {
        final String id = evaluationFormRepository.create(wrapper);

        final RdfTriple triple = new RdfTriple(id, RdfConstants.EVALUATES, documentId);
        paperRepository.writeMetadata(triple);

        return id;
    }

    public XmlWrapper findById(String id) {
        return evaluationFormRepository.findById(id);
    }
}
