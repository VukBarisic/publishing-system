package com.code10.xml.controller;

import com.code10.xml.controller.exception.BadRequestException;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.DatabaseConstsants;
import com.code10.xml.service.MailService;
import com.code10.xml.service.PaperService;
import com.code10.xml.service.ReviewService;
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
public class ReviewController {

    @Value("classpath:xsd/review.xsd")
    private Resource reviewXsd;

    @Value("classpath:xsd/paper.xsd")
    private Resource paperXsd;

    @Value("classpath:xsl/review-xhtml.xsl")
    private Resource reviewXhtmlXsl;

    private final ReviewService reviewService;

    private final PaperService paperService;

    private final UserService userService;

    private final MailService mailService;

    @Autowired
    public ReviewController(ReviewService reviewService, PaperService paperService, UserService userService, MailService mailService) {
        this.reviewService = reviewService;
        this.paperService = paperService;
        this.userService = userService;
        this.mailService = mailService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/reviews",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity create(@RequestBody String xml, UriComponentsBuilder builder) throws IOException {
        XsdUtil.validate(xml, paperXsd.getURI().getPath());

        final String username = userService.getCurrentUser().getUsername();
        final XmlWrapper paperWrapper = new XmlWrapper(xml);
        final String paperId = ((Node) XPathUtil.evaluate("/paper/@id", paperWrapper.getDom(), XPathConstants.NODE)).getNodeValue();
        userService.checkIsReviewing(username, paperId);

        final XmlWrapper reviewWrapper = reviewService.prepareReview(paperWrapper, username, paperId);
        final String id = reviewService.create(reviewWrapper, paperId, username);

        final HttpHeaders headers = new HttpHeaders();
        final URI location = builder.path("api/reviews/{id}").buildAndExpand(id).toUri();
        headers.setLocation(location);

        mailService.sendPaperReviewedMail(paperWrapper, username,
                userService.findUserResponseByUsername(DatabaseConstsants.EDITOR_USERNAME).getEmail(),
                XsltUtil.transform(paperWrapper, reviewXhtmlXsl),
                XsltUtil.toPdf(paperWrapper, reviewXhtmlXsl).getInputStream());

        return new ResponseEntity<>(id, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/reviews/preview",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XHTML_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity preview(@RequestBody String xml) throws IOException {
        XsdUtil.validate(xml, paperXsd.getURI().getPath());
        final XmlWrapper wrapper = new XmlWrapper(xml);
        return new ResponseEntity<>(XsltUtil.transform(wrapper, reviewXhtmlXsl), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @PutMapping(value = "/reviews/{paperId}/assign/{username}")
    public ResponseEntity assignReviewer(@PathVariable String paperId, @PathVariable String username) {
        userService.findByUsername(username);

        final String currentUsername = userService.getCurrentUser().getUsername();
        if (username.equals(currentUsername)) {
            throw new BadRequestException("Cannot assign paper to yourself!");
        }

        final XmlWrapper paperWrapper = paperService.findById(paperId);
        reviewService.assignReviewer(paperId, paperWrapper, username);

        mailService.sendPaperAssignedMail(paperService.findById(paperId),
                userService.findUserResponseByUsername(username).getEmail(),
                "http://localhost:4200/papers/assigned");

        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/reviews/{paperId}/respond",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity respondToReviewRequest(@PathVariable String paperId, @RequestParam boolean accepted) {
        final String username = userService.getCurrentUser().getUsername();
        userService.checkIsAssignedTo(username, paperId);
        reviewService.respondToReviewRequest(username, paperId, accepted);

        if (accepted) {
            mailService.sendReviewerAcceptedMail(paperService.findById(paperId),
                    userService.findUserResponseByUsername(DatabaseConstsants.EDITOR_USERNAME).getEmail(), username);
        } else {
            mailService.sendReviewerDeclinedMail(paperService.findById(paperId),
                    userService.findUserResponseByUsername(DatabaseConstsants.EDITOR_USERNAME).getEmail(), username);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @GetMapping(value = "/papers/{paperId}/suggested-reviewers",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity suggestReviewers(@PathVariable String paperId) {
        paperService.findById(paperId);

        return new ResponseEntity<>(paperService.findSuggestedReviewers(paperId), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('EDITOR')")
    @GetMapping(value = "/papers/{paperId}/reviewers",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity reviewers(@PathVariable String paperId) {
        paperService.findById(paperId);

        return new ResponseEntity<>(userService.findAssignedReviewers(paperId), HttpStatus.OK);
    }
}
