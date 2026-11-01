import { useEffect, useState } from 'react';
import { getCurrentUser, logout } from './auth';
import type { AuthUser } from './auth';
import AdminLayout from './admin/AdminLayout';
import DashboardPage from './admin/DashboardPage';
import LoginPage from './LoginPage';

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
    return <div className="session-loader" role="status"><span className="brand-mark"><i /><i /></span><p>Vérification de votre session...</p></div>;
  }

  if (!user) {
    return <LoginPage onAuthenticated={setUser} />;
  }

  if (user.role !== 'ADMIN') {
    return <div className="session-loader"><p>Cette interface est réservée aux administrateurs.</p><button className="primary" onClick={() => { logout(); setUser(null); }}>Se déconnecter</button></div>;
  }

  return (
    <AdminLayout user={user} onLogout={() => { logout(); setUser(null); }}>
      <DashboardPage user={user} />
    </AdminLayout>
  );
}
