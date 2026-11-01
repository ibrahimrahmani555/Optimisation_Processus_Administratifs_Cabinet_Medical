import { useQuery } from '@tanstack/react-query';
import { getDashboardData } from './dashboardApi';

export const dashboardQueryKey = ['admin', 'dashboard'];

export function useDashboardData() {
  return useQuery({
    queryKey: dashboardQueryKey,
    queryFn: getDashboardData,
    staleTime: 60_000,
  });
}
