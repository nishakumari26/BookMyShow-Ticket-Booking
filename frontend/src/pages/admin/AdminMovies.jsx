import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../../services/api';

const empty = { title: '', description: '', language: 'English', genre: 'Action', duration: 120, releaseDate: '', posterUrl: '', rating: 8, status: 'NOW_SHOWING' };

export default function AdminMovies() {
  const [movies, setMovies] = useState([]);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [error, setError] = useState('');

  const load = async () => {
    const data = unwrap(await api.get('/movies', { params: { size: 50 } }));
    setMovies(data.content || []);
  };

  useEffect(() => { load().catch((e) => setError(errorMessage(e))); }, []);

  const save = async (e) => {
    e.preventDefault();
    try {
      const body = { ...form, duration: Number(form.duration), rating: Number(form.rating) };
      if (editId) await api.put(`/admin/movies/${editId}`, body);
      else await api.post('/admin/movies', body);
      setForm(empty);
      setEditId(null);
      await load();
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  const remove = async (id) => {
    if (!window.confirm('Delete movie?')) return;
    try {
      await api.delete(`/admin/movies/${id}`);
      await load();
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <>
      {error && <div className="alert">{error}</div>}
      <form className="form" onSubmit={save}>
        <input placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
        <textarea placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <div className="row">
          <input placeholder="Language" value={form.language} onChange={(e) => setForm({ ...form, language: e.target.value })} />
          <input placeholder="Genre" value={form.genre} onChange={(e) => setForm({ ...form, genre: e.target.value })} />
        </div>
        <div className="row">
          <input type="number" placeholder="Duration" value={form.duration} onChange={(e) => setForm({ ...form, duration: e.target.value })} />
          <input type="number" step="0.1" placeholder="Rating" value={form.rating} onChange={(e) => setForm({ ...form, rating: e.target.value })} />
        </div>
        <input type="date" value={form.releaseDate} onChange={(e) => setForm({ ...form, releaseDate: e.target.value })} />
        <input placeholder="Poster URL" value={form.posterUrl} onChange={(e) => setForm({ ...form, posterUrl: e.target.value })} />
        <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
          <option>NOW_SHOWING</option>
          <option>UPCOMING</option>
          <option>ENDED</option>
        </select>
        <button className="btn" type="submit">{editId ? 'Update' : 'Add movie'}</button>
      </form>
      <table>
        <thead><tr><th>Title</th><th>Language</th><th /><th /></tr></thead>
        <tbody>
          {movies.map((m) => (
            <tr key={m.id}>
              <td>{m.title}</td>
              <td>{m.language}</td>
              <td><button className="btn ghost" onClick={() => { setEditId(m.id); setForm({ ...empty, ...m, releaseDate: m.releaseDate || '' }); }}>Edit</button></td>
              <td><button className="btn ghost" onClick={() => remove(m.id)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
