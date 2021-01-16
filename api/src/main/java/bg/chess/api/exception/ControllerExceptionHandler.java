package bg.chess.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {DuplicateKeyException.class})
    public ResponseEntity<Map<String, Object>> duplicateKeyException(ServerHttpRequest request, DuplicateKeyException ex) {
        this.logException(ex);

        HttpStatus status = HttpStatus.CONFLICT;
        String message = "Resource already exists";
        Map<String, Object> errorPropertiesMap = this.getErrorObject(request, status, message);
        return new ResponseEntity<>(errorPropertiesMap, status);
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    public ResponseEntity<Map<String, Object>> responseStatusException(ServerHttpRequest request, ResponseStatusException ex) {
        this.logException(ex);

        Map<String, Object> errorPropertiesMap = this.getErrorObject(request, ex.getStatus(), ex.getReason());
        return new ResponseEntity<>(errorPropertiesMap, ex.getStatus());
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<Map<String, Object>> badCredentialsException(ServerHttpRequest request, BadCredentialsException ex) {
        this.logException(ex);

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String message = "Bad Credentials";
        Map<String, Object> errorPropertiesMap = this.getErrorObject(request, status, message);
        return new ResponseEntity<>(errorPropertiesMap, status);
    }

    private void logException(Exception ex) {
        if (log.isDebugEnabled()) {
            log.debug("Exception: ", ex);
        } else if (log.isInfoEnabled()){
            log.debug(ex.getMessage());
        }
    }

    private Map<String, Object> getErrorObject(ServerHttpRequest request, HttpStatus status, String message) {
        Map<String, Object> errorPropertiesMap = new LinkedHashMap<>();
        errorPropertiesMap.put("timestamp", new Date());
        errorPropertiesMap.put("path", request.getPath().pathWithinApplication().value());
        errorPropertiesMap.put("status", status.value());
        errorPropertiesMap.put("error", status.getReasonPhrase());
        errorPropertiesMap.put("message", message);
        errorPropertiesMap.put("requestId", request.getId());
        return errorPropertiesMap;
    }
}
