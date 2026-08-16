import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import api, { unwrap, errorMessage } from '../services/api';
import SeatMap from '../components/SeatMap';
import { useAuth } from '../context/AuthContext';

export default function SeatSelect() {
  const { showId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [show, setShow] = useState(null);
  const [seats, setSeats] = useState([]);
  const [selected, setSelected] = useState([]);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        setShow(unwrap(await api.get(`/shows/${showId}`)));
        setSeats(unwrap(await api.get(`/shows/${showId}/seats`)) || []);
      } catch (err) {
        setError(errorMessage(err));
      }
    })();
  }, [showId]);

  const selectedSeats = seats.filter((s) => selected.includes(s.showSeatId));
  const total = selectedSeats.reduce((sum, s) => sum + Number(s.price || 0), 0);

  const toggle = (seat) => {
    setSelected((cur) => cur.includes(seat.showSeatId)
      ? cur.filter((id) => id !== seat.showSeatId)
      : [...cur, seat.showSeatId]);
  };

  const book = async () => {
    if (!user) {
      navigate('/login');
      return;
    }
    setBusy(true);
    setError('');
    try {
      const booking = unwrap(await api.post('/bookings', { showId: Number(showId), showSeatIds: selected }));
      navigate('/confirmation', { state: { booking } });
    } catch (err) {
      setError(errorMessage(err));
      const fresh = unwrap(await api.get(`/shows/${showId}/seats`));
      setSeats(fresh || []);
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      {show && (
        <div className="hero">
          <h1>{show.movieTitle}</h1>
          <p>{show.theaterName} · {show.screenName} · {show.showDate} · {show.startTime?.slice(0, 5)}</p>
        </div>
      )}
      {error && <div className="alert">{error}</div>}
      <SeatMap seats={seats} selected={selected} onToggle={toggle} />
      <div className="panel">
        <p>Selected: {selectedSeats.map((s) => s.seatNumber).join(', ') || 'none'}</p>
        <p><b>Total: ₹{total.toFixed(2)}</b></p>
        <button className="btn" disabled={!selected.length || busy} onClick={book}>
          {busy ? 'Booking...' : 'Confirm booking'}
        </button>
      </div>
    </>
  );
}
