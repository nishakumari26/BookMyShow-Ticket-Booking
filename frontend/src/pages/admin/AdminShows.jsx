import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../../services/api';

export default function AdminShows() {
  const [shows, setShows] = useState([]);
  const [form, setForm] = useState({ movieId: '', theaterId: '', screenId: '', showDate: '', startTime: '19:30', endTime: '22:30' });
  const [error, setError] = useState('');

  const load = async () => {
    const data = unwrap(await api.get('/shows', { params: { size: 50 } }));
    setShows(data.content || []);
  };

  useEffect(() => { load().catch((e) => setError(errorMessage(e))); }, []);

  const save = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/shows', {
        movieId: Number(form.movieId),
        theaterId: Number(form.theaterId),
        screenId: Number(form.screenId),
        showDate: form.showDate,
        startTime: form.startTime.length === 5 ? `${form.startTime}:00` : form.startTime,
        endTime: form.endTime.length === 5 ? `${form.endTime}:00` : form.endTime,
      });
      await load();
    } catch (err) { setError(errorMessage(err)); }
  };

  const remove = async (id) => {
    try {
      await api.delete(`/admin/shows/${id}`);
      await load();
    } catch (err) { setError(errorMessage(err)); }
  };

  return (
    <>
      {error && <div className="alert">{error}</div>}
      <form className="form" onSubmit={save}>
        <input placeholder="Movie ID" value={form.movieId} onChange={(e) => setForm({ ...form, movieId: e.target.value })} required />
        <input placeholder="Theater ID" value={form.theaterId} onChange={(e) => setForm({ ...form, theaterId: e.target.value })} required />
        <input placeholder="Screen ID" value={form.screenId} onChange={(e) => setForm({ ...form, screenId: e.target.value })} required />
        <input type="date" value={form.showDate} onChange={(e) => setForm({ ...form, showDate: e.target.value })} required />
        <input type="time" value={form.startTime} onChange={(e) => setForm({ ...form, startTime: e.target.value })} />
        <input type="time" value={form.endTime} onChange={(e) => setForm({ ...form, endTime: e.target.value })} />
        <button className="btn" type="submit">Create show</button>
      </form>
      <table>
        <thead><tr><th>Movie</th><th>Theater</th><th>When</th><th /></tr></thead>
        <tbody>
          {shows.map((s) => (
            <tr key={s.id}>
              <td>{s.movieTitle}</td>
              <td>{s.theaterName} / {s.screenName}</td>
              <td>{s.showDate} {s.startTime}</td>
              <td><button className="btn ghost" onClick={() => remove(s.id)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
