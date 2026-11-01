import {
  Bar, BarChart, CartesianGrid, Cell, Line, LineChart, Pie, PieChart, ResponsiveContainer,
  Tooltip, XAxis, YAxis,
} from 'recharts';
import type { DashboardData } from './types';

interface DashboardChartsProps {
  data: DashboardData;
}

const statusColors = ['#24ad72', '#f28b45'];
const roleColors = ['#343cac', '#12bdd2', '#e94f89'];

export default function DashboardCharts({ data }: DashboardChartsProps) {
  return (
    <section className="dashboard-grid admin-chart-grid">
      <article className="panel admin-chart-panel">
        <div className="panel-title"><div><h2>Évolution des cabinets</h2><p>Inscriptions mensuelles</p></div></div>
        <div className="rechart-wrap">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data.cabinetsEvolution} margin={{ top: 10, right: 12, left: -20, bottom: 0 }}>
              <CartesianGrid stroke="#eef0f4" vertical={false} />
              <XAxis dataKey="month" tick={{ fontSize: 10, fill: '#77829a' }} />
              <YAxis allowDecimals={false} tick={{ fontSize: 10, fill: '#77829a' }} />
              <Tooltip />
              <Line type="monotone" dataKey="value" name="Cabinets" stroke="#343cac" strokeWidth={3} dot={{ r: 3 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </article>

      <article className="panel admin-chart-panel">
        <div className="panel-title"><div><h2>Statut des cabinets</h2><p>Répartition actuelle</p></div></div>
        <div className="rechart-wrap pie-wrap">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie data={data.cabinetStatusDistribution} dataKey="value" nameKey="status" innerRadius={48} outerRadius={76} paddingAngle={3}>
                {data.cabinetStatusDistribution.map((entry, index) => <Cell key={entry.status} fill={statusColors[index % statusColors.length]} />)}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
        <div className="chart-labels">{data.cabinetStatusDistribution.map((item, index) => <span key={item.status}><i style={{ background: statusColors[index % statusColors.length] }} />{item.status === 'ACTIVE' ? 'Actifs' : 'Inactifs'} ({item.value})</span>)}</div>
      </article>

      <article className="panel admin-chart-panel">
        <div className="panel-title"><div><h2>Utilisateurs par rôle</h2><p>Répartition globale</p></div></div>
        <div className="rechart-wrap pie-wrap">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie data={data.usersDistribution} dataKey="value" nameKey="role" innerRadius={48} outerRadius={76} paddingAngle={3}>
                {data.usersDistribution.map((entry, index) => <Cell key={entry.role} fill={roleColors[index % roleColors.length]} />)}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
        <div className="chart-labels">{data.usersDistribution.map((item, index) => <span key={item.role}><i style={{ background: roleColors[index % roleColors.length] }} />{item.role} ({item.value})</span>)}</div>
      </article>

      <article className="panel admin-chart-panel">
        <div className="panel-title"><div><h2>Évolution des inscriptions</h2><p>Utilisateurs et patients</p></div></div>
        <div className="rechart-wrap">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={data.registrationsEvolution} margin={{ top: 10, right: 12, left: -20, bottom: 0 }}>
              <CartesianGrid stroke="#eef0f4" vertical={false} />
              <XAxis dataKey="month" tick={{ fontSize: 10, fill: '#77829a' }} />
              <YAxis allowDecimals={false} tick={{ fontSize: 10, fill: '#77829a' }} />
              <Tooltip />
              <Bar dataKey="value" name="Inscriptions" fill="#12bdd2" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </article>
    </section>
  );
}
