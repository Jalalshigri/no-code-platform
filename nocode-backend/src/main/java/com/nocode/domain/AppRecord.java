package com.nocode.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

@Entity
@Table(name = "app_records")
public class AppRecord extends PanacheEntity {
    @JsonIgnore
    @ManyToOne(optional = false)
    private AppEntity entity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data;

    public AppEntity getEntity() {
        return entity;
    }

    public void setEntity(AppEntity entity) {
        this.entity = entity;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}