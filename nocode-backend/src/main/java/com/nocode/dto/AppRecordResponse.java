package com.nocode.dto;

import java.util.Map;

public class AppRecordResponse {

    private Long id;
    private Map<String, Object> data;

    public AppRecordResponse(
            Long id,
            Map<String, Object> data) {

        this.id = id;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public Map<String, Object> getData() {
        return data;
    }
}