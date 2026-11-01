import type { DashboardStatistics } from './types';

interface StatisticsCardsProps {
  statistics: DashboardStatistics;
}

const cards = [
  ['totalCabinets', 'Cabinets', 'CA', 'indigo'],
  ['activeCabinets', 'Cabinets actifs', 'AC', 'green'],
  ['inactiveCabinets', 'Cabinets inactifs', 'IN', 'orange'],
  ['doctors', 'Médecins', 'ME', 'cyan'],
  ['secretaries', 'Secrétaires', 'SE', 'indigo'],
  ['patients', 'Patients', 'PA', 'green'],
] as const;

export default function StatisticsCards({ statistics }: StatisticsCardsProps) {
  return (
    <section className="stat-grid admin-stat-grid" aria-label="Statistiques globales">
      {cards.map(([key, label, icon, tone]) => (
        <article className={`stat-card ${tone}`} key={key}>
          <div className="stat-top"><span className="stat-icon">{icon}</span></div>
          <p>{label}</p><strong>{statistics[key].toLocaleString('fr-FR')}</strong>
        </article>
      ))}
    </section>
  );
}
