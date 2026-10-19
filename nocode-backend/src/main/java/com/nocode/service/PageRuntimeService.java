package com.nocode.service;

import com.nocode.domain.AppEntity;
import com.nocode.domain.AppField;
import com.nocode.domain.AppPageComponent;
import com.nocode.domain.AppRecord;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@ApplicationScoped
public class PageRuntimeService {

    @PersistenceContext
    EntityManager entityManager;

    public AppPageComponent findComponent(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppPageComponent component = AppPageComponent.findById(componentId);

        if (component == null) {
            return null;
        }

        if (!component.getPage().id.equals(pageId)) {
            return null;
        }

        if (!component.getPage()
                .getApplication().id
                .equals(applicationId)) {
            return null;
        }

        return component;
    }

    public Long getDataSourceEntityId(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppPageComponent component = findComponent(
                applicationId,
                pageId,
                componentId);

        if (component == null) {
            return null;
        }

        if (component.getConfig() == null) {
            return null;
        }

        Object dataSourceObject = component.getConfig().get("dataSource");

        if (!(dataSourceObject instanceof java.util.Map<?, ?> dataSource)) {
            return null;
        }

        Object entityIdObject = dataSource.get("entityId");

        if (entityIdObject instanceof Number number) {
            return number.longValue();
        }

        return null;
    }

    public AppEntity findDataSourceEntity(
            Long applicationId,
            Long pageId,
            Long componentId) {

        Long entityId = getDataSourceEntityId(
                applicationId,
                pageId,
                componentId);

        if (entityId == null) {
            return null;
        }

        AppEntity entity = AppEntity.findById(entityId);

        if (entity == null) {
            return null;
        }

        if (!entity.getApplication().id
                .equals(applicationId)) {
            return null;
        }

        return entity;
    }

    public java.util.List<AppField> findDataSourceFields(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppEntity entity = findDataSourceEntity(
                applicationId,
                pageId,
                componentId);

        if (entity == null) {
            return java.util.List.of();
        }

        return AppField.list(
                "entity.id = ?1",
                entity.id);
    }

    public java.util.Map<String, Object> getFormMetadata(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppEntity entity = findDataSourceEntity(
                applicationId,
                pageId,
                componentId);

        if (entity == null) {
            return java.util.Map.of();
        }

        AppPageComponent component = findComponent(
                applicationId,
                pageId,
                componentId);

        if (component == null ||
                component.getConfig() == null) {
            return java.util.Map.of();
        }

        Object fieldsObject = component.getConfig().get("fields");

        if (!(fieldsObject instanceof java.util.List<?> fieldIds)) {
            return java.util.Map.of();
        }

        java.util.List<AppField> allFields = findDataSourceFields(
                applicationId,
                pageId,
                componentId);

        java.util.List<AppField> selectedFields = new java.util.ArrayList<>();

        for (Object fieldIdObject : fieldIds) {

            if (!(fieldIdObject instanceof Number number)) {
                continue;
            }

            Long fieldId = number.longValue();

            for (AppField field : allFields) {

                if (field.id.equals(fieldId)) {
                    selectedFields.add(field);
                    break;
                }
            }
        }

        java.util.Map<String, Object> metadata = new java.util.LinkedHashMap<>();

        java.util.List<java.util.Map<String, Object>> formFields = new java.util.ArrayList<>();

        for (AppField field : selectedFields) {

            java.util.Map<String, Object> fieldMetadata = new java.util.LinkedHashMap<>();

            fieldMetadata.put("id", field.id);
            fieldMetadata.put("name", field.getName());
            fieldMetadata.put("type", field.getType());
            fieldMetadata.put("required", field.isRequired());
            fieldMetadata.put("minLength", field.getMinLength());
            fieldMetadata.put("maxLength", field.getMaxLength());
            fieldMetadata.put("minValue", field.getMinValue());
            fieldMetadata.put("maxValue", field.getMaxValue());

            String inputType;

            switch (field.getType()) {

                case STRING:
                    inputType = "text";
                    break;

                case TEXT:
                    inputType = "textarea";
                    break;

                case INTEGER:
                    inputType = "number";
                    break;

                case DECIMAL:
                    inputType = "number";
                    break;

                case BOOLEAN:
                    inputType = "checkbox";
                    break;

                case DATE:
                    inputType = "date";
                    break;

                case DATETIME:
                    inputType = "datetime-local";
                    break;

                default:
                    inputType = "text";
            }

            fieldMetadata.put("inputType", inputType);

            formFields.add(fieldMetadata);
        }

        metadata.put("entityId", entity.id);
        metadata.put("entityName", entity.getName());
        metadata.put("fields", formFields);

        return metadata;
    }

