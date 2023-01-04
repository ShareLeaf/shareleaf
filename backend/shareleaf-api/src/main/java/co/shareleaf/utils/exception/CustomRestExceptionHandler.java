package co.shareleaf.utils.exception;

import co.shareleaf.model.SLExceptionResponse;
import co.shareleaf.model.SLGenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nonnull;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Biz Melesse
 * created on 01/04/2023
 */
@Slf4j
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * BindException: This exception is thrown when fatal binding errors occur.
     * MethodArgumentNotValidException:
     * This exception is thrown when argument annotated with @Valid failed validation:
     */
    @Nonnull
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            @Nonnull MethodArgumentNotValidException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status,
            @Nonnull WebRequest request
    ) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        String err = errors.stream()
                .collect(Collectors.joining(";", "{", "}"));

        SLGenericResponse body = new SLGenericResponse();
        body.setStatus(JsonNullable.of(HttpStatus.BAD_REQUEST.toString()));
        body.setError(JsonNullable.of(err));

        return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * MissingServletRequestPartException: This exception is thrown when the part of a multipart request not found
     * MissingServletRequestParameterException: This exception is thrown when request missing parameter:
     */
    @Nonnull
    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(
            @Nonnull MissingServletRequestParameterException ex, @Nonnull HttpHeaders headers,
            @Nonnull HttpStatus status, @Nonnull WebRequest request
    ) {
        String error = ex.getParameterName() + " parameter is missing";

        SLGenericResponse body = new SLGenericResponse();
        body.setStatus(JsonNullable.of(HttpStatus.BAD_REQUEST.toString()));
        body.setError(JsonNullable.of(error));
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /*
     * ConstrainViolationException: This exception reports the result of constraint violations:
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request
    ) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        String err = errors.stream()
                .collect(Collectors.joining(";", "{", "}"));
        SLGenericResponse body = new SLGenericResponse();
        body.setStatus(JsonNullable.of(HttpStatus.BAD_REQUEST.toString()));
        body.setError(JsonNullable.of(err));

        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * TypeMismatchException: This exception is thrown when try to set bean property with wrong type.
     * MethodArgumentTypeMismatchException: This exception is thrown when method argument is not the expected type:
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request
    ) {
        String error = ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();

        SLGenericResponse body = new SLGenericResponse();
        body.setStatus(JsonNullable.of(HttpStatus.BAD_REQUEST.toString()));
        body.setError(JsonNullable.of(error));

        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            ShareLeafControllerException.class
            , ShareLeafValidationException.class
    })
    public ResponseEntity<Object> handleShareLeafSpecificException(
            RuntimeException ex, WebRequest request) {
        SLGenericResponse body = new SLGenericResponse();
        String bodyOfResponse = ex.getMessage(); // This should be application specific
        body.setStatus(JsonNullable.of(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
        body.setError(JsonNullable.of(bodyOfResponse));
        return handleExceptionInternal(ex, body,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = ShareLeafException.class)
    public ResponseEntity<Object> handleShareLeafException(ShareLeafException ex, WebRequest request) {
        SLExceptionResponse response = new SLExceptionResponse();
        if (ex.getShareLeafExceptionCode() != null) {
            response.setExceptionCode(JsonNullable.of(ex.getShareLeafExceptionCode().name()));
        }
        response.setStatus(JsonNullable.of(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
        if (ex.getTitle() != null) {
            response.setTitle(JsonNullable.of(ex.getTitle()));
        }
        if (ex.getErrMessage() != null) {
            response.setMessage(JsonNullable.of(ex.getErrMessage()));
        }
        return handleExceptionInternal(ex, response,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
