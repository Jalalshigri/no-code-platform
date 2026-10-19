<script setup lang="ts">
import { onMounted, ref } from "vue";
import EntityDesigner from "./components/EntityDesigner.vue";
import './style.css'

import {
  getApplications,
  createApplication as createApplicationApi,
  deleteApplication as deleteApplicationApi,
  updateApplication as updateApplicationApi,
  type Application,
} from "./services/applicationApi";
const currentView = ref<"applications" | "data-models">("applications");
const applications = ref<Application[]>([]);
const loading = ref(false);
const errorMessage = ref("");

const showCreateForm = ref(false);
const newApplicationName = ref("");
const newApplicationDescription = ref("");
const savingApplication = ref(false);
const createError = ref("");
const editingApplicationId = ref<number | null>(null);
const editApplicationName = ref("");
const editApplicationDescription = ref("");

async function loadApplications() {
  loading.value = true;
  errorMessage.value = "";

  try {
    applications.value = await getApplications();
  } catch (error) {
    errorMessage.value =
      error instanceof Error ? error.message : "Unable to load applications";
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadApplications();
});

async function createApplication() {
  const name = newApplicationName.value.trim();
  const description = newApplicationDescription.value.trim();

  if (!name) {
    createError.value = "Application name is required";
    return;
  }

  if (name.length < 3 || name.length > 100) {
    createError.value = "Application name must be between 3 and 100 characters";
    return;
  }

  if (description.length > 500) {
    createError.value = "Description must not exceed 500 characters";
    return;
  }

  savingApplication.value = true;
  createError.value = "";

  try {
    const savedApplication = await createApplicationApi({
      name,
      description,
    });
    console.log("Saved application from backend:", savedApplication);

    console.log(
      "Existing application IDs:",
      applications.value.map((application) => application.id),
    );

    applications.value.push(savedApplication);

    newApplicationName.value = "";
    newApplicationDescription.value = "";
    showCreateForm.value = false;
  } catch (error) {
    createError.value =
      error instanceof Error ? error.message : "Unable to create application";
  } finally {
    savingApplication.value = false;
  }
}

async function deleteApplication(id: number) {
  const confirmed = window.confirm(
    "Are you sure you want to delete this application?",
  );

  if (!confirmed) {
    return;
  }

  try {
    await deleteApplicationApi(id);

    // Reload the list from the backend after deletion.
    await loadApplications();
  } catch (error) {
    window.alert(
      error instanceof Error ? error.message : "Unable to delete application",
    );
  }
  function startEditing(application: Application) {
    editingApplicationId.value = application.id;
    editApplicationName.value = application.name;
    editApplicationDescription.value = application.description ?? "";
  }
}
async function saveApplicationChanges() {
  const id = editingApplicationId.value;

  if (id === null) {
    return;
  }

  const name = editApplicationName.value.trim();
  const description = editApplicationDescription.value.trim();

  if (name.length < 3 || name.length > 100) {
    window.alert("Application name must be between 3 and 100 characters");
    return;
  }

  if (description.length > 500) {
    window.alert("Description must not exceed 500 characters");
    return;
  }

  try {
    await updateApplicationApi(id, {
      name,
      description,
    });

    await loadApplications();

    editingApplicationId.value = null;
    editApplicationName.value = "";
    editApplicationDescription.value = "";
  } catch (error) {
    window.alert(
      error instanceof Error ? error.message : "Unable to update application",
    );
  }
}
</script>

