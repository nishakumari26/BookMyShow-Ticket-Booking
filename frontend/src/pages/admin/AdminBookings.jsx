import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../../services/api';

export default function AdminBookings() {
  const [items, setItems] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/admin/bookings', { params: { size: 50 } })
      .then((r) => setItems(unwrap(r).content || []))
      .catch((e) => setError(errorMessage(e)));
  }, []);

  if (error) return <div className="alert">{error}</div>;

  return (
    <table>
      <thead><tr><th>Ref</th><th>User</th><th>Movie</th><th>Seats</th><th>Status</th></tr></thead>
      <tbody>
        {items.map((b) => (
          <tr key={b.bookingId}>
            <td>{b.bookingReference}</td>
            <td>{b.userName}</td>
            <td>{b.movieTitle}</td>
            <td>{b.selectedSeats?.join(', ')}</td>
            <td>{b.bookingStatus}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
