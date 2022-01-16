package com.example.demo.railway;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.base.Throwables;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class WebfluxExceptionHandler {
	@ExceptionHandler
	  public ResponseEntity<ErrorResponse> handleSingleValidationException(
			  ValidationException ex, ServerWebExchange exchange) {
	    log.error(Throwables.getStackTraceAsString(ex));
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	        .body(
	            ErrorResponse.builder()
	                .timestamp(Instant.now())
	                .status(HttpStatus.BAD_REQUEST.value())
	                .path(exchange.getRequest().getPath().toString())
	                .error("Input Validation Error")
	                .message(ex.getMessage())
	                .build());
	  }
	
	@ExceptionHandler
	  public ResponseEntity<ErrorResponse> handleMultipleValidationException(
			  Exception ex, ServerWebExchange exchange) {
	    log.error(Throwables.getStackTraceAsString(ex));
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	        .body(
	            ErrorResponse.builder()
	                .timestamp(Instant.now())
	                .status(HttpStatus.BAD_REQUEST.value())
	                .path(exchange.getRequest().getPath().toString())
	                .error("Input Validation Error")
	                .message(getSurpressedExceptions(ex))
	                .build());
	  }

	public static String getSurpressedExceptions(Throwable ex) {
		if (ex.getSuppressed().length == 0) {
			return ex.getMessage();
		} else {
			return Arrays.asList(ex.getSuppressed()).stream()
					.map(Throwable::getMessage)
					.filter(m -> !m.contains("__checkpoint"))
					.collect(Collectors.joining(", "));
		}
	}
}
