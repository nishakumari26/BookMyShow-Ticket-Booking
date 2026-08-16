import { Link } from 'react-router-dom';

const FALLBACK = 'https://placehold.co/300x450/1c1f28/ff2e63?text=Movie';

export default function MovieCard({ movie }) {
  return (
    <Link to={`/movies/${movie.id}`} className="card">
      <img src={movie.posterUrl || FALLBACK} alt={movie.title} onError={(e) => { e.target.src = FALLBACK; }} />
      <div className="body">
        <h3>{movie.title}</h3>
        <div className="meta">{movie.language} · {movie.genre}</div>
        <div className="meta">★ {movie.rating ?? '—'} · {movie.duration ?? '—'} min</div>
      </div>
    </Link>
  );
}
