<script setup lang="ts">
import { onMounted, ref } from "vue";
import { getApplications, type Application } from "../services/applicationApi";

import {
  getEntities,
  createEntity,
  getFields,
  createField,
  deleteField,
  type AppEntity,
  type EntityField,
  type CreateFieldRequest,
} from "../services/entityApi";

const applications = ref<Application[]>([]);
const selectedApplicationId = ref<number | null>(null);
const entities = ref<AppEntity[]>([]);
const loading = ref(false);
const saving = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const newEntityName = ref("");
const selectedEntityId = ref<number | null>(null);
const fields = ref<EntityField[]>([]);

const newFieldName = ref("");
const newFieldType = ref("STRING");
const newFieldRequired = ref(false);

const newFieldMinLength = ref<number | null>(null);
const newFieldMaxLength = ref<number | null>(null);
const newFieldMinValue = ref<number | null>(null);
const newFieldMaxValue = ref<number | null>(null);
onMounted(async () => {
  try {
    applications.value = await getApplications();
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : "unable to load appliations";
  }
});
async function loadEntities() {
  if (selectedApplicationId.value == null) {
    entities.value = [];
    return;
  }

  loading.value = true;
  errorMessage.value = "";
  successMessage.value = "";
  try {
    const result = await getEntities(selectedApplicationId.value);

    entities.value = Array.isArray(result) ? result : [];
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : "unable to load entity";
  } finally {
    loading.value = false;
  }
}
async function loadFields(entityId: number) {
  if (selectedApplicationId.value === null) {
    fields.value = [];
    return;
  }

  selectedEntityId.value = entityId;
  fields.value = [];
  errorMessage.value = "";
  successMessage.value = "";

  try {
    const result = await getFields(selectedApplicationId.value, entityId);

    fields.value = Array.isArray(result) ? result : [];
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : "Unable to load fields";
  }
}
async function addField() {
  if (selectedApplicationId.value === null) {
    errorMessage.value = "Please select an application first.";
    return;
  }

  if (selectedEntityId.value === null) {
    errorMessage.value = "Please select an entity first.";
    return;
  }

  const name = newFieldName.value.trim();

  if (name.length < 2 || name.length > 100) {
    errorMessage.value = "Field name must be between 2 and 100 characters.";
    return;
  }

  const request: CreateFieldRequest = {
    name,
    type: newFieldType.value as CreateFieldRequest["type"],
    required: newFieldRequired.value,
    minLength: newFieldMinLength.value,
    maxLength: newFieldMaxLength.value,
    minValue: newFieldMinValue.value,
    maxValue: newFieldMaxValue.value,
  };

  saving.value = true;
  errorMessage.value = "";
  successMessage.value = "";

  try {
    await createField(
      selectedApplicationId.value,
      selectedEntityId.value,
      request,
    );

    newFieldName.value = "";
    newFieldRequired.value = false;
    newFieldMinLength.value = null;
    newFieldMaxLength.value = null;
    newFieldMinValue.value = null;
    newFieldMaxValue.value = null;

    await loadFields(selectedEntityId.value);

    successMessage.value = "Field created successfully.";
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : "Unable to create field.";
  } finally {
    saving.value = false;
  }
}
async function removeField(fieldId: number) {
  if (selectedApplicationId.value === null || selectedEntityId.value === null) {
    return;
  }

  const confirmed = window.confirm(
    "Are you sure you want to delete this field?",
  );

  if (!confirmed) {
    return;
  }

  saving.value = true;
  errorMessage.value = "";
  successMessage.value = "";

  try {
    await deleteField(
      selectedApplicationId.value,
      selectedEntityId.value,
      fieldId,
    );

    await loadFields(selectedEntityId.value);

    successMessage.value = "Field deleted successfully.";
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : "Failed to delete field.";
  } finally {
    saving.value = false;
  }
}
async function addEntity() {
  const name = newEntityName.value.trim();
  if (selectedApplicationId.value === null) {
    errorMessage.value = "plsase select an appliation frist";
    return;
  }
  if (name.length < 2 || name.length > 100) {
    errorMessage.value = "Entity name must ne between 2 and 100 characters";
    return;
  }
  saving.value = true;
  errorMessage.value = "";
  successMessage.value = "";
  try {
    await createEntity(selectedApplicationId.value, { name });
    newEntityName.value = "";
    successMessage.value = "Entity created successfully";
    await loadEntities();
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : "Unable to create entity";
  } finally {
    saving.value = false;
  }
}
</script>
<template>
  <section class="entity-designer">
    <div class="designer-header">
      <div>
        <p class="eyebrow">DATA MODELING</p>
        <h1>Entity Designer</h1>
        <p class="description">
          Define the data structure for your application.
        </p>
      </div>
    </div>

    <div class="designer-panel">
      <label for="application-select">Select application</label>

      <select
        id="application-select"
        v-model="selectedApplicationId"
        @change="loadEntities"
      >
        <option :value="null" disabled>Select an application</option>

        <option
          v-for="application in applications"
          :key="application.id"
          :value="application.id"
        >
          {{ application.name }} (ID: {{ application.id }})
        </option>
      </select>

      <div class="entity-create-form">
        <label for="entity-name">Entity name</label>

        <form @submit.prevent="addEntity">
          <input
            id="entity-name"
            v-model="newEntityName"
            type="text"
            placeholder="e.g. Customer, Product, Order"
            minlength="2"
            maxlength="100"
            required
          />

          <button
            type="submit"
            class="primary-button"
            :disabled="saving || selectedApplicationId === null"
          >
            {{ saving ? "Creating..." : "+ Create entity" }}
          </button>
        </form>
      </div>

      <p v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </p>

      <p v-if="successMessage" class="success-message">
        {{ successMessage }}
      </p>
    </div>

    <div class="entities-section">
      <div class="section-heading">
        <div>
          <h2>Entities</h2>
          <p>Entities define the data your application stores.</p>
        </div>

        <span class="count-label">{{ entities.length }} total</span>
      </div>

      <p v-if="loading">Loading entities...</p>

      <div v-else-if="entities.length > 0" class="entity-grid">
        <article
          v-for="entity in entities"
          :key="entity.id"
          class="entity-card"
        >
          <div class="entity-card-icon">▤</div>

          <h3>{{ entity.name }}</h3>

          <p>Entity ID: {{ entity.id }}</p>

          <div class="entity-card-footer">
            <span>{{ entity.fields?.length ?? 0 }} fields</span>
          </div>

          <div
            v-if="entity.fields && entity.fields.length > 0"
            class="entity-fields-list"
          >
            <div
              v-for="field in entity.fields"
              :key="field.id"
              class="entity-field-item"
            >
              <div>
                <strong>{{ field.name }}</strong>
                <span class="field-type">{{ field.type }}</span>
              </div>

              <span v-if="field.required" class="required-badge">
                Required
              </span>
            </div>
          </div>

          <p v-else class="no-fields-message">No fields added yet.</p>
          <button
            type="button"
            class="primary-button"
            @click="loadFields(entity.id)"
          >
            Manage Fields
          </button>
        </article>
      </div>

      <div v-else-if="selectedApplicationId !== null" class="empty-state">
        <h3>No entities yet</h3>
        <p>Create your first entity using the form above.</p>
      </div>

      <div v-else class="empty-state">
        <h3>Select an application</h3>
        <p>Choose an application to view its entities.</p>
      </div>
    </div>
    <!-- Field Management -->
    <div v-if="selectedEntityId !== null" class="designer-panel field-panel">
      <h2>Manage Fields</h2>

      <p>Add a field to the selected entity.</p>

      <form @submit.prevent="addField">
        <div class="form-group">
          <label for="field-name">Field Name</label>

          <input
            id="field-name"
            v-model="newFieldName"
            type="text"
            placeholder="e.g. orderNumber"
            minlength="2"
            maxlength="100"
            required
          />
        </div>

        <div class="form-group">
          <label for="field-type">Field Type</label>

          <select id="field-type" v-model="newFieldType">
            <option value="STRING">String</option>
            <option value="TEXT">Text</option>
            <option value="INTEGER">Integer</option>
            <option value="DECIMAL">Decimal</option>
            <option value="BOOLEAN">Boolean</option>
            <option value="DATE">Date</option>
            <option value="DATETIME">Date and Time</option>
          </select>
        </div>

        <div class="form-group">
          <label>
            <input v-model="newFieldRequired" type="checkbox" />
            Required field
          </label>
        </div>

        <div v-if="newFieldType === 'STRING' || newFieldType === 'TEXT'">
          <div class="form-group">
            <label for="field-min-length">Minimum Length</label>

            <input
              id="field-min-length"
              v-model.number="newFieldMinLength"
              type="number"
              min="0"
              placeholder="Optional"
            />
          </div>

          <div class="form-group">
            <label for="field-max-length">Maximum Length</label>

            <input
              id="field-max-length"
              v-model.number="newFieldMaxLength"
              type="number"
              min="0"
              placeholder="Optional"
            />
          </div>
        </div>

        <div v-if="newFieldType === 'INTEGER' || newFieldType === 'DECIMAL'">
          <div class="form-group">
            <label for="field-min-value">Minimum Value</label>

            <input
              id="field-min-value"
              v-model.number="newFieldMinValue"
              type="number"
              placeholder="Optional"
            />
          </div>

          <div class="form-group">
            <label for="field-max-value">Maximum Value</label>

            <input
              id="field-max-value"
              v-model.number="newFieldMaxValue"
              type="number"
              placeholder="Optional"
            />
          </div>
        </div>

        <button type="submit" class="primary-button" :disabled="saving">
          {{ saving ? "Saving..." : "Add Field" }}
        </button>
      </form>

      <p v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </p>

      <p v-if="successMessage" class="success-message">
        {{ successMessage }}
      </p>
    </div>
  </section>
</template>
