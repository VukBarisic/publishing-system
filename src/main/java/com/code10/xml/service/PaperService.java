package com.code10.xml.service;

import com.code10.xml.controller.exception.AuthorizationException;
import com.code10.xml.controller.exception.BadRequestException;
import com.code10.xml.model.RdfTriple;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.RdfConstants;
import com.code10.xml.model.constants.Roles;
import com.code10.xml.model.dto.AuthenticationResponse;
import com.code10.xml.model.dto.PaperResponse;
import com.code10.xml.model.dto.UserResponse;
import com.code10.xml.repository.PaperRepository;
import com.code10.xml.repository.ReviewRepository;
import com.code10.xml.repository.UserRepository;
import com.code10.xml.util.XPathUtil;
import com.code10.xml.util.search.RdfQueryBuilder;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaperService {

    private final PaperRepository paperRepository;

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    @Autowired
    public PaperService(PaperRepository paperRepository, ReviewRepository reviewRepository, UserRepository userRepository) {
        this.paperRepository = paperRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public String create(XmlWrapper wrapper, String username) {
        final String id = paperRepository.create(wrapper);

        final RdfTriple createdTriple = new RdfTriple(username, RdfConstants.CREATED, id);
        paperRepository.writeMetadata(createdTriple);

        final RdfTriple submittedTriple = new RdfTriple(username, RdfConstants.SUBMITTED, id);
        paperRepository.writeMetadata(submittedTriple);

        extractKeywords(wrapper).forEach(word ->
                paperRepository.writeMetadata(new RdfTriple(id, RdfConstants.HAS_KEYWORD, word.toLowerCase())));

        return id;
    }

    public String update(String username, String id, XmlWrapper wrapper) {
        if (!paperRepository.isAuthorOfPaper(username, id)) {
            throw new AuthorizationException("You are not authorized to update this paper");
        }

        paperRepository.removeMetadata(null, RdfConstants.REVIEW_OF, id, RdfConstants.REVIEW_GRAPH_URI);
        paperRepository.removeMetadata(null, RdfConstants.HAS_REVIEWS_OF, id, RdfConstants.REVIEW_GRAPH_URI);
        paperRepository.removeMetadata(null, RdfConstants.REVIEWED, id, RdfConstants.REVIEW_GRAPH_URI);

        return paperRepository.update(id, wrapper);
    }

    public XmlWrapper groupReviews(String id) {
        final XmlWrapper paperWrapper = findById(id);
        final List<XmlWrapper> reviewWrappers = reviewRepository.findByPaper(id);

        for (XmlWrapper wrapper : reviewWrappers) {
            final NodeList comments = (NodeList) XPathUtil.evaluate("/review/*", wrapper.getDom(), XPathConstants.NODESET);

            for (int i = 0; i < comments.getLength(); i++) {
                final Element comment = (Element) comments.item(i);
                final String content = comment.getTextContent();
                final String refId = comment.getAttribute("refId");
                if (!TextUtils.isEmpty(content)) {
                    final Node node = (Node) XPathUtil.evaluate(String.format("//*[@id='%s']", refId), paperWrapper.getDom(), XPathConstants.NODE);
                    final Node newComment = paperWrapper.getDom().createElement("comment");
                    newComment.setTextContent(content);
                    node.appendChild(newComment);
                }
            }
        }

        paperWrapper.updateXml();

        final String username = paperRepository.findUsernameByPaper(id);
        final RdfTriple rdfTriple = new RdfTriple(username, RdfConstants.HAS_REVIEWS_OF, id);
        paperRepository.writeMetadata(rdfTriple);

        return paperWrapper;
    }

    public XmlWrapper findById(String id) {
        return paperRepository.findById(id);
    }

    public void checkAuthorities(AuthenticationResponse currentUser, String paperId) {
        final boolean isPaperPublished = paperRepository.isPublished(paperId);

        if (currentUser == null) {
            if (!isPaperPublished) {
                throw new AuthorizationException("You are not authorized to view this paper");
            }
            return;
        }

        final boolean isEditor = Roles.EDITOR.equals(currentUser.getRole());
        final boolean isPaperAuthor = paperRepository.isAuthorOfPaper(currentUser.getUsername(), paperId);

        if (!isPaperPublished && !isEditor && !isPaperAuthor) {
            throw new AuthorizationException("You are not authorized to view this paper");
        }
    }

    public List<PaperResponse> findByCreator(String username) {
        final List<PaperResponse> papers = toResponseList(paperRepository.findByCreator(username));

        for (PaperResponse paper : papers) {
            if (!paperRepository.isPublished(paper.getId())) {
                paper.setRevisionRequired(true);
            }
        }

        return papers;
    }

    public List<PaperResponse> findPublishedByCreator(String username) {
        return toResponseList(paperRepository.findPublishedByCreator(username));
    }

    public List<PaperResponse> findPublished() {
        return toResponseList(paperRepository.findPublished());
    }

    public List<PaperResponse> findByText(String text, boolean published) {
        final List<PaperResponse> all = toResponseList(paperRepository.findByText(text));

        if (!published) {
            return all;
        } else {
            Set<PaperResponse> intersection = new HashSet<>(all);

            intersection.retainAll(new HashSet<>(findPublished()));

            return new ArrayList<>(intersection);
        }
    }

    public List<PaperResponse> findByTextAndUser(String text, String username) {
        Set<PaperResponse> all = new HashSet<>(findByText(text, false));

        Set<PaperResponse> userPapers = new HashSet<>(findByCreator(username));
        userPapers.retainAll(all);

        return new ArrayList<>(userPapers);
    }

    public List<PaperResponse> findByMetadataAndUser(String query, String username) {
        Set<PaperResponse> all = new HashSet<>(metadataSearch(query, false));

        Set<PaperResponse> userPapers = new HashSet<>(findByCreator(username));
        userPapers.retainAll(all);

        return new ArrayList<>(userPapers);
    }

    public List<PaperResponse> findSubmitted() {
        return toResponseList(paperRepository.findSubmitted());
    }

    public List<PaperResponse> findAssigned(String username) {
        return toResponseList(paperRepository.findAssigned(username));
    }

    public List<PaperResponse> findAccepted(String username) {
        return toResponseList(paperRepository.findAccepted(username));
    }

    public String findPaperAuthor(String paperId) {
        return paperRepository.findUsernameByPaper(paperId);
    }

    public void respondToPublishRequest(String paperId, XmlWrapper paperWrapper, boolean accepted) {
        if (accepted) {
            final NodeList authors = (NodeList) XPathUtil.evaluate("/paper/authors/*", paperWrapper.getDom(), XPathConstants.NODESET);

            for (int i = 0; i < authors.getLength(); i++) {
                final Element author = (Element) authors.item(i);
                final String username = author.getAttribute("username");
                try {
                    userRepository.findById(username);
                } catch (Exception e) {
                    continue;
                }
                if (!TextUtils.isEmpty(username)) {
                    paperRepository.writeMetadata(new RdfTriple(username, RdfConstants.PUBLISHED, paperId));
                    paperRepository.writeMetadata(new RdfTriple(paperId, RdfConstants.PUBLISHED_BY, username));
                    if (!paperRepository.isAuthorOfPaper(username, paperId)) {
                        paperRepository.writeMetadata(new RdfTriple(username, RdfConstants.CREATED, paperId));
                    }
                }
            }
        }

        paperRepository.removeMetadata(null, RdfConstants.SUBMITTED, paperId, RdfConstants.PAPER_GRAPH_URI);
        paperRepository.removeMetadata(null, RdfConstants.ASSIGNED_TO, paperId, RdfConstants.REVIEW_GRAPH_URI);
        paperRepository.removeMetadata(null, RdfConstants.REVIEWING, paperId, RdfConstants.REVIEW_GRAPH_URI);
        paperRepository.removeMetadata(null, RdfConstants.REVIEW_OF, paperId, RdfConstants.REVIEW_GRAPH_URI);
        paperRepository.removeMetadata(null, RdfConstants.REVIEWED, paperId, RdfConstants.REVIEW_GRAPH_URI);
    }

    public List<PaperResponse> metadataSearch(String query, boolean published) {
        final List<PaperResponse> all = toResponseList(paperRepository.findByMetadata(parseQuery(query)));

        if (!published) {
            return all;
        } else {
            Set<PaperResponse> publishedPapers = new HashSet<>(toResponseList(paperRepository.findPublished()));
            publishedPapers.retainAll(new HashSet<>(all));
            return new ArrayList<>(publishedPapers);
        }
    }

    public List<UserResponse> findSuggestedReviewers(String id) {
        final XmlWrapper paper = findById(id);

        final List<String> keywords = extractKeywords(paper);

        final List<String> assigned = userRepository.findAssignedByPaper(id).stream().map(user -> ((Node) XPathUtil.evaluate(
                "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent()).collect(Collectors.toList());

        final List<String> reviewing = userRepository.findReviewingByPaper(id).stream().map(user -> ((Node) XPathUtil.evaluate(
                "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent()).collect(Collectors.toList());

        final List<String> reviewed = userRepository.findReviewedByPaper(id).stream().map(user -> ((Node) XPathUtil.evaluate(
                "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent()).collect(Collectors.toList());

        final List<XmlWrapper> users = userRepository.findAll().stream().filter(
                user -> {
                    final String username = ((Node) XPathUtil.evaluate(
                            "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent();
                    final String role = ((Node) XPathUtil.evaluate(
                            "/user/role", user.getDom(), XPathConstants.NODE)).getTextContent();
                    return role.equals(Roles.AUTHOR) &&
                            !assigned.contains(username) &&
                            !reviewing.contains(username) &&
                            !reviewed.contains(username);
                }).collect(Collectors.toList());

        final Map<String, Integer> score = new HashMap<>();

        users.forEach(user -> score.put(((Node) XPathUtil.evaluate(
                "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent(), 0));

        users.forEach(user -> {
            final String username = ((Node) XPathUtil.evaluate(
                    "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent();
            keywords.forEach(keyword -> score.put(username, score.get(username) + paperRepository.findPapersByUserAndKeyword(username, keyword).size()));
        });

        final List<String> usernames = paperRepository.findAuthorsByPaper(id);

        return score.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .map(entry -> {
                    final String email = ((Node) XPathUtil.evaluate(
                            "/user/email", userRepository.findById(entry.getKey()).getDom(), XPathConstants.NODE)).getTextContent();
                    return new UserResponse(entry.getKey(), entry.getValue(), email);
                })
                .filter(user -> !usernames.contains(user.getUsername()))
                .collect(Collectors.toList());
    }

    public void deletePaper(String paperId) {
        paperRepository.delete(paperId);

        paperRepository.removeMetadata(paperId, null, null, RdfConstants.PAPER_GRAPH_URI);
        paperRepository.removeMetadata(null, null, paperId, RdfConstants.PAPER_GRAPH_URI);

        paperRepository.removeMetadata(paperId, null, null, RdfConstants.REVIEW_GRAPH_URI);
        paperRepository.removeMetadata(null, null, paperId, RdfConstants.REVIEW_GRAPH_URI);
        paperRepository.removeMetadata(null, RdfConstants.REVIEWED, paperId, RdfConstants.REVIEW_GRAPH_URI);
    }

    private List<String> extractKeywords(XmlWrapper wrapper) {
        final Node node = (Node) XPathUtil.evaluate("/paper/abstract/keywords",
                wrapper.getDom(), XPathConstants.NODE);

        final List<String> keywords = new ArrayList<>();

        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node curr = node.getChildNodes().item(i);
            if (curr.getNodeType() == Node.ELEMENT_NODE)
                keywords.add(curr.getTextContent());
        }

        return keywords;
    }

    private List<PaperResponse> toResponseList(List<XmlWrapper> wrappers) {
        return wrappers.stream().map(w -> {
            final String id = ((Node) XPathUtil.evaluate("/paper/@id",
                    w.getDom(), XPathConstants.NODE)).getNodeValue();
            final String title = ((Node) XPathUtil.evaluate("/paper/title",
                    w.getDom(), XPathConstants.NODE)).getTextContent();
            return new PaperResponse(id, title);
        }).collect(Collectors.toList());
    }

    private RdfQueryBuilder parseQuery(String query) {
        final String[] tokens = query.split(" ");

        if (tokens.length == 0 || tokens.length % 2 == 0) {
            throw new BadRequestException("Invalid query");
        }

        final RdfQueryBuilder builder = new RdfQueryBuilder(convertTokenToRdf(tokens[0]), null, null, null);

        for (int i = 1; i < tokens.length; i += 2) {
            if (i % 2 == 1) {
                switch (tokens[i]) {
                    case "&":
                        builder.and(convertTokenToRdf(tokens[i + 1]), null, null, null);
                        break;
                    case "|":
                        builder.or(convertTokenToRdf(tokens[i + 1]), null, null, null);
                        break;
                }
            }
        }

        return builder;
    }

    private RdfTriple convertTokenToRdf(String token) {
        final String[] tokens = token.split(":");

        if (tokens.length != 2) {
            throw new BadRequestException("Invalid query");
        }

        return new RdfTriple(null, tokens[0], tokens[1].toLowerCase());
    }

    public void removeAuthors(XmlWrapper wrapper, String username, String id) {
        if (paperRepository.isAuthorOfPaper(username, id)) {
            return;
        }

        final Element element = wrapper.getDom().getDocumentElement();
        element.removeChild(element.getElementsByTagName("authors").item(0));
        wrapper.updateXml();
    }
}
