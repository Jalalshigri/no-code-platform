package com.nocode.resource;

import com.nocode.service.DuplicatePageException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class DuplicatePageExceptionHandler
        implements ExceptionMapper<DuplicatePageException> {

    @Override
    public Response toResponse(DuplicatePageException exception) {

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("error", exception.getMessage()))
                .build();
    }
}