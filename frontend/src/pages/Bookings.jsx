import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../services/api';

export default function Bookings() {
  const [tab, setTab] = useState('all');
  const [items, setItems] = useState([]);
  const [error, setError] = useState('');

  const load = async (which) => {
    const path = which === 'upcoming' ? '/bookings/upcoming' : which === 'past' ? '/bookings/past' : '/bookings';
    try {
      const data = unwrap(await api.get(path));
      setItems(data.content || []);
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  useEffect(() => { load(tab); }, [tab]);

  const cancel = async (id) => {
    if (!window.confirm('Cancel this booking?')) return;
    try {
      await api.delete(`/bookings/${id}`);
      load(tab);
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <>
      <h1>My bookings</h1>
      <div className="row" style={{ marginBottom: 16 }}>
        {['all', 'upcoming', 'past'].map((t) => (
          <button key={t} className={`btn ${tab === t ? '' : 'secondary'}`} onClick={() => setTab(t)}>{t}</button>
        ))}
      </div>
      {error && <div className="alert">{error}</div>}
      {items.map((b) => (
        <div className="show-item" key={b.bookingId}>
          <div>
            <b>{b.movieTitle}</b>
            <div className="meta">{b.theaterName} · {b.showDate} · {b.selectedSeats?.join(', ')} · ₹{b.totalAmount} · {b.bookingStatus}</div>
          </div>
          {b.bookingStatus === 'CONFIRMED' && (
            <button className="btn ghost" onClick={() => cancel(b.bookingId)}>Cancel</button>
          )}
        </div>
      ))}
    </>
  );
}