    public java.util.List<String> getConfiguredColumns(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppPageComponent component = findComponent(
                applicationId,
                pageId,
                componentId);

        if (component == null) {
            return java.util.List.of();
        }

        if (component.getConfig() == null) {
            return java.util.List.of();
        }

        Object columnsObject = component.getConfig().get("columns");

        if (!(columnsObject instanceof java.util.List<?> columns)) {
            return java.util.List.of();
        }

        return columns.stream()
                .filter(value -> value instanceof String)
                .map(value -> (String) value)
                .toList();
    }

  public java.util.List<AppField> findConfiguredFields(
        Long applicationId,
        Long pageId,
        Long componentId) {

    AppPageComponent component =
            findComponent(
                    applicationId,
                    pageId,
                    componentId);

    if (component == null ||
            component.getConfig() == null) {

        return java.util.List.of();
    }

    Object fieldsObject =
            component.getConfig().get("fields");

    if (!(fieldsObject instanceof java.util.List<?> fieldIds)) {
        return java.util.List.of();
    }

    java.util.List<AppField> allFields =
            findDataSourceFields(
                    applicationId,
                    pageId,
                    componentId);

    java.util.List<AppField> configuredFields =
            new java.util.ArrayList<>();

    for (Object fieldIdObject : fieldIds) {

        if (!(fieldIdObject instanceof Number number)) {
            continue;
        }

        Long fieldId = number.longValue();

        for (AppField field : allFields) {

            if (field.id.equals(fieldId)) {
                configuredFields.add(field);
                break;
            }
        }
    }

    return configuredFields;
}
    public java.util.List<AppRecord> findDataSourceRecords(
            Long applicationId,
            Long pageId,
            Long componentId) {

        return findDataSourceRecordsPaged(
                applicationId,
                pageId,
                componentId,
                0,
                "", "id", "asc");
    }

    public java.util.List<java.util.Map<String, Object>> buildTableRows(
            Long applicationId,
            Long pageId,
            Long componentId,
            int requestedPage,
            String search,
            String sortField,
            String sortDirection) {

        java.util.List<String> columns = getConfiguredColumns(
                applicationId,
                pageId,
                componentId);

        java.util.List<AppRecord> records = findDataSourceRecordsPaged(
                applicationId,
                pageId,
                componentId,
                requestedPage,
                search,
                sortField,
                sortDirection);
        java.util.List<java.util.Map<String, Object>> rows = new java.util.ArrayList<>();

        for (AppRecord record : records) {

            java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();

            for (String column : columns) {

                if (column.equalsIgnoreCase("id")) {
                    row.put("id", record.id);
                    continue;
                }

                Object value = null;

                if (record.getData() != null) {

                    value = record.getData().get(column);

                    if (value == null) {

                        for (java.util.Map.Entry<String, Object> entry : record.getData().entrySet()) {

                            if (entry.getKey()
                                    .equalsIgnoreCase(column)) {

                                value = entry.getValue();
                                break;
                            }
                        }
                    }
                }

                row.put(column, value);
            }

            rows.add(row);
        }

        return rows;
    }

    public java.util.Map<String, Object> getPaginationMetadata(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppPageComponent component = findComponent(
                applicationId,
                pageId,
                componentId);

        if (component == null
                || component.getConfig() == null) {

            return java.util.Map.of(
                    "page", 0,
                    "pageSize", 50);
        }

        Object pageSizeObject = component.getConfig().get("pageSize");

        int pageSize = 50;

        if (pageSizeObject instanceof Number number) {
            pageSize = number.intValue();
        }

        if (pageSize <= 0) {
            pageSize = 50;
        }

        return java.util.Map.of(
                "page", 0,
                "pageSize", pageSize);
    }

    public int getPageSize(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppPageComponent component = findComponent(
                applicationId,
                pageId,
                componentId);

        if (component == null
                || component.getConfig() == null) {

            return 50;
        }

        Object pageSizeObject = component.getConfig().get("pageSize");

        if (pageSizeObject instanceof Number number) {

            int pageSize = number.intValue();

            if (pageSize > 0 && pageSize <= 100) {
                return pageSize;
            }
        }

        return 50;
    }

    public long getDataSourceRecordCount(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppEntity entity = findDataSourceEntity(
                applicationId,
                pageId,
                componentId);

        if (entity == null) {
            return 0;
        }

        return AppRecord.count(
                "entity.id = ?1 AND entity.application.id = ?2",
                entity.id,
                applicationId);
    }

    public int getPage(
            Long applicationId,
            Long pageId,
            Long componentId,
            int page) {

        if (page < 0) {
            return 0;
        }

        return page;
    }

    public int getPageOffset(
            Long applicationId,
            Long pageId,
            Long componentId) {

        int page = getPage(
                applicationId,
                pageId,
                componentId,
                0);

        int pageSize = getPageSize(
                applicationId,
                pageId,
                componentId);

        return page * pageSize;
    }

