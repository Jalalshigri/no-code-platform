package com.nocode.dto;

import com.nocode.domain.FieldType;

public class AppFieldResponse {

    private Long id;
    private String name;
    private FieldType type;
    private boolean required;
    private Integer minLength;
    private Integer maxLength;
    private Integer minValue;
    private Integer maxValue;

    public AppFieldResponse(
            Long id,
            String name,
            FieldType type,
            boolean required,
            Integer minLength,
            Integer maxLength,
            Integer minValue,
            Integer maxValue) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.required = required;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

}