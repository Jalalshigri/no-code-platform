package com.nocode.dto;
import com.nocode.domain.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AppFieldRequest {

    @NotBlank(message = "Field name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Field name must be between 2 and 100 characters"
    )
    private String name;
    private Integer minLength;
    private Integer maxLength;
    private Integer minValue;
private Integer maxValue;
    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
    public Integer getMinValue() {
    return minValue;
}

public void setMinValue(Integer minValue) {
    this.minValue = minValue;
}

public Integer getMaxValue() {
    return maxValue;
}

public void setMaxValue(Integer maxValue) {
    this.maxValue = maxValue;
}
    @NotNull(message = "Field type is required")
    private FieldType type;


    private boolean required;
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }
}