    private java.util.List<AppRecord> findDataSourceRecordsBySearch(
            Long applicationId,
            Long pageId,
            Long componentId,
            int page,
            String search,
            String sortField,
            String sortDirection) {

        AppEntity entity = findDataSourceEntity(
                applicationId,
                pageId,
                componentId);

        if (entity == null) {
            return java.util.List.of();
        }

        int pageSize = getPageSize(
                applicationId,
                pageId,
                componentId);

        java.util.List<String> columns = getConfiguredColumns(
                applicationId,
                pageId,
                componentId);
        AppField sortAppField = null;

        for (AppField field : entity.getFields()) {

            if (field.getName().equalsIgnoreCase(sortField)) {
                sortAppField = field;
                break;
            }
        }

        if (columns.isEmpty()) {
            return java.util.List.of();
        }

        StringBuilder sql = new StringBuilder(
                "SELECT ar.* " +
                        "FROM app_records ar " +
                        "JOIN app_entities ae ON ar.entity_id = ae.id " +
                        "WHERE ar.entity_id = :entityId " +
                        "AND ae.application_id = :applicationId " +
                        "AND ( ");

        boolean firstCondition = true;
        int fieldIndex = 0;

        for (String column : columns) {

            if (column.equalsIgnoreCase("id")) {

                sql.append(
                        "CAST(ar.id AS TEXT) ILIKE :search ");

            } else {

                if (!firstCondition) {
                    sql.append(" OR ");
                }

                sql.append(
                        "ar.data ->> :field"
                                + fieldIndex
                                + " ILIKE :search ");

                fieldIndex++;
            }

            firstCondition = false;

            if (column.equalsIgnoreCase("id")) {
                sql.append("");
            }
        }

        sql.append(") ");

        if (sortField.equalsIgnoreCase("id")) {

            sql.append(
                    "ORDER BY ar.id "
                            + sortDirection);

        } else if (sortAppField != null
                && (sortAppField.getType() == com.nocode.domain.FieldType.INTEGER
                        || sortAppField.getType() == com.nocode.domain.FieldType.DECIMAL)) {

            sql.append(
                    "ORDER BY (ar.data ->> :sortField)::numeric "
                            + sortDirection
                            + " NULLS LAST");

        } else if (sortAppField != null
                && sortAppField.getType() == com.nocode.domain.FieldType.DATE) {

            sql.append(
                    "ORDER BY (ar.data ->> :sortField)::date "
                            + sortDirection
                            + " NULLS LAST");

        } else if (sortAppField != null
                && sortAppField.getType() == com.nocode.domain.FieldType.DATETIME) {

            sql.append(
                    "ORDER BY (ar.data ->> :sortField)::timestamp "
                            + sortDirection
                            + " NULLS LAST");

        } else {

            sql.append(
                    "ORDER BY ar.data ->> :sortField "
                            + sortDirection
                            + " NULLS LAST");
        }

        Query query = entityManager.createNativeQuery(
                sql.toString(),
                AppRecord.class);

        query.setParameter("entityId", entity.id);
        query.setParameter(
                "applicationId",
                applicationId);

        query.setParameter(
                "search",
                "%" + search.trim() + "%");

        fieldIndex = 0;

        for (String column : columns) {

            if (!column.equalsIgnoreCase("id")) {

                query.setParameter(
                        "field" + fieldIndex,
                        column);

                fieldIndex++;
            }
        }

        if (!sortField.equalsIgnoreCase("id")) {

            query.setParameter(
                    "sortField",
                    sortField);
        }

        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    public java.util.List<AppRecord> findDataSourceRecordsPaged(
            Long applicationId,
            Long pageId,
            Long componentId,
            int requestedPage,
            String search,
            String sortField,
            String sortDirection) {

        AppEntity entity = findDataSourceEntity(
                applicationId,
                pageId,
                componentId);

        if (entity == null) {
            return java.util.List.of();
        }

        int page = getPage(
                applicationId,
                pageId,
                componentId,
                requestedPage);

        search = search == null
                ? ""
                : search.trim();
        if (!search.isEmpty()) {
            return findDataSourceRecordsBySearch(
                    applicationId,
                    pageId,
                    componentId,
                    requestedPage,
                    search,
                    sortField,
                    sortDirection);
        }

        java.util.List<String> columns = getConfiguredColumns(
                applicationId,
                pageId,
                componentId);

        if (columns.isEmpty()) {
            return java.util.List.of();
        }

        int pageSize = getPageSize(
                applicationId,
                pageId,
                componentId);

        // return AppRecord.find(
        // "entity.id = :entityId " +
        // "AND entity.application.id = :applicationId " +
        // "ORDER BY id",
        // io.quarkus.panache.common.Parameters
        // .with("entityId", entity.id)
        // .and("applicationId", applicationId))
        // .page(page, pageSize)
        // .list();
        StringBuilder sql = new StringBuilder(
                "SELECT ar.* " +
                        "FROM app_records ar " +
                        "JOIN app_entities ae ON ar.entity_id = ae.id " +
                        "WHERE ar.entity_id = :entityId " +
                        "AND ae.application_id = :applicationId ");

        AppField sortAppField = null;

        for (AppField field : entity.getFields()) {

            if (field.getName().equalsIgnoreCase(sortField)) {
                sortAppField = field;
                break;
            }
        }

        if (sortField.equalsIgnoreCase("id")) {

            sql.append(
                    "ORDER BY ar.id "
                            + sortDirection);

        } else if (sortAppField != null
                && (sortAppField.getType() == com.nocode.domain.FieldType.INTEGER
                        || sortAppField.getType() == com.nocode.domain.FieldType.DECIMAL
                        || sortAppField.getType() == com.nocode.domain.FieldType.DATE
                        || sortAppField.getType() == com.nocode.domain.FieldType.DATETIME)) {

            if (sortAppField.getType() == com.nocode.domain.FieldType.DATE) {

                sql.append(
                        "ORDER BY (ar.data ->> :sortField)::date "
                                + sortDirection
                                + " NULLS LAST");

            } else if (sortAppField.getType() == com.nocode.domain.FieldType.DATETIME) {

                sql.append(
                        "ORDER BY (ar.data ->> :sortField)::timestamp "
                                + sortDirection
                                + " NULLS LAST");

            } else {

                sql.append(
                        "ORDER BY (ar.data ->> :sortField)::numeric "
                                + sortDirection
                                + " NULLS LAST");
            }

        } else {

            sql.append(
                    "ORDER BY ar.data ->> :sortField "
                            + sortDirection
                            + " NULLS LAST");
        }

        Query query = entityManager.createNativeQuery(
                sql.toString(),
                AppRecord.class);

        query.setParameter(
                "entityId",
                entity.id);

        query.setParameter(
                "applicationId",
                applicationId);

        if (!sortField.equalsIgnoreCase("id")) {

            query.setParameter(
                    "sortField",
                    sortField);
        }

        query.setFirstResult(
                page * pageSize);

        query.setMaxResults(
                pageSize);

        return query.getResultList();
    }

    public int getTotalPages(
            Long applicationId,
            Long pageId,
            Long componentId,
            String search) {

        long totalRecords = getDataSourceRecordCount(
                applicationId,
                pageId,
                componentId,
                search);

        int pageSize = getPageSize(
                applicationId,
                pageId,
                componentId);

        if (totalRecords == 0) {
            return 0;
        }

        return (int) Math.ceil(
                (double) totalRecords / pageSize);
    }

    public long getDataSourceRecordCount(
            Long applicationId,
            Long pageId,
            Long componentId,
            String search) {

        if (search == null || search.trim().isEmpty()) {
            return getDataSourceRecordCount(
                    applicationId,
                    pageId,
                    componentId);
        }

        AppEntity entity = findDataSourceEntity(
                applicationId,
                pageId,
                componentId);

        if (entity == null) {
            return 0;
        }

        java.util.List<String> columns = getConfiguredColumns(
                applicationId,
                pageId,
                componentId);

        if (columns.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) " +
                        "FROM app_records ar " +
                        "JOIN app_entities ae ON ar.entity_id = ae.id " +
                        "WHERE ar.entity_id = :entityId " +
                        "AND ae.application_id = :applicationId " +
                        "AND ( ");

        boolean firstCondition = true;
        int fieldIndex = 0;

        for (String column : columns) {

            if (!firstCondition) {
                sql.append(" OR ");
            }

            if (column.equalsIgnoreCase("id")) {

                sql.append(
                        "CAST(ar.id AS TEXT) ILIKE :search ");

            } else {

                sql.append(
                        "ar.data ->> :field"
                                + fieldIndex
                                + " ILIKE :search ");

                fieldIndex++;
            }

            firstCondition = false;
        }

        sql.append(")");

        Query query = entityManager.createNativeQuery(
                sql.toString());

        query.setParameter("entityId", entity.id);
        query.setParameter(
                "applicationId",
                applicationId);

        query.setParameter(
                "search",
                "%" + search.trim() + "%");

        fieldIndex = 0;

        for (String column : columns) {

            if (!column.equalsIgnoreCase("id")) {

                query.setParameter(
                        "field" + fieldIndex,
                        column);

                fieldIndex++;
            }
        }

        Number count = (Number) query.getSingleResult();

        return count.longValue();
    }
}