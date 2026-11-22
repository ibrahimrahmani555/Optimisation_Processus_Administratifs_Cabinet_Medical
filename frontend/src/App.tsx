import { useEffect, useState } from 'react';
import { getCurrentUser, logout } from './auth';
import type { AuthUser } from './auth';
import AdminLayout from './admin/AdminLayout';
import DashboardPage from './admin/DashboardPage';
import CabinetPage from './admin/CabinetPage';
import LoginPage from './LoginPage';

type AdminPage = 'dashboard' | 'cabinets';

function pageFromHash(): AdminPage {
  return window.location.hash === '#cabinets' ? 'cabinets' : 'dashboard';
}

export default function App() {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [checkingSession, setCheckingSession] = useState(true);
  const [page, setPage] = useState<AdminPage>(pageFromHash);

  useEffect(() => {
    getCurrentUser()
      .then(setUser)
      .catch(() => setUser(null))
      .finally(() => setCheckingSession(false));
  }, []);

  useEffect(() => {
    const onHashChange = () => setPage(pageFromHash());
    window.addEventListener('hashchange', onHashChange);
    return () => window.removeEventListener('hashchange', onHashChange);
  }, []);

  if (checkingSession) {
    return <div className="session-loader" role="status"><span className="brand-mark"><i /><i /></span><p>Vérification de votre session...</p></div>;
  }

  if (!user) {
    return <LoginPage onAuthenticated={setUser} />;
  }

  if (user.role !== 'ADMIN') {
    return <div className="session-loader"><p>Cette interface est réservée aux administrateurs.</p><button className="primary" onClick={() => { logout(); setUser(null); }}>Se déconnecter</button></div>;
  }

  return (
    <AdminLayout
      user={user}
      activePage={page}
      onNavigate={(nextPage) => {
        setPage(nextPage);
        window.location.hash = nextPage;
      }}
      onLogout={() => { logout(); setUser(null); }}
    >
      {page === 'cabinets'
        ? <CabinetPage />
        : <DashboardPage user={user} />}
    </AdminLayout>
  );
}
