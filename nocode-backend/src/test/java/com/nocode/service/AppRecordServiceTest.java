package com.nocode.service;

import com.nocode.domain.AppEntity;
import com.nocode.domain.AppRecord;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppRecordServiceTest {

    @Test
    void shouldBuildRecordFromFormData() {

        AppEntity entity = new AppEntity();

        entity.setName("Customer");

        Map<String, Object> data = Map.of(
                "name", "John",
                "email", "john@example.com",
                "age", 35);

        AppRecordService service = new AppRecordService();

        AppRecord record = service.buildRecord(entity, data);

        assertEquals(
                entity,
                record.getEntity());

        assertEquals(
                "John",
                record.getData().get("name"));

        assertEquals(
                "john@example.com",
                record.getData().get("email"));

        assertEquals(
                35,
                record.getData().get("age"));
    }

  
}