import { useState, type ReactNode } from 'react';
import { Bell, Building2, CalendarDays, ChevronDown, ChevronRight, FileClock, LayoutDashboard, LogOut, Menu, Search, Settings, Users, X } from 'lucide-react';
import type { AuthUser } from '../auth';
import logo from '../public/logo.png';

interface AdminLayoutProps {
  user: AuthUser;
  onLogout: () => void;
  children: ReactNode;
}

export default function AdminLayout({ user, onLogout, children }: AdminLayoutProps) {
  const initials = `${user.firstName[0] || ''}${user.lastName[0] || ''}`.toUpperCase();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="app-shell">
      <button className={`sidebar-overlay${sidebarOpen ? ' is-visible' : ''}`} type="button" aria-label="Fermer le menu" onClick={() => setSidebarOpen(false)} />
      <aside className={`sidebar${sidebarOpen ? ' is-open' : ''}`}>
        <a className="brand" href="#dashboard" aria-label="Accueil Cabinova">
          <img className="brand-logo" src={logo} alt="Cabinova" />
          <button className="sidebar-collapse" type="button" aria-label="Fermer le menu" onClick={(event) => { event.preventDefault(); setSidebarOpen(false); }}><X size={14} /></button>
        </a>
        <div className="clinic-card">
          <span className="clinic-avatar">AD</span>
          <div><strong>Administration</strong><small>Plateforme globale</small></div>
          <ChevronDown size={14} />
        </div>
        <nav aria-label="Navigation administrateur">
          <p>Menu principal</p>
          <a className="active" href="#dashboard"><LayoutDashboard size={16} />Tableau de bord<ChevronRight className="nav-chevron" size={14} /></a>
          <a href="#overview"><LayoutDashboard size={16} />Vue d’ensemble</a>
          <p>Administration</p>
          <a href="#cabinets"><Building2 size={16} />Cabinets</a>
          <a href="#users"><Users size={16} />Utilisateurs</a>
          <a href="#audit"><FileClock size={16} />Journal d'audit</a>
          <a href="#settings"><Settings size={16} />Paramètres</a>
        </nav>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div className="topbar-left">
            <button className="mobile-menu" type="button" aria-label="Ouvrir le menu" onClick={() => setSidebarOpen(true)}><Menu size={19} /></button>
            <label className="search"><Search size={16} /><input aria-label="Rechercher" placeholder="Rechercher..." /><kbd>⌘ K</kbd></label>
          </div>
          <div className="top-actions">
            <button className="top-help" type="button" aria-label="Calendrier"><CalendarDays size={16} /></button>
            <button className="top-help" type="button" aria-label="Paramètres"><Settings size={16} /></button>
            <button className="top-help notification-button" type="button" aria-label="Notifications"><Bell size={16} /><i /></button>
            <div className="profile">
              <span>{initials}</span><i className="online-dot" />
              <div><strong>{user.firstName} {user.lastName}</strong><small>Administrateur</small></div>
            </div>
            <button className="logout-button" type="button" onClick={onLogout} title="Se déconnecter"><LogOut size={15} /><span>Déconnexion</span></button>
          </div>
        </header>
        {children}
      </section>
    </div>
  );
}
