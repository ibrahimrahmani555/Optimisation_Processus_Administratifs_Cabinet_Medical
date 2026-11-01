import type { ReactNode } from 'react';
import type { AuthUser } from '../auth';

interface AdminLayoutProps {
  user: AuthUser;
  onLogout: () => void;
  children: ReactNode;
}

export default function AdminLayout({ user, onLogout, children }: AdminLayoutProps) {
  const initials = `${user.firstName[0] || ''}${user.lastName[0] || ''}`.toUpperCase();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="#dashboard" aria-label="Accueil Cabinova">
          <span className="brand-mark"><i /><i /></span>
          <strong>Cabinova</strong>
        </a>
        <div className="clinic-card">
          <span className="clinic-avatar">AD</span>
          <div><strong>Administration</strong><small>Plateforme globale</small></div>
        </div>
        <nav aria-label="Navigation administrateur">
          <p>MENU PRINCIPAL</p>
          <a className="active" href="#dashboard"><span>DB</span>Tableau de bord</a>
          <p>ADMINISTRATION</p>
          <a href="#cabinets"><span>CA</span>Cabinets</a>
          <a href="#users"><span>UT</span>Utilisateurs</a>
          <a href="#audit"><span>AU</span>Journal d'audit</a>
        </nav>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div className="search"><span aria-hidden="true">⌕</span><input aria-label="Rechercher" placeholder="Rechercher..." /></div>
          <div className="top-actions">
            <button className="icon-button" type="button" aria-label="Notifications">●</button>
            <button className="profile" type="button" onClick={onLogout} title="Se déconnecter">
              <span>{initials}</span>
              <div><strong>{user.firstName} {user.lastName}</strong><small>Administrateur · Déconnexion</small></div>
            </button>
          </div>
        </header>
        {children}
      </section>
    </div>
  );
}
