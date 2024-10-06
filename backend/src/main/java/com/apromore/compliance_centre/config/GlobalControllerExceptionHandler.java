package com.apromore.compliance_centre.config;

import com.apromore.compliance_centre.response.Response;
import com.apromore.compliance_centre.response.ResponseError;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Response<?>> handleNotFoundException(ResponseStatusException ex) {
        return new ResponseEntity<>(
            Response.error(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                new ArrayList<>(List.of(new ResponseError("error", ex.getReason())))
            ),
            ex.getStatusCode()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleException(Exception ex) {
        return new ResponseEntity<>(
            Response.error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                new ArrayList<>(List.of(new ResponseError("error", "Internal server error")))
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
