import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api, { unwrap, errorMessage } from '../services/api';

export default function Theaters() {
  const [theaters, setTheaters] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/theaters', { params: { size: 50 } })
      .then((r) => setTheaters(unwrap(r).content || []))
      .catch((e) => setError(errorMessage(e)));
  }, []);

  return (
    <>
      <h1>Theaters</h1>
      {error && <div className="alert">{error}</div>}
      <div className="show-list">
        {theaters.map((t) => (
          <div className="show-item" key={t.id}>
            <div>
              <b>{t.name}</b>
              <div className="meta">{t.city} · {t.location} · {t.address}</div>
            </div>
            <Link className="btn secondary" to={`/movies`}>Find shows</Link>
          </div>
        ))}
      </div>
    </>
  );
}
