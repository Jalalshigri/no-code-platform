package com.nocode.service;

import com.nocode.domain.AppField;
import com.nocode.domain.FieldType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormValidationServiceTest {

        @Test
        void shouldRejectMobileWhenTooShort() {

                AppField field = new AppField();

                field.setName("mobile");
                field.setType(FieldType.STRING);
                field.setRequired(true);
                field.setMinLength(11);
                field.setMaxLength(15);

                FormValidationService service = new FormValidationService();

                String error = service.validateField(field, "123");

                assertEquals(
                                "mobile must be at least 11 characters",
                                error);
        }

        @Test
        void shouldReturnMultipleValidationErrors() {

                AppField mobile = new AppField();

                mobile.setName("mobile");
                mobile.setType(FieldType.STRING);
                mobile.setRequired(true);
                mobile.setMinLength(11);
                mobile.setMaxLength(15);

                AppField age = new AppField();

                age.setName("age");
                age.setType(FieldType.DECIMAL);
                age.setRequired(false);
                age.setMinValue(18);
                age.setMaxValue(100);

                java.util.List<AppField> fields = java.util.List.of(mobile, age);

                java.util.Map<String, Object> data = java.util.Map.of(
                                "mobile", "123",
                                "age", 15);

                FormValidationService service = new FormValidationService();

                java.util.Map<String, String> errors = service.validateFields(fields, data);

                assertEquals(2, errors.size());

                assertEquals(
                                "mobile must be at least 11 characters",
                                errors.get("mobile"));

                assertEquals(
                                "age must be at least 18",
                                errors.get("age"));
        }
}