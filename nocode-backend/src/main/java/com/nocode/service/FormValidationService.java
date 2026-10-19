package com.nocode.service;

import com.nocode.domain.AppField;
import com.nocode.domain.FieldType;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FormValidationService {

    public String validateField(
            AppField field,
            Object value) {

        // 1. Required validation
        if (field.isRequired()) {

            if (value == null ||
                    value.toString().trim().isEmpty()) {

                return field.getName() + " is required";
            }
        }

        // Optional field with no value
        if (value == null) {
            return null;
        }

        String stringValue = value.toString();

        if (stringValue.trim().isEmpty()) {
            return null;
        }

        // 2. String / Text validation
        if (field.getType() == FieldType.STRING ||
                field.getType() == FieldType.TEXT) {

            if (field.getMinLength() != null &&
                    stringValue.length() < field.getMinLength()) {

                return field.getName()
                        + " must be at least "
                        + field.getMinLength()
                        + " characters";
            }

            if (field.getMaxLength() != null &&
                    stringValue.length() > field.getMaxLength()) {

                return field.getName()
                        + " must not exceed "
                        + field.getMaxLength()
                        + " characters";
            }
        }

        // 3. Numeric validation
        if (field.getType() == FieldType.INTEGER ||
                field.getType() == FieldType.DECIMAL) {

            try {
                java.math.BigDecimal number = new java.math.BigDecimal(stringValue);

                if (field.getMinValue() != null &&
                        number.compareTo(
                                java.math.BigDecimal.valueOf(
                                        field.getMinValue())) < 0) {

                    return field.getName()
                            + " must be at least "
                            + field.getMinValue();
                }

                if (field.getMaxValue() != null &&
                        number.compareTo(
                                java.math.BigDecimal.valueOf(
                                        field.getMaxValue())) > 0) {

                    return field.getName()
                            + " must not exceed "
                            + field.getMaxValue();
                }

            } catch (NumberFormatException e) {

                return field.getName()
                        + " must be a valid number";
            }
        }

        return null;
    }

    public java.util.Map<String, String> validateFields(
            java.util.List<AppField> fields,
            java.util.Map<String, Object> data) {

        java.util.Map<String, String> errors = new java.util.LinkedHashMap<>();

        for (AppField field : fields) {

            Object value = data.get(field.getName());

            String error = validateField(field, value);

            if (error != null) {
                errors.put(field.getName(), error);
            }
        }

        return errors;
    }
}