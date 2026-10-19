package com.nocode.resource;

import java.util.List;

import com.nocode.domain.AppEntity;
import com.nocode.domain.AppField;
import com.nocode.domain.AppPage;
import com.nocode.domain.AppPageComponent;
import com.nocode.domain.AppRecord;
import com.nocode.domain.Application;
import com.nocode.dto.AppEntityRequest;
import com.nocode.dto.AppFieldRequest;
import com.nocode.dto.AppFieldResponse;
import com.nocode.dto.AppPageComponentRequest;
import com.nocode.dto.AppPageRequest;
import com.nocode.dto.AppRecordRequest;
import com.nocode.dto.AppRecordResponse;
import com.nocode.dto.ApplicationRequest;
import com.nocode.service.AppEntityService;
import com.nocode.service.AppFieldService;
import com.nocode.service.AppPageComponentService;
import com.nocode.service.AppPageService;
import com.nocode.service.AppRecordService;
import com.nocode.service.ApplicationService;
import com.nocode.service.FormValidationService;
import com.nocode.service.PageRuntimeService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/applications")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource {
        @Inject
        ApplicationService applicationService;
        @Inject
        AppEntityService appEntityService;
        @Inject
        AppFieldService appFieldService;
        @Inject
        AppRecordService appRecordService;
        @Inject
        AppPageService appPageService;
        @Inject
        AppPageComponentService appPageComponentService;
        @Inject
        PageRuntimeService pageRuntimeService;
        @Inject
        FormValidationService formValidationService;

        @POST
        public Response create(@Valid ApplicationRequest request) {

                Application application = applicationService.create(request);

                return Response
                                .status(Response.Status.CREATED)
                                .entity(application)
                                .build();
        }

        @GET
        public List<Application> getAll() {

                return applicationService.findAll();

        }

        @GET
        @Path("/{id}")
        public Response getById(@PathParam("id") Long id) {

                // Application application = Application.findById(id);
                Application application = applicationService.findById(id);
                if (application == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .ok(application)
                                .build();
        }

        @PUT
        @Path("/{id}")
        // @Transactional
        public Response update(
                        @PathParam("id") Long id,
                        @Valid ApplicationRequest request) {

                Application application = applicationService.update(id, request);

                if (application == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                // application.setName(request.getName());
                // application.setDescription(request.getDescription());

                return Response
                                .ok(application)
                                .build();
        }

        @DELETE
        @Path("/{id}")
        @Transactional
        public Response delete(@PathParam("id") Long id) {

                boolean deleted = applicationService.delete(id);
                if (!deleted) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .noContent()
                                .build();
        }

        @POST
        @Path("/{applicationId}/entities")
        public Response createEntity(
                        @PathParam("applicationId") Long applicationId,
                        @Valid AppEntityRequest request) {

                AppEntity entity = appEntityService.create(applicationId, request);

                if (entity == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .status(Response.Status.CREATED)
                                .entity(entity)
                                .build();
        }

        @GET
        @Path("/{applicationId}/entities")
        public Response getEntities(
                        @PathParam("applicationId") Long applicationId) {

                List<AppEntity> entities = appEntityService.findByApplicationId(applicationId);

                return Response
                                .ok(entities)
                                .build();
        }

        @GET
        @Path("/{applicationId}/entities/{entityId}")
        public Response getEntity(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId) {

                AppEntity entity = appEntityService.findById(
                                applicationId,
                                entityId);

                if (entity == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .ok(entity)
                                .build();
        }

        @PUT
        @Path("/{applicationId}/entities/{entityId}")
        public Response updateEntity(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @Valid AppEntityRequest request) {

                AppEntity entity = appEntityService.update(
                                applicationId,
                                entityId,
                                request);

                if (entity == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .ok(entity)
                                .build();
        }

        @DELETE
        @Path("/{applicationId}/entities/{entityId}")
        public Response deleteEntity(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId) {

                boolean deleted = appEntityService.delete(
                                applicationId,
                                entityId);

                if (!deleted) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .noContent()
                                .build();
        }

        @POST
        @Path("/{applicationId}/entities/{entityId}/fields")
        public Response createField(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @Valid AppFieldRequest request) {

                AppField field = appFieldService.create(applicationId, entityId, request);

                if (field == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                AppFieldResponse response = new AppFieldResponse(
                                field.id,
                                field.getName(),
                                field.getType(), field.isRequired(),
                                field.getMinLength(),
                                field.getMaxLength(), field.getMinValue(), field.getMaxValue());

                return Response
                                .status(Response.Status.CREATED)
                                .entity(response)
                                .build();
        }

        @GET
        @Path("/{applicationId}/entities/{entityId}/fields")
        public Response getFields(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId) {

                List<AppField> fields = appFieldService.findByEntityId(applicationId, entityId);

                List<AppFieldResponse> response = fields.stream()
                                .map(field -> new AppFieldResponse(
                                                field.id,
                                                field.getName(),
                                                field.getType(),
                                                field.isRequired(),
                                                field.getMinLength(),
                                                field.getMaxLength(),
                                                field.getMinValue(),
                                                field.getMaxValue()))
                                .toList();

                return Response
                                .ok(response)
                                .build();
        }

        @GET
        @Path("/{applicationId}/entities/{entityId}/fields/{fieldId}")
        public Response getField(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @PathParam("fieldId") Long fieldId) {

                AppField field = appFieldService.findById(
                                applicationId,
                                entityId,
                                fieldId);

                if (field == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                AppFieldResponse response = new AppFieldResponse(
                                field.id,
                                field.getName(),
                                field.getType(),
                                field.isRequired(),
                                field.getMinLength(),
                                field.getMaxLength(),
                                field.getMinValue(),
                                field.getMaxValue());

                return Response
                                .ok(response)
                                .build();
        }

        @DELETE
        @Path("/{applicationId}/entities/{entityId}/fields/{fieldId}")
        public Response deleteField(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @PathParam("fieldId") Long fieldId) {

                boolean deleted = appFieldService.delete(applicationId,
                                entityId,
                                fieldId);

                if (!deleted) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .noContent()
                                .build();
        }

        @PUT
        @Path("/{applicationId}/entities/{entityId}/fields/{fieldId}")
        public Response updateField(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @PathParam("fieldId") Long fieldId,
                        @Valid AppFieldRequest request) {

                AppField field = appFieldService.update(applicationId, entityId, fieldId, request);

                if (field == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                AppFieldResponse response = new AppFieldResponse(
                                field.id,
                                field.getName(),
                                field.getType(),
                                field.isRequired(),
                                field.getMinLength(),
                                field.getMaxLength(), field.getMinValue(), field.getMaxValue());

                return Response
                                .ok(response)
                                .build();
        }

        @GET
        @Path("/{applicationId}/entities/{entityId}/records")
        public Response getRecords(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId) {

                List<AppRecord> records = appRecordService.findByEntityId(
                                applicationId,
                                entityId);

                List<AppRecordResponse> response = records.stream()
                                .map(record -> new AppRecordResponse(
                                                record.id,
                                                record.getData()))
                                .toList();

                return Response
                                .ok(response)
                                .build();
        }

        @GET
        @Path("/{applicationId}/entities/{entityId}/records/{recordId}")
        public Response getRecord(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @PathParam("recordId") Long recordId) {

                AppRecord record = appRecordService.findById(
                                applicationId,
                                entityId,
                                recordId);

                if (record == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .ok(new AppRecordResponse(
                                                record.id,
                                                record.getData()))
                                .build();
        }

        @POST
        @Path("/{applicationId}/entities/{entityId}/records")
        public Response createRecord(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @Valid AppRecordRequest request) {

                AppRecord record = appRecordService.create(
                                applicationId,
                                entityId,
                                request);

                if (record == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                AppRecordResponse response = new AppRecordResponse(
                                record.id,
                                record.getData());

                return Response
                                .status(Response.Status.CREATED)
                                .entity(response)
                                .build();
        }

        @PUT
        @Path("/{applicationId}/entities/{entityId}/records/{recordId}")
        public Response updateRecord(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @PathParam("recordId") Long recordId,
                        @Valid AppRecordRequest request) {

                AppRecord record = appRecordService.update(
                                applicationId,
                                entityId,
                                recordId,
                                request);

                if (record == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .ok(new AppRecordResponse(
                                                record.id,
                                                record.getData()))
                                .build();
        }

        @DELETE
        @Path("/{applicationId}/entities/{entityId}/records/{recordId}")
        public Response deleteRecord(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("entityId") Long entityId,
                        @PathParam("recordId") Long recordId) {

                boolean deleted = appRecordService.delete(
                                applicationId,
                                entityId,
                                recordId);

                if (!deleted) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                return Response
                                .noContent()
                                .build();
        }

        ///////////// app page DTO
        @POST
        @Path("/{applicationId}/pages")
        public Response createPage(
                        @PathParam("applicationId") Long applicationId,
                        @Valid AppPageRequest request) {

                AppPage page = appPageService.create(applicationId, request);

                if (page == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.status(Response.Status.CREATED)
                                .entity(page)
                                .build();
        }

        @GET
        @Path("/{applicationId}/pages")
        public Response getPages(
                        @PathParam("applicationId") Long applicationId) {

                Application application = applicationService.findById(applicationId);

                if (application == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.ok(
                                appPageService.findByApplicationId(applicationId)).build();
        }

        @GET
        @Path("/{applicationId}/pages/{pageId}")
        public Response getPage(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId) {

                AppPage page = appPageService.findById(applicationId, pageId);

                if (page == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.ok(page).build();
        }

        @PUT
        @Path("/{applicationId}/pages/{pageId}")
        public Response updatePage(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @Valid AppPageRequest request) {

                AppPage page = appPageService.update(
                                applicationId,
                                pageId,
                                request);

                if (page == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.ok(page).build();
        }

        @DELETE
        @Path("/{applicationId}/pages/{pageId}")
        public Response deletePage(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId) {

                boolean deleted = appPageService.delete(applicationId, pageId);

                if (!deleted) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.noContent().build();
        }

        /* page component */
        @POST
        @Path("/{applicationId}/pages/{pageId}/components")
        public Response createPageComponent(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @Valid AppPageComponentRequest request) {

                AppPageComponent component = appPageComponentService.create(
                                applicationId,
                                pageId,
                                request);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.status(Response.Status.CREATED)
                                .entity(component)
                                .build();
        }

        @GET
        @Path("/{applicationId}/pages/{pageId}/components")
        public Response getPageComponents(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId) {

                AppPage page = AppPage.findById(pageId);

                if (page == null
                                || !page.getApplication().id.equals(applicationId)) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.ok(
                                appPageComponentService.findByPageId(
                                                applicationId,
                                                pageId))
                                .build();
        }

        @GET
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}")
        public Response getPageComponent(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId) {

                AppPageComponent component = appPageComponentService.findById(
                                applicationId,
                                pageId,
                                componentId);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.ok(component).build();
        }

        @PUT
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}")
        public Response updatePageComponent(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId,
                        @Valid AppPageComponentRequest request) {

                AppPageComponent component = appPageComponentService.update(
                                applicationId,
                                pageId,
                                componentId,
                                request);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.ok(component).build();
        }

        @DELETE
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}")
        public Response deletePageComponent(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId) {

                boolean deleted = appPageComponentService.delete(
                                applicationId,
                                pageId,
                                componentId);

                if (!deleted) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }

                return Response.noContent().build();
        }

        // page runtime
        @GET
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}/runtime")
        public Response getRuntimeComponent(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId,
                        @QueryParam("page") @DefaultValue("0") int page,
                        @QueryParam("search") @DefaultValue("") String search,
                        @QueryParam("sortField") @DefaultValue("id") String sortField,
                        @QueryParam("sortDirection") @DefaultValue("asc") String sortDirection) {
                sortDirection = sortDirection == null
                                ? "asc"
                                : sortDirection.trim().toLowerCase();

                if (!sortDirection.equals("asc")
                                && !sortDirection.equals("desc")) {

                        sortDirection = "asc";
                }

                java.util.List<String> configuredColumns = pageRuntimeService.getConfiguredColumns(
                                applicationId,
                                pageId,
                                componentId);

                boolean validSortField = false;

                for (String column : configuredColumns) {

                        if (column.equalsIgnoreCase(sortField)) {
                                sortField = column;
                                validSortField = true;
                                break;
                        }
                }

                if (!validSortField) {
                        sortField = "id";
                }

                AppPageComponent component = pageRuntimeService.findComponent(
                                applicationId,
                                pageId,
                                componentId);

                if (component == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .build();
                }

                Long entityId = pageRuntimeService.getDataSourceEntityId(
                                applicationId,
                                pageId,
                                componentId);

                if (entityId == null) {
                        return Response
                                        .status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error", "Component has no valid data source"))
                                        .build();
                }

                AppEntity entity = pageRuntimeService.findDataSourceEntity(
                                applicationId,
                                pageId,
                                componentId);
                java.util.List<AppField> fields = pageRuntimeService.findConfiguredFields(
                                applicationId,
                                pageId,
                                componentId);
                java.util.List<String> columns = pageRuntimeService.getConfiguredColumns(
                                applicationId,
                                pageId,
                                componentId);
                java.util.List<AppField> configuredFields = pageRuntimeService.findConfiguredFields(
                                applicationId,
                                pageId,
                                componentId);
                java.util.List<AppRecord> records = pageRuntimeService.findDataSourceRecordsPaged(
                                applicationId,
                                pageId,
                                componentId, page, search,
                                sortField,
                                sortDirection);
                java.util.List<java.util.Map<String, Object>> tableRows = pageRuntimeService.buildTableRows(
                                applicationId,
                                pageId,
                                componentId, page, search,
                                sortField,
                                sortDirection);
                if (entity == null) {
                        return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error", "Data source entity not found"))
                                        .build();
                }
                long totalRecords = pageRuntimeService.getDataSourceRecordCount(
                                applicationId,
                                pageId,
                                componentId,
                                search);
                int totalPages = pageRuntimeService.getTotalPages(
                                applicationId,
                                pageId,
                                componentId,
                                search);

                return Response
                                .ok(java.util.Map.ofEntries(
                                                java.util.Map.entry("componentId", component.id),
                                                java.util.Map.entry("type", component.getType()),
                                                java.util.Map.entry("entityId", entity.id),
                                                java.util.Map.entry("entityName", entity.getName()),
                                                java.util.Map.entry("page", page),
                                                java.util.Map.entry("pageSize",
                                                                pageRuntimeService.getPageSize(
                                                                                applicationId,
                                                                                pageId,
                                                                                componentId)),
                                                java.util.Map.entry("totalRecords", totalRecords),
                                                java.util.Map.entry("totalPages", totalPages),
                                                java.util.Map.entry("fields", fields),
                                                java.util.Map.entry("columns", columns),
                                                java.util.Map.entry("configuredFields", configuredFields),
                                                java.util.Map.entry("records", records),
                                                java.util.Map.entry("tableRows", tableRows)))
                                .build();

                // return Response
                // .ok(java.util.Map.of(
                // "componentId", component.id,
                // "type", component.getType(),
                // "entityId", entity.id,
                // "entityName", entity.getName(),
                // "pageSize", pageRuntimeService.getPageSize(
                // applicationId,
                // pageId,
                // componentId),
                // "totalRecords", totalRecords,
                // "fields", fields,
                // "columns", columns,
                // "configuredFields", configuredFields,
                // "records", records,
                // "tableRows", tableRows))
                // .build();
        }

        @GET
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}/form-runtime")
        public Response getFormRuntime(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId) {

                AppPageComponent component = pageRuntimeService.findComponent(
                                applicationId,
                                pageId,
                                componentId);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component not found"))
                                        .build();
                }

                if (component.getType() != com.nocode.domain.PageComponentType.FORM) {

                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component is not a FORM"))
                                        .build();
                }

                java.util.Map<String, Object> metadata = pageRuntimeService.getFormMetadata(
                                applicationId,
                                pageId,
                                componentId);

                if (metadata.isEmpty()) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "FORM data source is invalid"))
                                        .build();
                }

                return Response.ok(metadata).build();
        }

        @POST
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}/form-validate")
        public Response validateForm(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId,
                        java.util.Map<String, Object> data) {

                AppPageComponent component = pageRuntimeService.findComponent(
                                applicationId,
                                pageId,
                                componentId);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component not found"))
                                        .build();
                }

                if (component.getType() != com.nocode.domain.PageComponentType.FORM) {

                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component is not a FORM"))
                                        .build();
                }

                AppEntity entity = pageRuntimeService.findDataSourceEntity(
                                applicationId,
                                pageId,
                                componentId);

                if (entity == null) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "FORM data source is invalid"))
                                        .build();
                }

                java.util.List<AppField> fields = pageRuntimeService.findConfiguredFields(
                                applicationId,
                                pageId,
                                componentId);
                java.util.Map<String, String> errors = formValidationService.validateFields(
                                fields,
                                data);

                if (!errors.isEmpty()) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "valid", false,
                                                        "errors", errors))
                                        .build();
                }

                return Response.ok(
                                java.util.Map.of(
                                                "valid", true))
                                .build();
        }

        @Transactional
        @POST
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}/form-submit")
        public Response submitForm(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId,
                        java.util.Map<String, Object> data) {

                AppPageComponent component = pageRuntimeService.findComponent(
                                applicationId,
                                pageId,
                                componentId);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component not found"))
                                        .build();
                }

                if (component.getType() != com.nocode.domain.PageComponentType.FORM) {

                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component is not a FORM"))
                                        .build();
                }
                java.util.List<AppField> fields = pageRuntimeService.findConfiguredFields(
                                applicationId,
                                pageId,
                                componentId);
                AppEntity entity = pageRuntimeService.findDataSourceEntity(
                                applicationId,
                                pageId,
                                componentId);

                if (entity == null) {
                        return Response.status(
                                        Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "FORM data source is invalid"))
                                        .build();
                }
                java.util.Map<String, String> errors = formValidationService.validateFields(
                                fields,
                                data);

                if (!errors.isEmpty()) {

                        return Response.status(
                                        Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "valid", false,
                                                        "errors", errors))
                                        .build();
                }
                AppRecord record = appRecordService.buildRecord(
                                entity,
                                fields,
                                data);
                appRecordService.saveRecord(record);
                AppRecordResponse response = new AppRecordResponse(
                                record.id,
                                record.getData());

                return Response.status(Response.Status.CREATED)
                                .entity(response)
                                .build();
        }

        @Transactional
        @PUT
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}/form-submit/{recordId}")
        public Response updateForm(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId,
                        @PathParam("recordId") Long recordId,
                        java.util.Map<String, Object> data) {

                AppPageComponent component = pageRuntimeService.findComponent(
                                applicationId,
                                pageId,
                                componentId);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component not found"))
                                        .build();
                }

                if (component.getType() != com.nocode.domain.PageComponentType.FORM) {

                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component is not a FORM"))
                                        .build();
                }

                AppEntity entity = pageRuntimeService.findDataSourceEntity(
                                applicationId,
                                pageId,
                                componentId);

                if (entity == null) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "FORM data source is invalid"))
                                        .build();
                }

                AppRecord record = appRecordService.findRecord(
                                entity,
                                recordId);

                if (record == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Record not found"))
                                        .build();
                }
                java.util.List<AppField> fields = pageRuntimeService.findConfiguredFields(
                                applicationId,
                                pageId,
                                componentId);
                java.util.Map<String, Object> mergedData = new java.util.HashMap<>();

                if (record.getData() != null) {
                        mergedData.putAll(record.getData());
                }

                mergedData.putAll(data);

                java.util.Map<String, String> errors = formValidationService.validateFields(
                                fields,
                                mergedData);
                if (!errors.isEmpty()) {

                        return Response.status(
                                        Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "valid", false,
                                                        "errors", errors))
                                        .build();
                }
                appRecordService.updateRecord(
                                record,
                                fields,
                                data);
                AppRecordResponse response = new AppRecordResponse(
                                record.id,
                                record.getData());

                return Response.ok(response)
                                .build();
        }

        @GET
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}/form-runtime/{recordId}")
        public Response getFormRecord(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId,
                        @PathParam("recordId") Long recordId) {

                AppPageComponent component = pageRuntimeService.findComponent(
                                applicationId,
                                pageId,
                                componentId);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component not found"))
                                        .build();
                }

                if (component.getType() != com.nocode.domain.PageComponentType.FORM) {

                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Component is not a FORM"))
                                        .build();
                }

                AppEntity entity = pageRuntimeService.findDataSourceEntity(
                                applicationId,
                                pageId,
                                componentId);

                if (entity == null) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "FORM data source is invalid"))
                                        .build();
                }

                AppRecord record = appRecordService.findRecord(
                                entity,
                                recordId);

                if (record == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error",
                                                        "Record not found"))
                                        .build();
                }

                AppRecordResponse response = new AppRecordResponse(
                                record.id,
                                record.getData());

                return Response.ok(response)
                                .build();
        }

        @Transactional
        @jakarta.ws.rs.DELETE
        @Path("/{applicationId}/pages/{pageId}/components/{componentId}/form-submit/{recordId}")
        public Response deleteFormRecord(
                        @PathParam("applicationId") Long applicationId,
                        @PathParam("pageId") Long pageId,
                        @PathParam("componentId") Long componentId,
                        @PathParam("recordId") Long recordId) {

                AppPageComponent component = pageRuntimeService.findComponent(
                                applicationId,
                                pageId,
                                componentId);

                if (component == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error", "Component not found"))
                                        .build();
                }

                if (component.getType() != com.nocode.domain.PageComponentType.FORM) {

                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error", "Component is not a FORM"))
                                        .build();
                }

                AppEntity entity = pageRuntimeService.findDataSourceEntity(
                                applicationId,
                                pageId,
                                componentId);

                if (entity == null) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(java.util.Map.of(
                                                        "error", "FORM data source is invalid"))
                                        .build();
                }

                AppRecord record = appRecordService.findRecord(
                                entity,
                                recordId);

                if (record == null) {
                        return Response.status(Response.Status.NOT_FOUND)
                                        .entity(java.util.Map.of(
                                                        "error", "Record not found"))
                                        .build();
                }

                appRecordService.deleteRecord(record);

                return Response.noContent().build();
        }
}