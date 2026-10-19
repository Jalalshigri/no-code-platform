package com.nocode.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class AppEntityRequest {
      @NotBlank(message = "Entity name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Entity name must be between 2 and 100 characters"
    )
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
