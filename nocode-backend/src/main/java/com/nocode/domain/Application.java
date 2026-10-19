package com.nocode.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applications")
public class Application extends PanacheEntity {

    private String name;

    private String description;
    @JsonIgnore
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppEntity> entities = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AppEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<AppEntity> entities) {
        this.entities = entities;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppPage> pages = new ArrayList<>();

    public List<AppPage> getPages() {
        return pages;
    }

    public void setPages(List<AppPage> pages) {
        this.pages = pages;
    }
}