package com.code10.xml.controller;

import com.code10.xml.controller.exception.ForbiddenException;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.DatabaseConstsants;
import com.code10.xml.model.dto.AuthenticationResponse;
import com.code10.xml.model.dto.PaperResponse;
import com.code10.xml.model.dto.UserResponse;
import com.code10.xml.service.MailService;
import com.code10.xml.service.PaperService;
import com.code10.xml.service.UserService;
import com.code10.xml.util.XsdUtil;
import com.code10.xml.util.XsltUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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
import java.util.List;

@RestController
@RequestMapping("api")
public class PaperController {

    @Value("classpath:xsd/paper.xsd")
    private Resource paperXsd;

    @Value("classpath:xsl/paper-xhtml.xsl")
    private Resource paperXhtmlXsl;

    @Value("classpath:xsl/review-xhtml.xsl")
    private Resource reviewXhtmlXsl;

    private final PaperService paperService;

    private final UserService userService;

    private final MailService mailService;

    @Autowired
    public PaperController(PaperService paperService, UserService userService, MailService mailService) {
        this.paperService = paperService;
        this.userService = userService;
        this.mailService = mailService;
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @PostMapping(value = "/papers",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity create(@RequestBody String xml, UriComponentsBuilder builder) throws IOException {
        XsdUtil.validate(xml, paperXsd.getURI().getPath());

        final XmlWrapper wrapper = new XmlWrapper(xml);
        final AuthenticationResponse authenticationResponse = userService.getCurrentUser();
        final String id = paperService.create(wrapper, authenticationResponse.getUsername());

        final HttpHeaders headers = new HttpHeaders();
        final URI location = builder.path("api/papers/{id}").buildAndExpand(id).toUri();
        headers.setLocation(location);

        mailService.sendPaperSubmittedMail(wrapper,
                userService.findUserResponseByUsername(DatabaseConstsants.EDITOR_USERNAME).getEmail(),
                XsltUtil.transform(wrapper, paperXhtmlXsl),
                XsltUtil.toPdf(wrapper, paperXhtmlXsl).getInputStream());

        return new ResponseEntity<>(id, headers, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/papers/{id}",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity update(@RequestBody String xml, @PathVariable String id, UriComponentsBuilder builder) throws IOException {
        XsdUtil.validate(xml, paperXsd.getURI().getPath());

        final XmlWrapper wrapper = new XmlWrapper(xml);
        paperService.update(userService.getCurrentUser().getUsername(), id, wrapper);

        final HttpHeaders headers = new HttpHeaders();
        final URI location = builder.path("api/papers/{id}").buildAndExpand(id).toUri();
        headers.setLocation(location);

        mailService.sendPaperRevisedMail(wrapper,
                userService.findUserResponseByUsername(DatabaseConstsants.EDITOR_USERNAME).getEmail(),
                XsltUtil.transform(wrapper, paperXhtmlXsl),
                XsltUtil.toPdf(wrapper, paperXhtmlXsl).getInputStream());

        return new ResponseEntity<>(id, headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "/papers",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findPapers(@RequestParam(required = false) Boolean mine) {
        if (userService.isAuthenticated() && mine != null && mine) {
            return new ResponseEntity<>(paperService.findByCreator(userService.getCurrentUser().getUsername()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(paperService.findPublished(), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/papers/preview",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XHTML_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity preview(@RequestBody String xml) throws IOException {
        XsdUtil.validate(xml, paperXsd.getURI().getPath());
        final XmlWrapper wrapper = new XmlWrapper(xml);
        return new ResponseEntity<>(XsltUtil.transform(wrapper, paperXhtmlXsl), HttpStatus.OK);
    }

    @GetMapping(value = "/papers/{id}",
            produces = {MediaType.APPLICATION_XHTML_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findById(@PathVariable String id) {
        final XmlWrapper wrapper = paperService.findById(id);
        AuthenticationResponse authentication = null;
        if (userService.isAuthenticated()) {
            authentication = userService.getCurrentUser();
        }
        paperService.checkAuthorities(authentication, id);

        return new ResponseEntity<>(XsltUtil.transform(wrapper, paperXhtmlXsl), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping(value = "/papers/{id}/xml",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findXmlById(@PathVariable String id) {
        final XmlWrapper wrapper = paperService.findById(id);
        final String username = userService.getCurrentUser().getUsername();
        paperService.removeAuthors(wrapper, username, id);

        return new ResponseEntity<>(wrapper.getXml(), HttpStatus.OK);
    }

    @GetMapping(value = "/papers/{id}/xml/download",
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity downloadXml(@PathVariable String id) {
        final String paper = paperService.findById(id).getXml();
        paperService.checkAuthorities(userService.getCurrentUser(), id);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Disposition", "attachment; filename=paper.xml");

        return new ResponseEntity<>(new ByteArrayResource(paper.getBytes()), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/papers/{id}/pdf/download",
            produces = {MediaType.APPLICATION_PDF_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity downloadPdf(@PathVariable String id) {
        final XmlWrapper wrapper = paperService.findById(id);
        paperService.checkAuthorities(userService.getCurrentUser(), id);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Disposition", "attachment; filename=paper.pdf");

        return new ResponseEntity<>(XsltUtil.toPdf(wrapper, paperXhtmlXsl), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/papers/search-text",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity basicSearch(@RequestParam(required = false) Boolean mine, @RequestParam String text) {
        final List<PaperResponse> papers;

        if (userService.isAuthenticated() && mine != null && mine) {

            papers = paperService.findByTextAndUser(text, userService.getCurrentUser().getUsername());
        } else {
            papers = paperService.findByText(text, true);
        }

        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    @GetMapping(value = "/papers/search-metadata",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity advancedSearch(@RequestParam(required = false) Boolean mine, @RequestParam String query) {
        final List<PaperResponse> papers;

        if (userService.isAuthenticated() && mine != null && mine) {

            papers = paperService.findByMetadataAndUser(query, userService.getCurrentUser().getUsername());
        } else {
            papers = paperService.metadataSearch(query, true);
        }

        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    @GetMapping(value = "/authors/{username}/papers",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findByAuthor(@PathVariable String username) {
        userService.findByUsername(username);

        final List<PaperResponse> papers;
        if (userService.isAuthenticated() && userService.getCurrentUser().getUsername().equals(username)) {
            papers = paperService.findByCreator(username);
        } else {
            papers = paperService.findPublishedByCreator(username);
        }

        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    @GetMapping(value = "/papers/{id}/author",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findAuthor(@PathVariable String id) {
        return new ResponseEntity<>(paperService.findPaperAuthor(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping(value = "/papers/assigned",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findAssigned() {
        final AuthenticationResponse authenticationResponse = userService.getCurrentUser();
        final List<PaperResponse> papers = paperService.findAssigned(authenticationResponse.getUsername());
        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @GetMapping(value = "/papers/accepted",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findAccepted() {
        final String username = userService.getCurrentUser().getUsername();
        final List<PaperResponse> papers = paperService.findAccepted(username);
        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @GetMapping(value = "/papers/submitted",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findSubmitted() {
        final List<PaperResponse> papers = paperService.findSubmitted();
        return new ResponseEntity<>(papers, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @PutMapping(value = "/papers/{id}/group-review",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity groupReviews(@PathVariable String id, UriComponentsBuilder builder) throws IOException {
        final XmlWrapper paperWithComments = paperService.groupReviews(id);

        final HttpHeaders headers = new HttpHeaders();
        final URI location = builder.path("api/papers/{id}").buildAndExpand(id).toUri();
        headers.setLocation(location);

        final UserResponse user = userService.findByPaperId(id);

        mailService.sendReviewSubmittedMail(paperWithComments, user.getEmail(),
                XsltUtil.transform(paperWithComments, reviewXhtmlXsl),
                XsltUtil.toPdf(paperWithComments, reviewXhtmlXsl).getInputStream());

        return new ResponseEntity<>(id, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @PutMapping(value = "/papers/{paperId}/respond",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity respondToPublishRequest(@PathVariable String paperId, @RequestParam boolean accepted) {
        final XmlWrapper wrapper = paperService.findById(paperId);
        paperService.respondToPublishRequest(paperId, wrapper, accepted);
        final UserResponse user = userService.findByPaperId(paperId);
        if (accepted) {
            mailService.sendPaperPublishedMail(wrapper, user.getEmail());
        } else {
            mailService.sendPaperRejectedMail(wrapper, user.getEmail());
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(value = "/authors",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity findAllAuthors() {
        return new ResponseEntity<>(userService.findAllAuthors(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('AUTHOR')")
    @DeleteMapping(value = "/papers/{paperId}")
    public ResponseEntity deletePaper(@PathVariable String paperId) {
        final UserResponse user = userService.findByPaperId(paperId);

        if (!userService.getCurrentUser().getUsername().equals(user.getUsername())) {
            throw new ForbiddenException("User cannot delete this paper");
        }

        paperService.deletePaper(paperId);

        return new ResponseEntity(HttpStatus.OK);
    }
}
