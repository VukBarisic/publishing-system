package com.code10.xml.service;

import com.code10.xml.model.RdfTriple;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.RdfConstants;
import com.code10.xml.repository.CoverLetterRepository;
import com.code10.xml.repository.PaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;

    private final PaperRepository paperRepository;

    @Autowired
    public CoverLetterService(CoverLetterRepository coverLetterRepository, PaperRepository paperRepository) {
        this.coverLetterRepository = coverLetterRepository;
        this.paperRepository = paperRepository;
    }

    public String create(XmlWrapper wrapper, String documentId) {
        final String id = coverLetterRepository.create(wrapper);

        final RdfTriple triple = new RdfTriple(id, RdfConstants.ACCOMPANIES, documentId);
        paperRepository.writeMetadata(triple);

        return id;
    }

    public XmlWrapper findById(String id) {
        return coverLetterRepository.findById(id);
    }
}
