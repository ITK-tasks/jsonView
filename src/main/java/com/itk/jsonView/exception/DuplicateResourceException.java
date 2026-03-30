package com.itk.jsonView.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BusinessException {
  public DuplicateResourceException(String resource, String field, String value) {
    super(String.format("%s with %s : %s already exists", resource, field, value), HttpStatus.CONFLICT);
  }
}