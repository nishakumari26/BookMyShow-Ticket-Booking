import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import api, { unwrap, errorMessage } from '../services/api';

const FALLBACK = 'https://placehold.co/300x450/1c1f28/ff2e63?text=Movie';

export default function MovieDetails() {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [shows, setShows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      try {
        setMovie(unwrap(await api.get(`/movies/${id}`)));
        const page = unwrap(await api.get('/shows', { params: { movieId: id, size: 50 } }));
        setShows(page.content || []);
      } catch (err) {
        setError(errorMessage(err));
      }
    })();
  }, [id]);

  if (error) return <div className="alert">{error}</div>;
  if (!movie) return <p className="meta">Loading...</p>;

  return (
    <div className="row" style={{ alignItems: 'flex-start' }}>
      <img src={movie.posterUrl || FALLBACK} alt="" style={{ width: 240, borderRadius: 12 }} onError={(e) => { e.target.src = FALLBACK; }} />
      <div style={{ flex: 1 }}>
        <h1>{movie.title}</h1>
        <p className="meta">{movie.language} · {movie.genre} · {movie.duration} min · ★ {movie.rating}</p>
        <p>{movie.description}</p>
        <h3>Shows</h3>
        <div className="show-list">
          {shows.length === 0 && <p className="meta">No shows scheduled.</p>}
          {shows.map((s) => (
            <div className="show-item" key={s.id}>
              <div>
                <b>{s.theaterName}</b> · {s.screenName}
                <div className="meta">{s.showDate} · {s.startTime?.slice(0, 5)}</div>
              </div>
              <Link className="btn" to={`/shows/${s.id}/seats`}>Select seats</Link>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
