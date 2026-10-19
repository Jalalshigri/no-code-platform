package com.nocode.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_entities")

public class AppEntity extends PanacheEntity {
    private String name;

    @ManyToOne
    private Application application;
    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppField> fields = new ArrayList<>();

    public List<AppField> getFields() {
        return fields;
    }

    public void setFields(List<AppField> fields) {
        this.fields = fields;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(
    mappedBy = "entity",
    cascade = CascadeType.ALL,
    orphanRemoval = true
)
private List<AppRecord> records = new ArrayList<>();
public List<AppRecord> getRecords() {
    return records;
}

public void setRecords(List<AppRecord> records) {
    this.records = records;
}
}