<template>
  <div class="dashboard">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-icon">N</div>
        <div>
          <h2>NoCode</h2>
          <span>Application Studio</span>
        </div>
      </div>

      <nav class="navigation">
        <p class="nav-heading">WORKSPACE</p>

        <a
          href="#"
          class="nav-link"
          :class="{ active: currentView === 'applications' }"
          @click.prevent="currentView = 'applications'"
        >
          <span>▦</span>
          Applications
        </a>
        <a
          href="#"
          class="nav-link"
          :class="{ active: currentView === 'data-models' }"
          @click.prevent="currentView = 'data-models'"
        >
          <span>▤</span>
          Data Models
        </a>

        <a href="#" class="nav-link">
          <span>▣</span>
          Pages
        </a>

        <a href="#" class="nav-link">
          <span>⚡</span>
          Workflows
        </a>

        <p class="nav-heading tools-heading">PLATFORM</p>

        <a href="#" class="nav-link">
          <span>⚙</span>
          Settings
        </a>
      </nav>

      <div class="sidebar-footer">
        <div class="avatar">JH</div>
        <div class="user-details">
          <strong>Workspace Admin</strong>
          <span>Platform Owner</span>
        </div>
      </div>
    </aside>

    <main class="main-content">
      <header class="topbar">
        <div class="breadcrumb">
          <span>Workspace</span>
          <span class="breadcrumb-divider">/</span>
          <strong>Applications</strong>
        </div>

        <div class="topbar-right">
          <span class="environment">Development</span>
          <div class="avatar small-avatar">JH</div>
        </div>
      </header>

      <section class="page-content">
  <EntityDesigner v-if="currentView === 'data-models'" />

  <template v-else>
        <div class="welcome-row">
          <div>
            <p class="eyebrow">YOUR WORKSPACE</p>
            <h1>Applications</h1>
            <p class="page-description">
              Build, manage, and launch applications without writing code.
            </p>
          </div>

          <button
            class="primary-button"
            @click="showCreateForm = !showCreateForm"
          >
            <span>＋</span>
            Create application
          </button>
        </div>

        <div class="stats-grid">
          <article class="stat-card">
            <div class="stat-top">
              <span>Total applications</span>
              <span class="stat-icon purple">▦</span>
            </div>
            <strong>{{ applications.length }}</strong>
            <p>Applications in this workspace</p>
          </article>

          <article class="stat-card">
            <div class="stat-top">
              <span>Published</span>
              <span class="stat-icon green">✓</span>
            </div>
            <strong>
              {{
                applications.filter((app) => app.status === "Published").length
              }}
            </strong>
            <p>Live applications</p>
          </article>

          <article class="stat-card">
            <div class="stat-top">
              <span>Drafts</span>
              <span class="stat-icon orange">✎</span>
            </div>
            <strong>
              {{ applications.filter((app) => app.status === "Draft").length }}
            </strong>
            <p>Applications in progress</p>
          </article>
        </div>

        <section v-if="showCreateForm" class="create-panel">
          <div class="section-heading">
            <div>
              <h2>Create an application</h2>
              <p>Start with a name and description.</p>
            </div>

            <button
              class="close-button"
              aria-label="Close form"
              @click="showCreateForm = false"
            >
              ×
            </button>
          </div>

          <form @submit.prevent="createApplication">
            <label for="application-name">Application name</label>
            <input
              id="application-name"
              v-model="newApplicationName"
              type="text"
              placeholder="e.g. Employee Management"
              required
              maxlength="100"
            />

            <label for="application-description">Description</label>

            <textarea
              id="application-description"
              v-model="newApplicationDescription"
              placeholder="What will this application do?"
              rows="3"
              maxlength="500"
            ></textarea>
            <p v-if="createError" class="error-message">
              {{ createError }}
            </p>
            <div class="form-actions">
              <button
                type="button"
                class="secondary-button"
                @click="showCreateForm = false"
              >
                Cancel
              </button>

              <button
                type="submit"
                class="primary-button"
                :disabled="savingApplication"
              >
                {{ savingApplication ? "Creating..." : "Create application" }}
              </button>
            </div>
          </form>
        </section>

        <section class="applications-section">
          <div class="section-heading">
            <div>
              <h2>Your applications</h2>
              <p>Manage the applications in your workspace.</p>
            </div>

            <span class="count-label"> {{ applications.length }} total </span>
          </div>

          <div v-if="applications.length > 0" class="application-grid">
            <article
              v-for="application in applications"
              :key="application.id"
              class="application-card"
            >
              <div class="application-card-top">
                <div class="application-icon">▦</div>
                <span class="status-badge draft"> Draft </span>
              </div>

              <h3>{{ application.name }}</h3>

              <p class="application-description">
                {{ application.description || "No description provided." }}
              </p>

              <div class="application-card-footer">
                <span class="application-id">App ID: {{ application.id }}</span>
                <button
                  class="edit-button"
                  @click="editingApplicationId = application.id"
                >
                  Edit
                </button>

                <button
                  class="delete-button"
                  :aria-label="`Delete ${application.name}`"
                  @click="deleteApplication(application.id)"
                >
                  Delete
                </button>
              </div>
              <form
                v-if="editingApplicationId === application.id"
                class="edit-form"
                @submit.prevent="saveApplicationChanges"
              >
                <h4>Edit Application</h4>

                <div class="form-group">
                  <label :for="`edit-name-${application.id}`">
                    Application Name
                  </label>

                  <input
                    :id="`edit-name-${application.id}`"
                    v-model="editApplicationName"
                    type="text"
                    placeholder="Enter application name"
                    required
                    minlength="3"
                    maxlength="100"
                  />
                </div>

                <div class="form-group">
                  <label :for="`edit-description-${application.id}`">
                    Description
                  </label>

                  <textarea
                    :id="`edit-description-${application.id}`"
                    v-model="editApplicationDescription"
                    placeholder="Describe your application"
                    maxlength="500"
                  ></textarea>
                </div>

                <div class="form-actions">
                  <button
                    type="button"
                    class="cancel-button"
                    @click="editingApplicationId = null"
                  >
                    Cancel
                  </button>

                  <button type="submit" class="save-button">
                    Save Changes
                  </button>
                </div>
              </form>
            </article>
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon">▦</div>
            <h3>No applications yet</h3>
            <p>Create your first application to get started.</p>
            <button class="primary-button" @click="showCreateForm = true">
              Create application
            </button>
          </div>
        </section>

        <footer class="page-footer">
          NoCode Application Studio
          <span>·</span>
          Development workspace
        </footer>
      </template>
</section>
</main>
  </div>
</template>
