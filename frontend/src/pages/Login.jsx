import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { errorMessage } from '../services/api';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const submit = async (e) => {
    e.preventDefault();
    try {
      const data = await login(email, password);
      navigate(data.role === 'ADMIN' ? '/admin' : '/');
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <div className="auth-box panel">
      <h1>Login</h1>
      {error && <div className="alert">{error}</div>}
      <form className="form" onSubmit={submit}>
        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" required />
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Password" required />
        <button className="btn" type="submit">Sign in</button>
      </form>
      <p className="meta">New here? <Link to="/register">Create an account</Link></p>
    </div>
  );
}
