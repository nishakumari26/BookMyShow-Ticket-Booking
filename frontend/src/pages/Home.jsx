import { useEffect, useState } from 'react';
import api, { unwrap, errorMessage } from '../services/api';
import MovieCard from '../components/MovieCard';

export default function Home() {
  const [movies, setMovies] = useState([]);
  const [q, setQ] = useState('');
  const [error, setError] = useState('');

  const load = async (query) => {
    try {
      const data = unwrap(await api.get('/movies', { params: { q: query || undefined, size: 20 } }));
      setMovies(data.content || []);
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  useEffect(() => { load(); }, []);

  return (
    <>
      <div className="hero">
        <h1>Book tickets. Skip the queue.</h1>
        <p>Search movies, pick a show, choose seats, and confirm in seconds.</p>
      </div>
      <form className="search-row" onSubmit={(e) => { e.preventDefault(); load(q); }}>
        <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="Search movies..." />
        <button className="btn" type="submit">Search</button>
      </form>
      {error && <div className="alert">{error}</div>}
      <div className="grid">
        {movies.map((m) => <MovieCard key={m.id} movie={m} />)}
      </div>
    </>
  );
}
