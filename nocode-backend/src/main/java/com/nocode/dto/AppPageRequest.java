package com.nocode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AppPageRequest {

    @NotBlank(message = "Page name is required")
    @Size(
        min = 2,
        max = 100,
        message = "Page name must be between 2 and 100 characters"
    )
    private String name;

    @NotBlank(message = "Route is required")
    @Size(
        min = 1,
        max = 200,
        message = "Route must be between 1 and 200 characters"
    )
    private String route;

    @Size(
        max = 200,
        message = "Title cannot exceed 200 characters"
    )
    private String title;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}