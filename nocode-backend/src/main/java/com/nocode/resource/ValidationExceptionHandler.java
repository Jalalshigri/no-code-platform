package com.nocode.resource;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Provider
public class ValidationExceptionHandler
        implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        Map<String, List<String>> errors = new HashMap<>();

        exception.getConstraintViolations().forEach(violation -> {

            String field = violation
                    .getPropertyPath()
                    .toString();

            field = field.substring(field.lastIndexOf('.') + 1);

            errors
        .computeIfAbsent(field, key -> new ArrayList<>())
        .add(violation.getMessage());
        });

        Map<String, Object> response = new HashMap<>();

        response.put("error", "Validation failed");
        response.put("fields", errors);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}