package com.code10.xml.controller;

import com.code10.xml.model.XmlWrapper;
import com.code10.xml.model.dto.AuthenticationRequest;
import com.code10.xml.model.dto.AuthenticationResponse;
import com.code10.xml.service.UserService;
import com.code10.xml.util.XsdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("api/auth")
public class AuthenticationController {

    @Value("classpath:xsd/user.xsd")
    private Resource userXsd;

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/signup",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity signup(@RequestBody String xml, UriComponentsBuilder builder) throws IOException {
        XsdUtil.validate(xml, userXsd.getURI().getPath());

        final XmlWrapper wrapper = new XmlWrapper(xml);
        final String id = userService.signup(wrapper);

        final HttpHeaders headers = new HttpHeaders();
        final URI location = builder.path("api/users/{id}").buildAndExpand(id).toUri();
        headers.setLocation(location);

        return new ResponseEntity<>(id, headers, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        final String role = userService.signin(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        return new ResponseEntity<>(new AuthenticationResponse(authenticationRequest.getUsername(), role), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/signout")
    public ResponseEntity signout() {
        SecurityContextHolder.clearContext();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity getCurrentUser() {
        final AuthenticationResponse response = userService.getCurrentUser();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
