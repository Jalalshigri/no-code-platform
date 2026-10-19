package com.nocode.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nocode.domain.AppEntity;
import com.nocode.domain.AppField;
import com.nocode.domain.AppRecord;
import com.nocode.domain.FieldType;
import com.nocode.dto.AppRecordRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AppRecordService {

    public List<AppRecord> findByEntityId(
            Long applicationId,
            Long entityId) {

        return AppRecord.list(
                "entity.id = ?1 AND entity.application.id = ?2",
                entityId,
                applicationId);
    }

    public AppRecord findById(Long applicationId, Long entityId, Long recordId) {

        AppRecord record = AppRecord.findById(recordId);

        if (record == null) {
            return null;
        }

        if (!record.getEntity().id.equals(entityId)) {
            return null;
        }

        if (!record.getEntity()
                .getApplication().id
                .equals(applicationId)) {
            return null;
        }

        return record;
    }

    private boolean required;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    private Map<String, Object> validateAndNormalize(Long entityId, AppRecordRequest request) {
        Map<String, Object> normalizedData = new HashMap<>();
        for (Map.Entry<String, Object> entry : request.getData().entrySet()) {

            String submittedFieldName = entry.getKey();
            Object value = entry.getValue();

            AppField field = AppField.find(
                    "entity.id = ?1 AND LOWER(name) = LOWER(?2)",
                    entityId,
                    submittedFieldName).firstResult();

            if (field == null) {
                throw new InvalidRecordException(
                        "Unknown field: " + submittedFieldName);
            }

            if (!isValidType(value, field.getType())) {
                throw new InvalidRecordException(
                        "Invalid type for field '" + submittedFieldName
                                + "'. Expected "
                                + field.getType());
            }
            if (value instanceof Number number) {

                if (field.getMinValue() != null
                        && number.doubleValue() < field.getMinValue()) {

                    throw new InvalidRecordException(
                            "Field '" + submittedFieldName
                                    + "' must be at least "
                                    + field.getMinValue());
                }

                if (field.getMaxValue() != null
                        && number.doubleValue() > field.getMaxValue()) {

                    throw new InvalidRecordException(
                            "Field '" + submittedFieldName
                                    + "' must not exceed "
                                    + field.getMaxValue());
                }
            }
            if (value instanceof String stringValue) {

                if (field.getMinLength() != null
                        && stringValue.length() < field.getMinLength()) {

                    throw new InvalidRecordException(
                            "Field '" + submittedFieldName
                                    + "' must be at least "
                                    + field.getMinLength()
                                    + " characters");
                }

                if (field.getMaxLength() != null
                        && stringValue.length() > field.getMaxLength()) {

                    throw new InvalidRecordException(
                            "Field '" + submittedFieldName
                                    + "' must not exceed "
                                    + field.getMaxLength()
                                    + " characters");
                }
            }
            normalizedData.put(field.getName(), value);
        }
        for (AppField field : AppField.<AppField>list("entity.id", entityId)) {

            boolean fieldPresent = request.getData().entrySet().stream()
                    .anyMatch(entry -> {

                        if (!entry.getKey().equalsIgnoreCase(field.getName())) {
                            return false;
                        }

                        Object value = entry.getValue();

                        if (value == null) {
                            return false;
                        }

                        if ((field.getType() == FieldType.STRING
                                || field.getType() == FieldType.TEXT)
                                && value instanceof String stringValue) {

                            return !stringValue.isBlank();
                        }

                        return true;
                    });

            if (field.isRequired() && !fieldPresent) {
                throw new InvalidRecordException(
                        "Required field is missing: " + field.getName());
            }
        }
        return normalizedData;
    }

    @Transactional
    public AppRecord create(
            Long applicationId,
            Long entityId,
            AppRecordRequest request) {

        AppEntity entity = AppEntity.findById(entityId);

        if (entity == null) {
            return null;
        }
        if (!entity.getApplication().id.equals(applicationId)) {
            return null;
        }
        Map<String, Object> normalizedData = validateAndNormalize(entityId, request);
        AppRecord appRecord = new AppRecord();

        appRecord.setEntity(entity);
        appRecord.setData(normalizedData);

        appRecord.persist();

        return appRecord;
    }

    @Transactional
    public AppRecord update(
            Long applicationId,
            Long entityId,
            Long recordId,
            AppRecordRequest request) {

        AppEntity entity = AppEntity.findById(entityId);

        if (entity == null) {
            return null;
        }

        if (!entity.getApplication().id.equals(applicationId)) {
            return null;
        }

        AppRecord record = AppRecord.findById(recordId);

        if (record == null) {
            return null;
        }

        if (!record.getEntity().id.equals(entityId)) {
            return null;
        }

        Map<String, Object> normalizedData = validateAndNormalize(entityId, request);

        record.setData(normalizedData);

        return record;
    }

    @Transactional
    public boolean delete(
            Long applicationId,
            Long entityId,
            Long recordId) {

        AppRecord record = AppRecord.findById(recordId);

        if (record == null) {
            return false;
        }

        if (!record.getEntity().id.equals(entityId)) {
            return false;
        }
        if (!record.getEntity()
                .getApplication().id
                .equals(applicationId)) {
            return false;
        }
        record.delete();

        return true;
    }

    private boolean isValidType(
            Object value,
            FieldType fieldType) {

        if (value == null) {
            return true;
        }

        return switch (fieldType) {

            case STRING, TEXT ->
                value instanceof String;

            case INTEGER ->
                value instanceof Integer
                        || value instanceof Long;

            case DECIMAL ->
                value instanceof Number;

            case BOOLEAN ->
                value instanceof Boolean;

            case DATE -> {
                if (!(value instanceof String stringValue)) {
                    yield false;
                }

                try {
                    LocalDate.parse(stringValue);
                    yield true;
                } catch (DateTimeParseException e) {
                    yield false;
                }
            }

            case DATETIME -> {
                if (!(value instanceof String stringValue)) {
                    yield false;
                }

                try {
                    LocalDateTime.parse(stringValue);
                    yield true;
                } catch (DateTimeParseException e) {
                    yield false;
                }
            }
        };
    }

    public AppRecord buildRecord(
            AppEntity entity,
            java.util.List<AppField> fields,
            java.util.Map<String, Object> data) {

        AppRecord record = new AppRecord();

        record.setEntity(entity);

        java.util.Map<String, Object> filteredData = new java.util.HashMap<>();

        for (AppField field : fields) {

            String fieldName = field.getName();

            if (data.containsKey(fieldName)) {
                filteredData.put(
                        fieldName,
                        data.get(fieldName));
            }
        }

        record.setData(filteredData);

        return record;
    }

    public AppRecord saveRecord(AppRecord record) {

        record.persist();

        return record;
    }

    public AppRecord findRecord(
            AppEntity entity,
            Long recordId) {

        AppRecord record = AppRecord.findById(recordId);

        if (record == null) {
            return null;
        }

        if (record.getEntity() == null) {
            return null;
        }

        if (!record.getEntity().id.equals(entity.id)) {
            return null;
        }

        return record;
    }

    public AppRecord updateRecord(
            AppRecord record,
            java.util.List<AppField> fields,
            java.util.Map<String, Object> data) {

        java.util.Map<String, Object> updatedData = new java.util.HashMap<>();

        // Preserve the existing record data.
        if (record.getData() != null) {
            updatedData.putAll(record.getData());
        }

        // Update only configured fields supplied in the request.
        for (AppField field : fields) {

            String fieldName = field.getName();

            if (data.containsKey(fieldName)) {
                updatedData.put(
                        fieldName,
                        data.get(fieldName));
            }
        }

        record.setData(updatedData);

        return record;
    }

    public boolean deleteRecord(AppRecord record) {

        if (record == null) {
            return false;
        }

        record.delete();

        return true;
    }
}