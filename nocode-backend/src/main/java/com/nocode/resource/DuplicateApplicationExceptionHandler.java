package com.nocode.resource;

import com.nocode.service.DuplicateApplicationException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class DuplicateApplicationExceptionHandler 
        implements ExceptionMapper<DuplicateApplicationException> {

    @Override
    public Response toResponse(DuplicateApplicationException exception) {

        Map<String, String> response = Map.of(
                "error", exception.getMessage()
        );

        return Response
                .status(Response.Status.CONFLICT)
                .entity(response)
                .build();
    }
}