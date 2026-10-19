package com.nocode.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class AppRecordRequest {

    @NotNull(message = "Record data is required")
    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}