import type { DashboardData, DistributionValue, MonthlyValue, RecentActivity, DashboardStatistics } from './types';

const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8081/api').replace(/\/$/, '');
const DASHBOARD_PATH = `${API_URL}/admin/dashboard`;
const TOKEN_KEY = 'cabinet-medical.access-token';

class DashboardApiError extends Error {
  constructor(message: string, public readonly status: number) {
    super(message);
  }
}

async function get<T>(path: string): Promise<T> {
  const response = await fetch(`${DASHBOARD_PATH}${path}`, {
    headers: {
      Accept: 'application/json',
      Authorization: `Bearer ${sessionStorage.getItem(TOKEN_KEY) || ''}`,
    },
  });

  if (!response.ok) {
    const body = await response.json().catch(() => null) as { message?: string } | null;
    throw new DashboardApiError(body?.message || 'Impossible de charger le tableau de bord.', response.status);
  }
  return response.json() as Promise<T>;
}

export async function getDashboardData(): Promise<DashboardData> {
  const [statistics, cabinetsEvolution, cabinetStatusDistribution, usersDistribution, registrationsEvolution, recentActivities] =
    await Promise.all([
      get<DashboardStatistics>('/statistics'),
      get<MonthlyValue[]>('/cabinets-evolution'),
      get<DistributionValue[]>('/cabinets-status'),
      get<DistributionValue[]>('/users-distribution'),
      get<MonthlyValue[]>('/registrations-evolution'),
      get<RecentActivity[]>('/recent-activities?limit=10'),
    ]);

  return {
    statistics,
    cabinetsEvolution,
    cabinetStatusDistribution,
    usersDistribution,
    registrationsEvolution,
    recentActivities,
  };
}

export { DashboardApiError };
