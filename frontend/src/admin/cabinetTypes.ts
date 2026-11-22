export type CabinetStatusFilter = 'ALL' | 'ACTIVE' | 'INACTIVE';

export interface Cabinet {
  id: string;
  name: string;
  active: boolean;
  createdAt: string;
  imageUrl: string | null;
  doctors: number;
  secretaries: number;
  patients: number;
}

export interface CabinetSummary {
  total: number;
  active: number;
  inactive: number;
}

export interface CabinetPage {
  content: Cabinet[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  summary: CabinetSummary;
}
