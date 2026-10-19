package com.nocode.service;

import java.util.List;

import com.nocode.domain.AppEntity;
import com.nocode.domain.Application;
import com.nocode.dto.AppEntityRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AppEntityService {

    @Transactional
    public AppEntity create(Long applicationId, AppEntityRequest request) {

        // Find the parent application
        Application application = Application.findById(applicationId);

        // If application does not exist
        if (application == null) {
            return null;
        }
        String name = request.getName().trim();

        boolean exists = AppEntity.count(
                "application.id = ?1 AND LOWER(name) = LOWER(?2)",
                applicationId,
                name) > 0;

        if (exists) {
            throw new DuplicateEntityException(
                    "Entity with this name already exists in this application");
        }
        // Create the new entity
        AppEntity entity = new AppEntity();

        entity.setName(name);
        entity.setApplication(application);

        // Save to database
        entity.persist();

        return entity;
    }

    public AppEntity findById(
            Long applicationId,
            Long entityId) {

        AppEntity entity = AppEntity.findById(entityId);

        if (entity == null) {
            return null;
        }

        if (!entity.getApplication().id.equals(applicationId)) {
            return null;
        }

        return entity;
    }

    @Transactional
    public AppEntity update(
            Long applicationId,
            Long entityId,
            AppEntityRequest request) {

        AppEntity entity = findById(
                applicationId,
                entityId);

        if (entity == null) {
            return null;
        }

        String name = request.getName().trim();

        boolean exists = AppEntity.count(
                "application.id = ?1 AND LOWER(name) = LOWER(?2) AND id <> ?3",
                applicationId,
                name,
                entityId) > 0;

        if (exists) {
            throw new DuplicateEntityException(
                    "Entity with this name already exists in this application");
        }

        entity.setName(name);

        return entity;
    }

    @Transactional
    public boolean delete(
            Long applicationId,
            Long entityId) {

        AppEntity entity = findById(
                applicationId,
                entityId);

        if (entity == null) {
            return false;
        }

        entity.delete();

        return true;
    }

    public List<AppEntity> findByApplicationId(Long applicationId) {

        return AppEntity.list(
                  "application.id = ?1",
                applicationId);
    }
}