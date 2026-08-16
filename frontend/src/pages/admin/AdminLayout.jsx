import { NavLink, Outlet } from 'react-router-dom';

export default function AdminLayout() {
  return (
    <>
      <h1>Admin</h1>
      <div className="row" style={{ marginBottom: 20 }}>
        <NavLink className="btn secondary" to="/admin">Dashboard</NavLink>
        <NavLink className="btn secondary" to="/admin/movies">Movies</NavLink>
        <NavLink className="btn secondary" to="/admin/theaters">Theaters</NavLink>
        <NavLink className="btn secondary" to="/admin/shows">Shows</NavLink>
        <NavLink className="btn secondary" to="/admin/bookings">Bookings</NavLink>
        <NavLink className="btn secondary" to="/admin/users">Users</NavLink>
      </div>
      <Outlet />
    </>
  );
}
