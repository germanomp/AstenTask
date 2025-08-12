package com.astentask.exception;

import com.astentask.dtos.ErrorResponseDTO;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
      log.warn("Recurso não encontrado: {}", ex.getMessage());

      ErrorResponseDTO error = new ErrorResponseDTO(
              HttpStatus.NOT_FOUND.value(),
              ex.getMessage(),
              Instant.now()
      );

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
      Map<String, String> errors = new HashMap<>();
      ex.getBindingResult().getAllErrors().forEach(err -> {
        String fieldName = ((FieldError) err).getField();
        String errorMessage = err.getDefaultMessage();
        errors.put(fieldName, errorMessage);
      });

      log.warn("Erro de validação: {}", errors);

      String combinedMessage = "Erro de validação: " + errors.toString();

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                combinedMessage,
                Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyInUse(EmailAlreadyUsedException ex) {
        log.warn("Tentativa de registro com email já existente: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
      log.error("Erro inesperado: ", ex);

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro inesperado",
                Instant.now()
        );

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
