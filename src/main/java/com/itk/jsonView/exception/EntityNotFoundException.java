package com.itk.jsonView.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BusinessException {
    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s with id %s not found", entityName, id), HttpStatus.NOT_FOUND);
    }
}
