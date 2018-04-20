package com.code10.xml.controller;

import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.DatabaseConstsants;
import com.code10.xml.service.CoverLetterService;
import com.code10.xml.service.MailService;
import com.code10.xml.service.UserService;
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

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("api")
public class CoverLetterController {

    @Value("classpath:xsd/coverLetter.xsd")
    private Resource coverLetterXsd;

    @Value("classpath:xsl/cover-letter.xsl")
    private Resource coverXhtmlXsl;

    private final CoverLetterService coverLetterService;

    private final UserService userService;

    private final MailService mailService;

    @Autowired
    public CoverLetterController(CoverLetterService coverLetterService, UserService userService, MailService mailService) {
        this.coverLetterService = coverLetterService;
        this.userService = userService;
        this.mailService = mailService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/coverLetters",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity create(@RequestBody String xml, @RequestParam String documentId, UriComponentsBuilder builder) throws IOException {
        XsdUtil.validate(xml, coverLetterXsd.getURI().getPath());

        final XmlWrapper wrapper = new XmlWrapper(xml);
        final String id = coverLetterService.create(wrapper, documentId);

        final HttpHeaders headers = new HttpHeaders();
        final URI location = builder.path("api/coverLetters/{id}").buildAndExpand(id).toUri();
        headers.setLocation(location);

        mailService.sendCoverLetterMail(userService.findUserResponseByUsername(DatabaseConstsants.EDITOR_USERNAME).getEmail(),
                XsltUtil.transform(wrapper, coverXhtmlXsl), XsltUtil.toPdf(wrapper, coverXhtmlXsl).getInputStream());

        return new ResponseEntity<>(id, headers, HttpStatus.OK);
    }
}
