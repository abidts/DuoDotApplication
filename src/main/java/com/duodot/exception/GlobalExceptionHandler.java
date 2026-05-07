package com.duodot.exception;

import com.duodot.responseBean.ServiceResponseBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ServiceResponseBean> handleResourceNotFound(ResourceNotFoundException ex) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean.setStatus(Boolean.FALSE);
        serviceResponseBean.setMessage(ex.getMessage());
        serviceResponseBean.setErrors(Set.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(serviceResponseBean);
    }
    
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ServiceResponseBean> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean.setStatus(Boolean.FALSE);
        serviceResponseBean.setMessage(ex.getMessage());
        serviceResponseBean.setErrors(Set.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(serviceResponseBean);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ServiceResponseBean> handleBadRequest(BadRequestException ex) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean.setStatus(Boolean.FALSE);
        serviceResponseBean.setMessage(ex.getMessage());
        serviceResponseBean.setErrors(Set.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(serviceResponseBean);
    }
    
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ServiceResponseBean> handleFileStorage(FileStorageException ex) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean.setStatus(Boolean.FALSE);
        serviceResponseBean.setMessage(ex.getMessage());
        serviceResponseBean.setErrors(Set.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(serviceResponseBean);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServiceResponseBean> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Set<String> validationErrors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toSet());

        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean.setStatus(Boolean.FALSE);
        serviceResponseBean.setMessage("Validation failed");
        serviceResponseBean.setErrors(validationErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(serviceResponseBean);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceResponseBean> handleGenericException(Exception ex) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean.setStatus(Boolean.FALSE);
        serviceResponseBean.setMessage("An unexpected error occurred: " + ex.getMessage());
        serviceResponseBean.setErrors(Set.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(serviceResponseBean);
    }
}
