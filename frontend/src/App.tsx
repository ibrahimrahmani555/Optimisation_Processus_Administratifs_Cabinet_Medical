import { useEffect, useState } from 'react';
import { getCurrentUser, logout } from './auth';
import type { AuthUser } from './auth';
import LoginPage from './LoginPage';

const stats = [
  { label: 'Medecins', value: '24', delta: '+4,8 %', tone: 'indigo' },
  { label: 'Patients', value: '1 248', delta: '+2,2 %', tone: 'orange' },
  { label: 'Rendez-vous', value: '86', delta: '+8,1 %', tone: 'cyan' },
  { label: 'Revenus', value: '54 120 DH', delta: '+2,5 %', tone: 'green' },
];

const appointments = [
  { time: '09:00', patient: 'Sara El Mansouri', reason: 'Consultation', status: 'Confirme' },
  { time: '10:30', patient: 'Youssef Amrani', reason: 'Controle', status: 'En attente' },
  { time: '11:15', patient: 'Nadia Benali', reason: 'Consultation', status: 'Confirme' },
];

const months = [38, 45, 58, 52, 70, 34, 44, 62, 76, 69, 53, 47];

export default function App() {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [checkingSession, setCheckingSession] = useState(true);

  useEffect(() => {
    getCurrentUser()
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setCheckingSession(false));
  }, []);

  if (checkingSession) {
    return <div className="session-loader" role="status"><span className="brand-mark"><i /><i /></span><p>Verification de votre session...</p></div>;
  }

  if (!user) {
    return <LoginPage onAuthenticated={setUser} />;
  }

  const roleLabels = { ADMIN: 'Administrateur', SECRETAIRE: 'Secretaire', MEDECIN: 'Medecin' };
  const initials = `${user.firstName[0] || ''}${user.lastName[0] || ''}`.toUpperCase();
  const handleLogout = () => { logout(); setUser(null); };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="#dashboard" aria-label="Accueil Cabinet Medical">
          <span className="brand-mark"><i /><i /></span>
          <strong>Cabinet<span>Medical</span></strong>
        </a>
        <div className="clinic-card"><span className="clinic-avatar">CM</span><div><strong>{user.cabinetName}</strong><small>Espace securise</small></div></div>
        <nav aria-label="Navigation principale">
          <p>MENU PRINCIPAL</p>
          <a className="active" href="#dashboard"><span>DB</span>Tableau de bord</a>
          {user.role !== 'ADMIN' && <><a href="#patients"><span>PA</span>Patients</a><a href="#appointments"><span>RD</span>Rendez-vous</a><a href="#waiting"><span>FA</span>File d'attente</a></>}
          {user.role === 'MEDECIN' && <><p>GESTION MEDICALE</p><a href="#consultations"><span>CO</span>Consultations</a><a href="#records"><span>DM</span>Dossiers medicaux</a><a href="#prescriptions"><span>OR</span>Ordonnances</a></>}
          {(user.role === 'ADMIN' || user.role === 'SECRETAIRE') && <><p>ADMINISTRATION</p>{user.role === 'SECRETAIRE' && <a href="#billing"><span>FC</span>Facturation</a>}{user.role === 'ADMIN' && <a href="#users"><span>UT</span>Utilisateurs</a>}</>}
        </nav>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div className="search"><span>⌕</span><input aria-label="Rechercher" placeholder="Rechercher un patient, un rendez-vous..." /></div>
          <div className="top-actions">
            <button className="ai-button">Assistant medical</button>
            <button className="icon-button" aria-label="Notifications">●</button>
            <button className="profile" type="button" onClick={handleLogout} title="Se deconnecter"><span>{initials}</span><div><strong>{user.firstName} {user.lastName}</strong><small>{roleLabels[user.role]} · Deconnexion</small></div></button>
          </div>
        </header>

        <main>
          <div className="page-heading">
            <div><p>Vue d'ensemble</p><h1>Tableau de bord</h1></div>
            <div className="heading-actions"><button className="secondary">Voir l'agenda</button><button className="primary">+ Nouveau rendez-vous</button></div>
          </div>

          <section className="stat-grid" aria-label="Statistiques principales">
            {stats.map((stat) => (
              <article className={`stat-card ${stat.tone}`} key={stat.label}>
                <div className="stat-top"><span className="stat-icon">{stat.label.slice(0, 2).toUpperCase()}</span><small>{stat.delta}</small></div>
                <p>{stat.label}</p><strong>{stat.value}</strong>
                <div className="sparkline"><i /><i /><i /><i /><i /><i /></div>
              </article>
            ))}
          </section>

          <section className="dashboard-grid">
            <article className="panel chart-panel">
              <div className="panel-title"><div><h2>Statistiques des rendez-vous</h2><p>Evolution mensuelle</p></div><button>Cette annee⌄</button></div>
              <div className="chart-legend"><span><i className="completed" />Termines</span><span><i className="scheduled" />Planifies</span></div>
              <div className="bar-chart" aria-label="Graphique des rendez-vous mensuels">
                {months.map((height, index) => <div className="bar-column" key={index}><div style={{ height: `${height}%` }}><i /></div><small>{['Jan','Fev','Mar','Avr','Mai','Juin','Juil','Aout','Sep','Oct','Nov','Dec'][index]}</small></div>)}
              </div>
            </article>

            <article className="panel appointment-panel">
              <div className="panel-title"><div><h2>Rendez-vous du jour</h2><p>23 septembre 2026</p></div><button>Tout voir</button></div>
              <div className="appointment-list">
                {appointments.map((item) => (
                  <div className="appointment" key={`${item.time}-${item.patient}`}>
                    <time>{item.time}</time><span className="patient-avatar">{item.patient.split(' ').map(word => word[0]).slice(0,2).join('')}</span>
                    <div><strong>{item.patient}</strong><small>{item.reason}</small></div><em>{item.status}</em>
                  </div>
                ))}
              </div>
              <button className="full-button">Voir tous les rendez-vous</button>
            </article>

            <article className="panel queue-panel">
              <div className="panel-title"><div><h2>File d'attente</h2><p>Patients arrives</p></div><span className="count">3</span></div>
              <div className="queue-row"><span>01</span><div><strong>Youssef Amrani</strong><small>En attente depuis 8 min</small></div><button>Appeler</button></div>
              <div className="queue-row"><span>02</span><div><strong>Salma Idrissi</strong><small>En attente depuis 3 min</small></div><button>Appeler</button></div>
            </article>

            <article className="panel progress-panel">
              <div className="panel-title"><div><h2>Activite du cabinet</h2><p>Aujourd'hui</p></div></div>
              <div className="progress-item"><div><span>Consultations terminees</span><strong>18 / 24</strong></div><progress value="18" max="24" /></div>
              <div className="progress-item"><div><span>Factures payees</span><strong>14 / 18</strong></div><progress value="14" max="18" /></div>
              <div className="progress-item"><div><span>Dossiers completes</span><strong>21 / 24</strong></div><progress value="21" max="24" /></div>
            </article>
          </section>
        </main>
      </section>
    </div>
  );
}
