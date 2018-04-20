package com.code10.xml.controller.exception.resolver;

import com.code10.xml.controller.exception.AuthorizationException;
import com.code10.xml.controller.exception.BadRequestException;
import com.code10.xml.controller.exception.ForbiddenException;
import com.code10.xml.controller.exception.NotFoundException;
import com.marklogic.client.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Catches exceptions that occur in any layer of the application (domain, service, controller).
 * This allows us to not have to worry about exception handling, as they will be caught here.
 * It also allows us to simply throw a custom exception when necessary, instead of having to propagate it up.
 * <p>
 * Caught exceptions get mapped to an adequate {@link HttpStatus}, which is then returned to the client along with the
 * exception message.
 */
@ControllerAdvice
public class ExceptionResolver {

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity authorizationException(AuthorizationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity badRequestException(BadRequestException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity forbiddenException(ForbiddenException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity notFoundException(NotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity resourceNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
