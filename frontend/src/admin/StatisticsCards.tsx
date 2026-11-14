import { Building2, CheckCircle2, CircleOff, ClipboardList, Stethoscope, Users } from 'lucide-react';
import type { DashboardStatistics } from './types';

interface StatisticsCardsProps {
  statistics: DashboardStatistics;
}

const cards = [
  ['totalCabinets', 'Cabinets', Building2, 'indigo'],
  ['activeCabinets', 'Cabinets actifs', CheckCircle2, 'green'],
  ['inactiveCabinets', 'Cabinets inactifs', CircleOff, 'orange'],
  ['doctors', 'Médecins', Stethoscope, 'cyan'],
  ['secretaries', 'Secrétaires', ClipboardList, 'indigo'],
  ['patients', 'Patients', Users, 'green'],
] as const;

export default function StatisticsCards({ statistics }: StatisticsCardsProps) {
  return (
    <section className="stat-grid admin-stat-grid" aria-label="Statistiques globales">
      {cards.map(([key, label, Icon, tone]) => (
        <article className={`stat-card ${tone}`} key={key}>
          <div className="stat-top"><span className="stat-icon"><Icon size={17} /></span><span className="stat-period">Global</span></div>
          <p>{label}</p><strong>{statistics[key].toLocaleString('fr-FR')}</strong>
        </article>
      ))}
    </section>
  );
}
