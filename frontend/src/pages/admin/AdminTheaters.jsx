import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../../services/api';

export default function AdminTheaters() {
  const [theaters, setTheaters] = useState([]);
  const [form, setForm] = useState({ name: '', city: '', location: '', address: '' });
  const [screenName, setScreenName] = useState('Screen 1');
  const [theaterId, setTheaterId] = useState('');
  const [screens, setScreens] = useState([]);
  const [seat, setSeat] = useState({ screenId: '', seatNumber: 'A1', rowNumber: 'A', seatType: 'REGULAR', price: 250 });
  const [error, setError] = useState('');

  const load = async () => {
    const data = unwrap(await api.get('/theaters', { params: { size: 50 } }));
    setTheaters(data.content || []);
  };

  useEffect(() => { load().catch((e) => setError(errorMessage(e))); }, []);

  const saveTheater = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/theaters', form);
      setForm({ name: '', city: '', location: '', address: '' });
      await load();
    } catch (err) { setError(errorMessage(err)); }
  };

  const addScreen = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/screens', { name: screenName, theaterId: Number(theaterId) });
      const list = unwrap(await api.get(`/theaters/${theaterId}/screens`));
      setScreens(list || []);
    } catch (err) { setError(errorMessage(err)); }
  };

  const addSeat = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/admin/screens/${seat.screenId}/seats`, { ...seat, price: Number(seat.price) });
      setError('');
    } catch (err) { setError(errorMessage(err)); }
  };

  return (
    <>
      {error && <div className="alert">{error}</div>}
      <h3>Add theater</h3>
      <form className="form" onSubmit={saveTheater}>
        <input placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
        <input placeholder="City" value={form.city} onChange={(e) => setForm({ ...form, city: e.target.value })} required />
        <input placeholder="Location" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} />
        <input placeholder="Address" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} />
        <button className="btn" type="submit">Save theater</button>
      </form>
      <table>
        <thead><tr><th>Name</th><th>City</th></tr></thead>
        <tbody>{theaters.map((t) => <tr key={t.id}><td>{t.name} (#{t.id})</td><td>{t.city}</td></tr>)}</tbody>
      </table>
      <h3>Add screen</h3>
      <form className="form" onSubmit={addScreen}>
        <input placeholder="Theater ID" value={theaterId} onChange={(e) => setTheaterId(e.target.value)} />
        <input placeholder="Screen name" value={screenName} onChange={(e) => setScreenName(e.target.value)} />
        <button className="btn" type="submit">Add screen</button>
      </form>
      {screens.length > 0 && <p className="meta">Screens: {screens.map((s) => `${s.name} (#${s.id})`).join(', ')}</p>}
      <h3>Add seat</h3>
      <form className="form" onSubmit={addSeat}>
        <input placeholder="Screen ID" value={seat.screenId} onChange={(e) => setSeat({ ...seat, screenId: e.target.value })} />
        <input placeholder="Seat number" value={seat.seatNumber} onChange={(e) => setSeat({ ...seat, seatNumber: e.target.value })} />
        <input placeholder="Row" value={seat.rowNumber} onChange={(e) => setSeat({ ...seat, rowNumber: e.target.value })} />
        <select value={seat.seatType} onChange={(e) => setSeat({ ...seat, seatType: e.target.value })}>
          <option>REGULAR</option><option>PREMIUM</option><option>VIP</option>
        </select>
        <input type="number" value={seat.price} onChange={(e) => setSeat({ ...seat, price: e.target.value })} />
        <button className="btn" type="submit">Add seat</button>
      </form>
    </>
  );
}
