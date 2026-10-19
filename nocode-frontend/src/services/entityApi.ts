const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api'

export interface AppEntity {
  id: number
  name: string
  fields: EntityField[]
}

export interface EntityField {
  id: number
  name: string
  type: string
  required: boolean
  minLength: number | null
  maxLength: number | null
  minValue: number | null
  maxValue: number | null
}

export interface CreateEntityRequest {
  name: string
}

export interface CreateFieldRequest {
  name: string
  type: string
  required: boolean
  minLength?: number | null
  maxLength?: number | null
  minValue?: number | null
  maxValue?: number | null
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorText = await response.text()

    throw new Error(
      errorText || `Request failed with status ${response.status}`,
    )
  }

  return response.json() as Promise<T>
}

export async function getEntities(
  applicationId: number,
): Promise<AppEntity[]> {
  const response = await fetch(
    `${API_BASE_URL}/applications/${applicationId}/entities`,
  )

  return handleResponse<AppEntity[]>(response)
}

export async function createEntity(
  applicationId: number,
  request: CreateEntityRequest,
): Promise<AppEntity> {
  const response = await fetch(
    `${API_BASE_URL}/applications/${applicationId}/entities`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    },
  )

  return handleResponse<AppEntity>(response)
}

export async function getFields(
  applicationId: number,
  entityId: number,
): Promise<EntityField[]> {
  const response = await fetch(
    `${API_BASE_URL}/applications/${applicationId}/entities/${entityId}/fields`,
  )

  return handleResponse<EntityField[]>(response)
}

export async function createField(
  applicationId: number,
  entityId: number,
  request: CreateFieldRequest,
): Promise<EntityField> {
  const response = await fetch(
    `${API_BASE_URL}/applications/${applicationId}/entities/${entityId}/fields`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    },
  )

  return handleResponse<EntityField>(response)
}
export async function deleteField(
  applicationId: number,
  entityId: number,
  fieldId: number,
): Promise<void> {
  const response = await fetch(
    `${API_BASE_URL}/applications/${applicationId}/entities/${entityId}/fields/${fieldId}`,
    {
      method: "DELETE",
    },
  );

  if (!response.ok) {
    throw new Error(`Failed to delete field. Status: ${response.status}`);
  }
}