package com.nocode.resource;

import com.nocode.service.InvalidFieldConfigurationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class InvalidFieldConfigurationExceptionHandler
                implements ExceptionMapper<InvalidFieldConfigurationException> {

        @Override
        public Response toResponse(
                        InvalidFieldConfigurationException exception) {

                Map<String, String> response = Map.of(
                                "error", exception.getMessage());

                return Response
                                .status(Response.Status.BAD_REQUEST)
                                .entity(response)
                                .build();
        }
}