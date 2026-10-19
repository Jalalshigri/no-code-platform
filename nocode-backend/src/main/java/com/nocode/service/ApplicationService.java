package com.nocode.service;

import com.nocode.domain.Application;
import com.nocode.dto.ApplicationRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ApplicationService {
    @Transactional
    public Application create(ApplicationRequest request) {
        String name = request.getName().trim();
        if (existsByName(name)) {
            throw new DuplicateApplicationException(
                    "Application with this name already exists");
        }
        Application application = new Application();
        application.setName(name);
        application.setDescription(request.getDescription());
        application.persist();
        return application;
    }

    public Application findById(Long id) {

        return Application.findById(id);
    }

    public List<Application> findAll() {

        return Application.listAll();
    }

   public boolean existsByName(String name) {

    return Application.count(
            "LOWER(name) = LOWER(?1)",
            name
    ) > 0;
}

     public boolean existsByNameExceptId(String name, Long id) {

        return Application.count(
                "LOWER(name) = LOWER(?1) AND id <> ?2",
                name,
                id
        ) > 0;
    }

    @Transactional
    public Application update(Long id, ApplicationRequest request) {

        Application application = Application.findById(id);

        if (application == null) {
            return null;
        }
    String name=request.getName().trim();
    if(existsByNameExceptId(name, id)){
    throw new DuplicateApplicationException("Application with this name already exists");
    }
        application.setName(name);
        application.setDescription(request.getDescription());

        return application;
    }

    @Transactional
    public boolean delete(Long id) {

        Application application = Application.findById(id);

        if (application == null) {
            return false;
        }

        application.delete();

        return true;
    }
}
