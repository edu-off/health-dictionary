package br.com.healthdictionary.exceptions;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public final ResponseEntity<ExceptionResponse> entityNotFoundException(NoSuchElementException ex, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public final ResponseEntity<ExceptionResponse> duplicateKeyException(DuplicateKeyException ex, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ExceptionResponse> illegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> usernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(JWTCreationException.class)
    public final ResponseEntity<ExceptionResponse> tokenCreationException(JWTCreationException ex, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public final ResponseEntity<ExceptionResponse> tokenVerificationException(JWTVerificationException ex, HttpServletRequest request) {
        String errorMessage = ex.getMessage().isBlank() ? "token inválido" : ex.getMessage();
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.BAD_REQUEST.value(), errorMessage, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public final ResponseEntity<ExceptionResponse> authorizationDeniedException(AuthorizationDeniedException ex, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<ExceptionResponse> badCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<ExceptionResponse> httpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getTimestamp(), HttpStatus.BAD_REQUEST.value(), "requisição sem corpo", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ExceptionResponseValidation> methodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ExceptionResponseValidation exceptionResponseValidation = new ExceptionResponseValidation(getTimestamp(), HttpStatus.UNPROCESSABLE_ENTITY.value(), "Existem dados incorretos no corpo da requisição", request.getRequestURI());
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> exceptionResponseValidation.addError(fieldError.getField(), fieldError.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exceptionResponseValidation);
    }

    private String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

}
