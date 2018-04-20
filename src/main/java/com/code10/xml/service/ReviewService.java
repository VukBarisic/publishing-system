package com.code10.xml.service;

import com.code10.xml.controller.exception.BadRequestException;
import com.code10.xml.model.RdfTriple;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.RdfConstants;
import com.code10.xml.repository.ReviewRepository;
import com.code10.xml.repository.UserRepository;
import com.code10.xml.util.XPathUtil;
import com.code10.xml.util.XsltUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;

@Service
public class ReviewService {

    @Value("classpath:xsl/review-xml.xsl")
    private Resource reviewXmlXsl;

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public String create(XmlWrapper wrapper, String paperId, String username) {
        final String id = reviewRepository.create(wrapper);

        final RdfTriple triple = new RdfTriple(id, RdfConstants.REVIEW_OF, paperId);
        reviewRepository.writeMetadata(triple);

        reviewRepository.writeMetadata(new RdfTriple(username, RdfConstants.REVIEWED, paperId));

        reviewRepository.removeMetadata(username, RdfConstants.REVIEWING, paperId, RdfConstants.REVIEW_GRAPH_URI);

        return id;
    }

    public XmlWrapper findById(String id) {
        return reviewRepository.findById(id);
    }

    public void assignReviewer(String id, XmlWrapper paperWrapper, String username) {
        final NodeList authors = (NodeList) XPathUtil.evaluate("/paper/authors/*", paperWrapper.getDom(), XPathConstants.NODESET);

        if (userRepository.isAssignedTo(username, id)) {
            throw new BadRequestException(String.format("%s is already assigned to review paper!", username));
        }

        for (int i = 0; i < authors.getLength(); i++) {
            final Element author = (Element) authors.item(i);
            final String authorUsername = author.getAttribute("username");
            if (username.equals(authorUsername)) {
                throw new BadRequestException("Cannot assign paper to its author!");
            }
        }

        final RdfTriple triple = new RdfTriple(username, RdfConstants.ASSIGNED_TO, id);
        reviewRepository.writeMetadata(triple);
    }

    public void respondToReviewRequest(String username, String id, boolean accepted) {
        if (accepted) {
            final RdfTriple triple = new RdfTriple(username, RdfConstants.REVIEWING, id);
            reviewRepository.writeMetadata(triple);
        }
        reviewRepository.removeMetadata(username, RdfConstants.ASSIGNED_TO, id, RdfConstants.REVIEW_GRAPH_URI);
    }

    public XmlWrapper prepareReview(XmlWrapper paperWrapper, String username, String paperId) {
        final Element comment = (Element) XPathUtil.evaluate("/paper/comment", paperWrapper.getDom(), XPathConstants.NODE);
        if (comment != null) {
            comment.setAttribute("refId", paperId);
        }

        final NodeList sections = (NodeList) XPathUtil.evaluate("//section", paperWrapper.getDom(), XPathConstants.NODESET);
        for (int i = 0; i < sections.getLength(); i++) {
            final Element section = (Element) sections.item(i);
            final Element commentChild = (Element) section.getLastChild();
            if ("comment".equals(commentChild.getNodeName())) {
                commentChild.setAttribute("refId", section.getAttribute("pp:id"));
            }
        }

        paperWrapper.updateXml();

        String reviewXml = XsltUtil.transform(paperWrapper, reviewXmlXsl);
        reviewXml = reviewXml.replaceAll("<comment", "<review:comment");
        reviewXml = reviewXml.replaceAll("comment>", "review:comment>");
        final XmlWrapper reviewWrapper = new XmlWrapper(reviewXml);

        final Element reviewElement = reviewWrapper.getDom().getDocumentElement();
        reviewElement.setAttribute("paperId", paperId);
        reviewElement.setAttribute("author", username);
        return reviewWrapper;
    }
}
