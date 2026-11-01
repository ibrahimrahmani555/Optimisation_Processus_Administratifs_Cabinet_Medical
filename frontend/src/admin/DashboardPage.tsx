import { useQueryClient } from '@tanstack/react-query';
import type { AuthUser } from '../auth';
import { dashboardQueryKey, useDashboardData } from './hooks';
import DashboardCharts from './DashboardCharts';
import RecentActivities from './RecentActivities';
import StatisticsCards from './StatisticsCards';

interface DashboardPageProps {
  user: AuthUser;
}

function DashboardSkeleton() {
  return <div className="dashboard-skeleton" role="status" aria-label="Chargement du tableau de bord"><span /><span /><span /><span /><span /><span /></div>;
}

export default function DashboardPage({ user }: DashboardPageProps) {
  const queryClient = useQueryClient();
  const dashboard = useDashboardData();

  if (dashboard.isPending) {
    return <main><div className="page-heading"><div><p>Vue d’ensemble</p><h1>Tableau de bord</h1></div></div><DashboardSkeleton /></main>;
  }

  if (dashboard.isError) {
    return <main><div className="page-heading"><div><p>Vue d’ensemble</p><h1>Tableau de bord</h1></div></div><div className="alert-error" role="alert"><span>!</span>Impossible de charger les statistiques. <button className="inline-action" onClick={() => dashboard.refetch()}>Réessayer</button></div></main>;
  }

  return (
    <main>
      <div className="page-heading">
        <div><p>Vue d’ensemble · {user.cabinetName}</p><h1>Tableau de bord ADMIN</h1></div>
        <button className="secondary" type="button" onClick={() => queryClient.invalidateQueries({ queryKey: dashboardQueryKey })}>Actualiser</button>
      </div>
      <StatisticsCards statistics={dashboard.data.statistics} />
      <DashboardCharts data={dashboard.data} />
      <RecentActivities activities={dashboard.data.recentActivities} />
    </main>
  );
}
