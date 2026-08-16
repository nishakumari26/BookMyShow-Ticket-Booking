import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../services/api';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user, refreshProfile } = useAuth();
  const [name, setName] = useState(user?.name || '');
  const [phone, setPhone] = useState(user?.phone || '');
  const [currentPassword, setCurrent] = useState('');
  const [newPassword, setNew] = useState('');
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    refreshProfile().then((p) => {
      setName(p.name);
      setPhone(p.phone || '');
    }).catch((err) => setError(errorMessage(err)));
  }, []);

  const save = async (e) => {
    e.preventDefault();
    setError('');
    try {
      unwrap(await api.put('/users/me', { name, phone }));
      await refreshProfile();
      setMsg('Profile updated');
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  const changePassword = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.put('/users/me/password', { currentPassword, newPassword });
      setMsg('Password changed');
      setCurrent('');
      setNew('');
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <>
      <h1>Profile</h1>
      {msg && <div className="ok">{msg}</div>}
      {error && <div className="alert">{error}</div>}
      <form className="form" onSubmit={save}>
        <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Name" />
        <input value={user?.email || ''} disabled />
        <input value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="Phone" />
        <button className="btn" type="submit">Save</button>
      </form>
      <h3>Change password</h3>
      <form className="form" onSubmit={changePassword}>
        <input type="password" value={currentPassword} onChange={(e) => setCurrent(e.target.value)} placeholder="Current password" />
        <input type="password" value={newPassword} onChange={(e) => setNew(e.target.value)} placeholder="New password" />
        <button className="btn secondary" type="submit">Update password</button>
      </form>
    </>
  );
}
