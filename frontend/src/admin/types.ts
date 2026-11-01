export interface DashboardStatistics {
  totalCabinets: number;
  activeCabinets: number;
  inactiveCabinets: number;
  doctors: number;
  secretaries: number;
  patients: number;
}

export interface MonthlyValue {
  month: string;
  value: number;
}

export interface DistributionValue {
  status?: string;
  role?: string;
  value: number;
}

export interface RecentActivity {
  id: string;
  action: string;
  entityType: string;
  description: string;
  actorName: string | null;
  occurredAt: string;
}

export interface DashboardData {
  statistics: DashboardStatistics;
  cabinetsEvolution: MonthlyValue[];
  cabinetStatusDistribution: DistributionValue[];
  usersDistribution: DistributionValue[];
  registrationsEvolution: MonthlyValue[];
  recentActivities: RecentActivity[];
}
