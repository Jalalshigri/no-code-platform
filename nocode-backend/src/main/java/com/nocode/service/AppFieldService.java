package com.nocode.service;

import java.util.List;

import com.nocode.domain.AppEntity;
import com.nocode.domain.AppField;
import com.nocode.domain.FieldType;
import com.nocode.dto.AppFieldRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AppFieldService {

    @Transactional
    public AppField create(
            Long applicationId,
            Long entityId,
            AppFieldRequest request) {

        AppEntity entity = AppEntity.findById(entityId);

        if (entity == null) {
            return null;
        }
        if (!entity.getApplication().id.equals(applicationId)) {
            return null;
        }

        String name = request.getName().trim();

        boolean exists = AppField.count(
                "entity.id = ?1 AND LOWER(name) = LOWER(?2)",
                entityId,
                name) > 0;

        if (exists) {
            throw new DuplicateFieldException(
                    "Field with this name already exists in this entity");
        }
        if (request.getMinLength() != null
                && request.getMaxLength() != null
                && request.getMinLength() > request.getMaxLength()) {

            throw new InvalidFieldConfigurationException(
                    "minLength cannot be greater than maxLength");
        }
        if (request.getMinValue() != null
                && request.getMaxValue() != null
                && request.getMinValue() > request.getMaxValue()) {

            throw new InvalidFieldConfigurationException(
                    "minValue cannot be greater than maxValue");
        }

        // 3. Length constraints must match field type
        if ((request.getMinLength() != null
                || request.getMaxLength() != null)
                && request.getType() != FieldType.STRING
                && request.getType() != FieldType.TEXT) {

            throw new InvalidFieldConfigurationException(
                    "minLength and maxLength can only be used with STRING or TEXT fields");
        }
        if ((request.getMinValue() != null
                || request.getMaxValue() != null)
                && request.getType() != FieldType.INTEGER
                && request.getType() != FieldType.DECIMAL) {

            throw new InvalidFieldConfigurationException(
                    "minValue and maxValue can only be used with INTEGER or DECIMAL fields");
        }
        AppField field = new AppField();

        field.setName(name);
        field.setType(request.getType());
        field.setRequired(request.isRequired());
        field.setEntity(entity);
        field.setMinLength(request.getMinLength());
        field.setMaxLength(request.getMaxLength());
        field.setMinValue(request.getMinValue());
        field.setMaxValue(request.getMaxValue());

        field.persist();

        return field;
    }

public List<AppField> findByEntityId(
        Long applicationId,
        Long entityId) {

    return AppField.list(
            "entity.id = ?1 AND entity.application.id = ?2",
            entityId,
            applicationId);
}

    @Transactional
    public boolean delete( Long applicationId,
            Long entityId,Long fieldId) {

        AppField field = findById(applicationId,entityId,fieldId);

        if (field == null) {
            return false;
        }

        field.delete();

        return true;
    }

    @Transactional
    public AppField update(
            Long applicationId,
            Long entityId,
            Long fieldId,
            AppFieldRequest request) {

        AppField field = findById(applicationId,
                entityId,
                fieldId);

        if (field == null) {
            return null;
        }
        if (!field.getEntity().id.equals(entityId)) {
            return null;
        }

        String name = request.getName().trim();

        boolean exists = AppField.count(
                "entity.id = ?1 AND LOWER(name) = LOWER(?2) AND id <> ?3",
                field.getEntity().id,
                name,
                fieldId) > 0;

        if (exists) {
            throw new DuplicateFieldException(
                    "Field with this name already exists in this entity");
        }

        // length rage validate
        if (request.getMinLength() != null
                && request.getMaxLength() != null
                && request.getMinLength() > request.getMaxLength()) {

            throw new InvalidFieldConfigurationException(
                    "minLength cannot be greater than maxLength");
        }

        // numeric value validate
        if (request.getMinValue() != null
                && request.getMaxValue() != null
                && request.getMinValue() > request.getMaxValue()) {

            throw new InvalidFieldConfigurationException(
                    "minValue cannot be greater than maxValue");
        }
        // 3. Length constraints must match field type

        if ((request.getMinLength() != null
                || request.getMaxLength() != null)
                && request.getType() != FieldType.STRING
                && request.getType() != FieldType.TEXT) {

            throw new InvalidFieldConfigurationException(
                    "minLength and maxLength can only be used with STRING or TEXT fields");
        }

        if ((request.getMinValue() != null
                || request.getMaxValue() != null)
                && request.getType() != FieldType.INTEGER
                && request.getType() != FieldType.DECIMAL) {

            throw new InvalidFieldConfigurationException(
                    "minValue and maxValue can only be used with INTEGER or DECIMAL fields");
        }
        field.setName(name);
        field.setType(request.getType());
        field.setRequired(request.isRequired());
        field.setMinLength(request.getMinLength());
        field.setMaxLength(request.getMaxLength());
        field.setMinValue(request.getMinValue());
        field.setMaxValue(request.getMaxValue());
        return field;
    }

    public AppField findById(
            Long applicationId,
            Long entityId,
            Long fieldId) {

        AppField field = AppField.findById(fieldId);

        if (field == null) {
            return null;
        }

        if (!field.getEntity().id.equals(entityId)) {
            return null;
        }

        if (!field.getEntity()
                .getApplication().id
                .equals(applicationId)) {
            return null;
        }

        return field;
    }


}