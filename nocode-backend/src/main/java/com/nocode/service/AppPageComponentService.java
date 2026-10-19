package com.nocode.service;

import com.nocode.domain.AppPage;
import com.nocode.domain.AppPageComponent;
import com.nocode.dto.AppPageComponentRequest;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.transaction.Transactional;

@ApplicationScoped
public class AppPageComponentService {

    @Transactional
    public AppPageComponent create(
            Long applicationId,
            Long pageId,
            AppPageComponentRequest request) {

        AppPage page = AppPage.findById(pageId);

        if (page == null) {
            return null;
        }

        if (!page.getApplication().id.equals(applicationId)) {
            return null;
        }

        AppPageComponent component = new AppPageComponent();

        component.setName(request.getName().trim());
        component.setType(request.getType());
        component.setPosition(request.getPosition());
        component.setPage(page);
        component.setPosition(request.getPosition());
        component.setPage(page);
        component.setConfig(request.getConfig());
        component.persist();

        return component;
    }

    public java.util.List<AppPageComponent> findByPageId(
            Long applicationId,
            Long pageId) {

        AppPage page = AppPage.findById(pageId);

        if (page == null) {
            return java.util.List.of();
        }

        if (!page.getApplication().id.equals(applicationId)) {
            return java.util.List.of();
        }

        return AppPageComponent.list(
                "page.id = ?1",
                pageId);
    }

    public AppPageComponent findById(
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

        if (!component.getPage().getApplication().id.equals(applicationId)) {
            return null;
        }

        return component;
    }

    @Transactional
    public AppPageComponent update(
            Long applicationId,
            Long pageId,
            Long componentId,
            AppPageComponentRequest request) {

        AppPageComponent component = findById(applicationId, pageId, componentId);

        if (component == null) {
            return null;
        }

        component.setName(request.getName().trim());
        component.setType(request.getType());
        component.setPosition(request.getPosition());
        component.setConfig(request.getConfig());
        return component;
    }

    @Transactional
    public boolean delete(
            Long applicationId,
            Long pageId,
            Long componentId) {

        AppPageComponent component = findById(applicationId, pageId, componentId);

        if (component == null) {
            return false;
        }

        component.delete();

        return true;
    }
}