package com.nocode.domain;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_page_components")
public class AppPageComponent extends PanacheEntity {

    private String name;

    @Enumerated(EnumType.STRING)
    private PageComponentType type;

    private Integer position;

    @ManyToOne(optional = false)
    private AppPage page;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PageComponentType getType() {
        return type;
    }

    public void setType(PageComponentType type) {
        this.type = type;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public AppPage getPage() {
        return page;
    }

    public void setPage(AppPage page) {
        this.page = page;
    }
    @JdbcTypeCode(SqlTypes.JSON)
@Column(columnDefinition = "jsonb")
private Map<String, Object> config;
public Map<String, Object> getConfig() {
    return config;
}

public void setConfig(Map<String, Object> config) {
    this.config = config;
}

}