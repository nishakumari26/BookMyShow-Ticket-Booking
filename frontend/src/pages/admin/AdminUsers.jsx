import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../../services/api';

export default function AdminUsers() {
  const [items, setItems] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/admin/users', { params: { size: 50 } })
      .then((r) => setItems(unwrap(r).content || []))
      .catch((e) => setError(errorMessage(e)));
  }, []);

  if (error) return <div className="alert">{error}</div>;

  return (
    <table>
      <thead><tr><th>Name</th><th>Email</th><th>Role</th></tr></thead>
      <tbody>
        {items.map((u) => (
          <tr key={u.id}><td>{u.name}</td><td>{u.email}</td><td>{u.role}</td></tr>
        ))}
      </tbody>
    </table>
  );
}
