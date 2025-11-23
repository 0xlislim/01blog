package com.blog.backend.exception;

import com.blog.backend.dto.error.ErrorResponse;
import com.blog.backend.dto.error.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 Not Found Exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "USER_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(
            PostNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "POST_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFoundException(
            CommentNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "COMMENT_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotificationNotFoundException(
            NotificationNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "NOTIFICATION_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReportNotFoundException(
            ReportNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "REPORT_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 401 Unauthorized Exceptions
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "AUTHENTICATION_FAILED",
                "Authentication failed: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_CREDENTIALS",
                "Invalid username/email or password",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // 403 Forbidden Exceptions
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "FORBIDDEN",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "ACCESS_DENIED",
                "Access denied: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BannedUserException.class)
    public ResponseEntity<ErrorResponse> handleBannedUserException(
            BannedUserException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "USER_BANNED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    // 400 Bad Request Exceptions
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "USER_ALREADY_EXISTS",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AlreadySubscribedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadySubscribedException(
            AlreadySubscribedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "ALREADY_SUBSCRIBED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotSubscribedException.class)
    public ResponseEntity<ErrorResponse> handleNotSubscribedException(
            NotSubscribedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "NOT_SUBSCRIBED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileException(
            InvalidFileException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_FILE",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Validation Errors (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        ValidationErrorResponse error = new ValidationErrorResponse(
                "VALIDATION_FAILED",
                "Input validation failed",
                request.getRequestURI()
        );

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
