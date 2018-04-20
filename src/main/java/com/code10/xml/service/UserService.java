package com.code10.xml.service;

import com.code10.xml.controller.exception.AuthorizationException;
import com.code10.xml.controller.exception.BadRequestException;
import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.constants.ReviewerStatus;
import com.code10.xml.model.constants.Roles;
import com.code10.xml.model.dto.AuthenticationResponse;
import com.code10.xml.model.dto.UserResponse;
import com.code10.xml.repository.UserRepository;
import com.code10.xml.util.XPathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, @Value("classpath:xml/editorInstance.xml") Resource editorXml) {
        this.userRepository = userRepository;
        this.initEditor(editorXml);
    }

    public String signup(XmlWrapper wrapper) {
        final String username = ((Node) XPathUtil.evaluate("/user/username", wrapper.getDom(), XPathConstants.NODE)).getTextContent();
        if (userRepository.exists(username)) {
            throw new BadRequestException("Username taken!");
        }

        final Element element = wrapper.getDom().getDocumentElement();
        final Node node = wrapper.getDom().createElement("role");
        node.setTextContent(Roles.AUTHOR);
        element.appendChild(node);
        wrapper.updateXml();

        userRepository.create(wrapper);

        return username;
    }

    public String signin(String username, String password) {
        if (!userRepository.exists(username)) {
            throw new AuthorizationException("Wrong username or password!");
        }

        final XmlWrapper wrapper = userRepository.findById(username);
        final Document document = wrapper.getDom();

        final String persistedUsername = ((Node) XPathUtil.evaluate("/user/username", document, XPathConstants.NODE)).getTextContent();
        final String persistedPassword = ((Node) XPathUtil.evaluate("/user/password", document, XPathConstants.NODE)).getTextContent();
        final Node node = (Node) XPathUtil.evaluate("/user/role", document, XPathConstants.NODE);
        final String role = node.getTextContent();

        if (!username.equals(persistedUsername) || !password.equals(persistedPassword)) {
            throw new AuthorizationException("Wrong username or password!");
        }

        final Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        final Authentication authentication = new PreAuthenticatedAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return role;
    }

    public AuthenticationResponse getCurrentUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String username = (String) auth.getPrincipal();
        final Object[] authorities = auth.getAuthorities().toArray();
        final String role = authorities.length > 0 ? authorities[0].toString() : null;

        userRepository.findById(username);

        return new AuthenticationResponse(username, role);
    }

    public boolean isAuthenticated() {
        return !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser");
    }

    public AuthenticationResponse findByUsername(String username) {
        final XmlWrapper wrapper = userRepository.findById(username);
        final Node node = (Node) XPathUtil.evaluate("/user/role", wrapper.getDom(), XPathConstants.NODE);

        return new AuthenticationResponse(username, node.getTextContent());
    }

    public void checkIsAssignedTo(String username, String paperId) {
        if (!userRepository.isAssignedTo(username, paperId)) {
            throw new AuthorizationException("You aren't assigned to review that paper!");
        }
    }

    public void checkIsReviewing(String username, String paperId) {
        if (!userRepository.isReviewing(username, paperId)) {
            throw new AuthorizationException("You aren't assigned to review that paper!");
        }
    }

    private void initEditor(Resource editorXml) {
        if (userRepository.exists("editor")) {
            return;
        }
        try {
            final File file = editorXml.getFile();
            final String xml = new String(Files.readAllBytes(file.toPath()));
            final XmlWrapper wrapper = new XmlWrapper(xml);
            userRepository.create(wrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<UserResponse> findAllAuthors() {
        return userRepository.findAll().stream().filter(user -> {
            final String role = ((Node) XPathUtil.evaluate(
                    "/user/role", user.getDom(), XPathConstants.NODE)).getTextContent();
            return role.equals("AUTHOR");
        }).map(user -> {
            final String username = ((Node) XPathUtil.evaluate(
                    "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent();
            final String email = ((Node) XPathUtil.evaluate(
                    "/user/email", user.getDom(), XPathConstants.NODE)).getTextContent();
            return new UserResponse(username, 0, email);
        }).collect(Collectors.toList());
    }

    public UserResponse findByPaperId(String paperId) {
        final XmlWrapper wrapper = userRepository.findByPaperId(paperId);

        final String username = ((Node) XPathUtil.evaluate("/user/username", wrapper.getDom(), XPathConstants.NODE)).getTextContent();
        final String email = ((Node) XPathUtil.evaluate("/user/email", wrapper.getDom(), XPathConstants.NODE)).getTextContent();
        final UserResponse user = new UserResponse();
        user.setUsername(username);
        user.setEmail(email);

        return user;
    }

    public UserResponse findUserResponseByUsername(String username) {
        final XmlWrapper wrapper = userRepository.findById(username);

        final String email = ((Node) XPathUtil.evaluate("/user/email", wrapper.getDom(), XPathConstants.NODE)).getTextContent();
        final UserResponse user = new UserResponse();
        user.setUsername(username);
        user.setEmail(email);

        return user;
    }

    public List<UserResponse> findAssignedReviewers(String paperId) {
        Set<UserResponse> assigned = userRepository.findAssignedByPaper(paperId).stream().filter(user -> {
            final String role = ((Node) XPathUtil.evaluate(
                    "/user/role", user.getDom(), XPathConstants.NODE)).getTextContent();
            return role.equals(Roles.AUTHOR);
        }).map(user -> {
            final String username = ((Node) XPathUtil.evaluate(
                    "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent();
            final String email = ((Node) XPathUtil.evaluate(
                    "/user/email", user.getDom(), XPathConstants.NODE)).getTextContent();
            return new UserResponse(username, email, ReviewerStatus.ASSIGNED);
        }).collect(Collectors.toSet());

        Set<UserResponse> reviewing = userRepository.findReviewingByPaper(paperId).stream().filter(user -> {
            final String role = ((Node) XPathUtil.evaluate(
                    "/user/role", user.getDom(), XPathConstants.NODE)).getTextContent();
            return role.equals(Roles.AUTHOR);
        }).map(user -> {
            final String username = ((Node) XPathUtil.evaluate(
                    "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent();
            final String email = ((Node) XPathUtil.evaluate(
                    "/user/email", user.getDom(), XPathConstants.NODE)).getTextContent();
            return new UserResponse(username, email, ReviewerStatus.REVIEWING);
        }).collect(Collectors.toSet());

        Set<UserResponse> reviewed = userRepository.findReviewedByPaper(paperId).stream().filter(user -> {
            final String role = ((Node) XPathUtil.evaluate(
                    "/user/role", user.getDom(), XPathConstants.NODE)).getTextContent();
            return role.equals(Roles.AUTHOR);
        }).map(user -> {
            final String username = ((Node) XPathUtil.evaluate(
                    "/user/username", user.getDom(), XPathConstants.NODE)).getTextContent();
            final String email = ((Node) XPathUtil.evaluate(
                    "/user/email", user.getDom(), XPathConstants.NODE)).getTextContent();
            return new UserResponse(username, email, ReviewerStatus.REVIEWED);
        }).collect(Collectors.toSet());

        assigned.addAll(reviewing);
        assigned.addAll(reviewed);
        return new ArrayList<>(assigned);
    }
}
