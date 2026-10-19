package com.nocode.service;

import com.nocode.domain.AppPage;
import com.nocode.domain.Application;
import com.nocode.dto.AppPageRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AppPageService {

    @Transactional
    public AppPage create(Long applicationId, AppPageRequest request) {

        Application application = Application.findById(applicationId);

        if (application == null) {
            return null;
        }
        String name = request.getName().trim();

        if (existsByName(applicationId, name)) {
            throw new DuplicatePageException(
                    "Page with this name already exists in this application");
        }
        AppPage page = new AppPage();

        page.setName(name);
        page.setRoute(request.getRoute().trim());
        page.setTitle(
                request.getTitle() == null
                        ? null
                        : request.getTitle().trim());

        page.setApplication(application);

        page.persist();

        return page;
    }

    @Transactional
    public AppPage update(
            Long applicationId,
            Long pageId,
            AppPageRequest request) {

        AppPage page = findById(applicationId, pageId);

        if (page == null) {
            return null;
        }

        String name = request.getName().trim();

        if (!page.getName().equalsIgnoreCase(name)
                && existsByName(applicationId, name)) {
            throw new DuplicatePageException(
                    "Page with this name already exists in this application");
        }

        page.setName(name);
        page.setRoute(request.getRoute().trim());
        page.setTitle(
                request.getTitle() == null
                        ? null
                        : request.getTitle().trim());

        return page;
    }

    @Transactional
    public boolean delete(Long applicationId, Long pageId) {

        AppPage page = findById(applicationId, pageId);

        if (page == null) {
            return false;
        }

        page.delete();

        return true;
    }

    public boolean existsByName(Long applicationId, String name) {
        return AppPage.count(
                "application.id = ?1 AND LOWER(name) = LOWER(?2)",
                applicationId,
                name) > 0;
    }

    public java.util.List<AppPage> findByApplicationId(Long applicationId) {
        return AppPage.list(
                "application.id = ?1",
                applicationId);
    }

    public AppPage findById(Long applicationId, Long pageId) {

        AppPage page = AppPage.findById(pageId);

        if (page == null) {
            return null;
        }

        if (!page.getApplication().id.equals(applicationId)) {
            return null;
        }

        return page;
    }
}