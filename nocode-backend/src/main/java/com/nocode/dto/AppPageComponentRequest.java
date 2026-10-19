package com.nocode.dto;

import java.util.Map;

import com.nocode.domain.PageComponentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AppPageComponentRequest {

    @NotBlank(message = "Component name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Component name must be between 2 and 100 characters"
    )
    private String name;

    @NotNull(message = "Component type is required")
    private PageComponentType type;

    @NotNull(message = "Position is required")
    @Positive(message = "Position must be greater than 0")
    private Integer position;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PageComponentType getType() {
        return type;
    }

    public void setType(PageComponentType type) {
        this.type = type;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
    private Map<String, Object> config;
    public Map<String, Object> getConfig() {
    return config;
}

public void setConfig(Map<String, Object> config) {
    this.config = config;
}
}