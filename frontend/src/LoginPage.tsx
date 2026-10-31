import { useState } from 'react';
import type { FormEvent } from 'react';
import { ApiError, login } from './auth';
import type { AuthUser } from './auth';
import logo from './public/logo.png';
import doctor from './public/doctor.png';

const SUPPORT_EMAIL = 'support@cabinet.ma';

interface LoginPageProps {
  onAuthenticated: (user: AuthUser) => void;
}

const UserIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <circle cx="12" cy="7" r="4" />
    <path d="M4 21v-1a6 6 0 0 1 6-6h4a6 6 0 0 1 6 6v1" />
  </svg>
);

const LockIcon = () => (
  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <rect x="4" y="11" width="16" height="10" rx="2" />
    <path d="M8 11V7a4 4 0 0 1 8 0v4" />
  </svg>
);

const EyeIcon = ({ off }: { off: boolean }) => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
    <path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12z" />
    <circle cx="12" cy="12" r="3" />
    {off && <path d="M3 3l18 18" />}
  </svg>
);

export default function LoginPage({ onAuthenticated }: LoginPageProps) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [remember, setRemember] = useState(true);
  const [showSupport, setShowSupport] = useState(false);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    if (!email.trim() || !password) {
      setError('Renseignez votre adresse e-mail et votre mot de passe.');
      return;
    }

    setSubmitting(true);
    try {
      onAuthenticated(await login(email.trim(), password));
    } catch (caught) {
      setError(caught instanceof ApiError ? caught.message : 'Le serveur est indisponible. Réessayez plus tard.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="lg-page">
      <section className="lg-showcase" aria-label="Présentation de Cabinova">
        <span className="lg-tint" aria-hidden="true" />
        <span className="lg-orb" aria-hidden="true" />
        <span className="lg-ring lg-ring--1" aria-hidden="true" />
        <span className="lg-ring lg-ring--2" aria-hidden="true" />
        <div className="lg-showcase-card">
          <h1>Seamless healthcare access with smart, modern clinic</h1>
          <p>Experience efficient, secure, and user-friendly healthcare management designed for modern clinics and growing practices.</p>
          <img className="lg-doctor" src={doctor} alt="" />
        </div>
      </section>

      <section className="lg-panel">
        <div className="lg-panel-inner">
          <a className="lg-logo" href="/" aria-label="Cabinova">
            <img src={logo} alt="Cabinova" />
          </a>

          <div className="lg-card">
            <h2>Sign In</h2>
            <p className="lg-sub">Please enter below details to access the dashboard</p>

            <form onSubmit={handleSubmit} noValidate>
              {error && <div className="lg-error" role="alert"><span>!</span>{error}</div>}

              <label htmlFor="email">Email Address</label>
              <div className="lg-input">
                <UserIcon />
                <input
                  id="email"
                  type="email"
                  autoComplete="username"
                  value={email}
                  onChange={event => setEmail(event.target.value)}
                  placeholder="Enter Email Address"
                  disabled={submitting}
                />
              </div>

              <label htmlFor="password">Password</label>
              <div className="lg-input">
                <LockIcon />
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  autoComplete="current-password"
                  value={password}
                  onChange={event => setPassword(event.target.value)}
                  placeholder="************"
                  disabled={submitting}
                />
                <button
                  type="button"
                  className="lg-eye"
                  onClick={() => setShowPassword(value => !value)}
                  aria-label={showPassword ? 'Masquer le mot de passe' : 'Afficher le mot de passe'}
                >
                  <EyeIcon off={!showPassword} />
                </button>
              </div>

              <div className="lg-row">
                <label className="lg-check" htmlFor="remember">
                  <input id="remember" type="checkbox" checked={remember} onChange={event => setRemember(event.target.checked)} />
                  <span>Remember Me</span>
                </label>
                <button
                  type="button"
                  className="lg-forgot"
                  onClick={() => setShowSupport(value => !value)}
                  aria-expanded={showSupport}
                  aria-controls="support-note"
                >
                  Forgot password?
                </button>
              </div>

              {showSupport && (
                <div id="support-note" className="lg-support" role="status">
                  <p>To reset your password, please contact your administrator:</p>
                  <a href={`mailto:${SUPPORT_EMAIL}`}>{SUPPORT_EMAIL}</a>
                </div>
              )}

              <button className="lg-submit" type="submit" disabled={submitting}>
                {submitting ? 'Connexion en cours...' : 'Login'}
              </button>
            </form>
          </div>
        </div>
      </section>
    </main>
  );
}