const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export interface Application {
  id: number;
  name: string;
  description: string;
}

export interface ApplicationRequest {
  name: string;
  description: string;
}

export async function getApplications(): Promise<Application[]> {
  const response = await fetch(`${API_BASE_URL}/applications`);

  if (!response.ok) {
    throw new Error(`Failed to load applications: ${response.status}`);
  }

  return response.json() as Promise<Application[]>;
}

export async function createApplication(
  request: ApplicationRequest,
): Promise<Application> {
  const response = await fetch(`${API_BASE_URL}/applications`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      errorText || `Failed to create application: ${response.status}`,
    );
  }

  return response.json() as Promise<Application>;
}
export async function deleteApplication(id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/applications/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error(`Failed to delete application: ${response.status}`);
  }
}

export async function updateApplication(
  id: number,
  requst: ApplicationRequest,
): Promise<Application> {
  const response = await fetch(`${API_BASE_URL}/applications/${id}`, {
    method: "PUT",
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requst),
  });
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(
      errorText || `Failed to update application:${response.status}`,
    );
  }

  return response.json() as Promise<Application>;
}
