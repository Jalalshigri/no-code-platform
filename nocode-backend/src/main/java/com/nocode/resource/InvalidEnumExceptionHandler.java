package com.nocode.resource;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import com.nocode.domain.FieldType;


import java.util.Map;

@Provider
public class InvalidEnumExceptionHandler
        implements ExceptionMapper<InvalidFormatException> {

    @Override
    public Response toResponse(InvalidFormatException exception) {

        Map<String, Object> response = Map.of(
                "error", "Invalid field type",
                "message", "Unsupported field type: " +
                        exception.getValue(),
                "allowedTypes",  FieldType.values()
        );

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}
