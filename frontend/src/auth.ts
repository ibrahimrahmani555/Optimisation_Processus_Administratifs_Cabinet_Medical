export type UserRole = 'ADMIN' | 'SECRETAIRE' | 'MEDECIN';

export interface AuthUser {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  cabinetId: string;
  cabinetName: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: 'Bearer';
  expiresIn: number;
  user: AuthUser;
}

const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8081/api').replace(/\/$/, '');
const TOKEN_KEY = 'cabinet-medical.access-token';

export class ApiError extends Error {
  constructor(message: string, public readonly status: number) {
    super(message);
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = sessionStorage.getItem(TOKEN_KEY);
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });

  if (!response.ok) {
    const body = await response.json().catch(() => null) as { message?: string } | null;
    throw new ApiError(body?.message || 'Une erreur est survenue', response.status);
  }
  return response.json() as Promise<T>;
}

export async function login(email: string, password: string): Promise<AuthUser> {
  const response = await request<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
  sessionStorage.setItem(TOKEN_KEY, response.accessToken);
  return response.user;
}

export async function getCurrentUser(): Promise<AuthUser | null> {
  if (!sessionStorage.getItem(TOKEN_KEY)) return null;
  try {
    return await request<AuthUser>('/auth/me');
  } catch (error) {
    if (error instanceof ApiError && error.status === 401) {
      logout();
      return null;
    }
    throw error;
  }
}

export function logout(): void {
  sessionStorage.removeItem(TOKEN_KEY);
}
