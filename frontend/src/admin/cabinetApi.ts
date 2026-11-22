import { apiRequest } from '../auth';
import type { Cabinet, CabinetPage, CabinetStatusFilter } from './cabinetTypes';

export interface CabinetListParams {
  page: number;
  size: number;
  search: string;
  status: CabinetStatusFilter;
  sort: string;
}

export function getCabinets(params: CabinetListParams): Promise<CabinetPage> {
  const query = new URLSearchParams({
    page: String(params.page),
    size: String(params.size),
    status: params.status,
    sort: params.sort,
  });
  if (params.search.trim()) query.set('search', params.search.trim());
  return apiRequest<CabinetPage>(`/admin/cabinets?${query}`);
}

export function getCabinet(id: string): Promise<Cabinet> {
  return apiRequest<Cabinet>(`/admin/cabinets/${id}`);
}

export function createCabinet(name: string): Promise<Cabinet> {
  return apiRequest<Cabinet>('/admin/cabinets', {
    method: 'POST',
    body: JSON.stringify({ name }),
  });
}

export function updateCabinet(id: string, name: string): Promise<Cabinet> {
  return apiRequest<Cabinet>(`/admin/cabinets/${id}`, {
    method: 'PUT',
    body: JSON.stringify({ name }),
  });
}

export function updateCabinetStatus(id: string, active: boolean): Promise<Cabinet> {
  return apiRequest<Cabinet>(`/admin/cabinets/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ active }),
  });
}

export function getCabinetImageUrl(imageUrl: string | null): string | null {
  if (!imageUrl) return null;
  if (/^https?:\/\//i.test(imageUrl)) return imageUrl;
  const apiUrl = (import.meta.env.VITE_API_URL || 'http://localhost:8081/api').replace(/\/$/, '');
  return `${new URL(apiUrl).origin}${imageUrl}`;
}

export function uploadCabinetImage(id: string, file: File): Promise<Cabinet> {
  const token = sessionStorage.getItem('cabinet-medical.access-token');
  const apiUrl = (import.meta.env.VITE_API_URL || 'http://localhost:8081/api').replace(/\/$/, '');
  const formData = new FormData();
  formData.append('file', file);
  return fetch(`${apiUrl}/admin/cabinets/${id}/image`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: formData,
  }).then(async (response) => {
    if (!response.ok) {
      const body = await response.json().catch(() => null) as { message?: string } | null;
      throw new Error(body?.message || "Impossible d'enregistrer l'image.");
    }
    return response.json() as Promise<Cabinet>;
  });
}

export function deleteCabinetImage(id: string): Promise<Cabinet> {
  return apiRequest<Cabinet>(`/admin/cabinets/${id}/image`, { method: 'DELETE' });
}
