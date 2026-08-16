import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Layout() {
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <>
      <nav className="nav">
        <NavLink to="/" className="brand">BOOK<span>MY</span>SHOW</NavLink>
        <NavLink to="/movies">Movies</NavLink>
        <NavLink to="/theaters">Theaters</NavLink>
        {user && <NavLink to="/bookings">My Bookings</NavLink>}
        {user && <NavLink to="/profile">Profile</NavLink>}
        {isAdmin && <NavLink to="/admin">Admin</NavLink>}
        <div className="nav-spacer" />
        {user ? (
          <>
            <span className="meta">{user.name}</span>
            <button className="btn ghost" onClick={() => { logout(); navigate('/'); }}>Logout</button>
          </>
        ) : (
          <>
            <NavLink to="/login">Login</NavLink>
            <NavLink to="/register" className="btn">Register</NavLink>
          </>
        )}
      </nav>
      <div className="wrap">
        <Outlet />
      </div>
    </>
  );
}
