package com.nocode.service;

import com.nocode.service.DuplicateEntityException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class DuplicateEntityExceptionHandler implements ExceptionMapper<DuplicateEntityException> {
   
    @Override
    public Response toResponse(DuplicateEntityException exception) {

        return Response
                .status(Response.Status.CONFLICT)
                .entity(Map.of(
                        "error",
                        exception.getMessage()))
                .build();
    }
}