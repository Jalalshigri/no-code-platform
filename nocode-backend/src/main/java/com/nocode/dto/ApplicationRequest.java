package com.nocode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApplicationRequest {

    @NotBlank(message = "Application name is required")
    @Size(
        min = 3,
        max = 100,
        message = "Application name must be between 3 and 100 characters"
    )
    private String name;

    @Size(
        max = 500,
        message = "Description must not exceed 500 characters"
    )
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}