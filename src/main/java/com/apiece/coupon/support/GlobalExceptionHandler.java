package com.apiece.coupon.support;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomain(DomainException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), Objects.requireNonNullElse(ex.getMessage(), ""));

        problemDetail.setTitle(humanize(ex.getCode()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("code", ex.getCode());
        return ResponseEntity.status(ex.getHttpStatus()).body(problemDetail);
    }

    private String humanize(String code) {
        return Arrays.stream(code.split("_"))
                .map(word -> word.isEmpty()
                        ? word
                        : word.substring(0, 1).toUpperCase(Locale.ROOT)
                          + word.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }
}
