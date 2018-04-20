package com.code10.xml.controller;

import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.DatabaseConstsants;
import com.code10.xml.service.EvaluationFormService;
import com.code10.xml.service.MailService;
import com.code10.xml.service.PaperService;
import com.code10.xml.service.UserService;
import com.code10.xml.util.XPathUtil;
import com.code10.xml.util.XsdUtil;
import com.code10.xml.util.XsltUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("api")
public class EvaluationFormController {

    @Value("classpath:xsd/evaluationForm.xsd")
    private Resource evaluationFormXsd;

    @Value("classpath:xsl/evaluation-form.xsl")
    private Resource evaluationFormXhtmlXsl;

    private final EvaluationFormService evaluationFormService;

    private final UserService userService;

    private final MailService mailService;

    private final PaperService paperService;

    @Autowired
    public EvaluationFormController(EvaluationFormService evaluationFormService, UserService userService,
                                    MailService mailService, PaperService paperService) {
        this.evaluationFormService = evaluationFormService;
        this.userService = userService;
        this.mailService = mailService;
        this.paperService = paperService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/evaluationForms",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity create(@RequestBody String xml, @RequestParam String documentId, UriComponentsBuilder builder) throws IOException {
        XsdUtil.validate(xml, evaluationFormXsd.getURI().getPath());

        final XmlWrapper wrapper = new XmlWrapper(xml);
        final String id = evaluationFormService.create(wrapper, documentId);

        final HttpHeaders headers = new HttpHeaders();
        final URI location = builder.path("api/evaluationForms/{id}").buildAndExpand(id).toUri();
        headers.setLocation(location);

        final String username = userService.getCurrentUser().getUsername();
        final XmlWrapper paper = paperService.findById(documentId);
        final String title = ((Node) XPathUtil.evaluate(
                "/paper/title", paper.getDom(), XPathConstants.NODE)).getTextContent();

        mailService.sendEvaluationMail(userService.findUserResponseByUsername(DatabaseConstsants.EDITOR_USERNAME).getEmail(),
                username,
                title,
                XsltUtil.transform(wrapper, evaluationFormXhtmlXsl), XsltUtil.toPdf(wrapper, evaluationFormXhtmlXsl).getInputStream());

        return new ResponseEntity<>(id, headers, HttpStatus.OK);
    }
}
