import type { RecentActivity } from './types';

interface RecentActivitiesProps {
  activities: RecentActivity[];
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('fr-FR', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
}

export default function RecentActivities({ activities }: RecentActivitiesProps) {
  return (
    <article className="panel activities-panel">
      <div className="panel-title"><div><h2>Activités récentes</h2><p>Dernières actions administratives</p></div></div>
      {activities.length === 0 ? (
        <div className="empty-state"><strong>Aucune activité récente</strong><span>Les actions administratives apparaîtront ici.</span></div>
      ) : (
        <div className="activity-list">
          {activities.map(activity => (
            <div className="activity-row" key={activity.id}>
              <span className="activity-dot" aria-hidden="true" />
              <div><strong>{activity.description}</strong><small>{activity.actorName || 'Système'} · {formatDate(activity.occurredAt)}</small></div>
            </div>
          ))}
        </div>
      )}
    </article>
  );
}
