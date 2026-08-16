import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../../services/api';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/admin/dashboard').then((r) => setStats(unwrap(r))).catch((e) => setError(errorMessage(e)));
  }, []);

  if (error) return <div className="alert">{error}</div>;
  if (!stats) return <p>Loading...</p>;

  const items = [
    ['Users', stats.totalUsers],
    ['Movies', stats.totalMovies],
    ['Theaters', stats.totalTheaters],
    ['Shows', stats.totalShows],
    ['Bookings', stats.totalBookings],
    ['Confirmed', stats.confirmedBookings],
    ['Cancelled', stats.cancelledBookings],
  ];

  return (
    <div className="stats">
      {items.map(([label, value]) => (
        <div className="stat" key={label}><b>{value}</b>{label}</div>
      ))}
    </div>
  );
}
