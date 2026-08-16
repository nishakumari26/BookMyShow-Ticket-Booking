import { Link, useLocation } from 'react-router-dom';

export default function Confirmation() {
  const booking = useLocation().state?.booking;
  if (!booking) {
    return <p>No booking to show. <Link to="/bookings">View history</Link></p>;
  }
  return (
    <div className="panel">
      <div className="ok">Booking successful</div>
      <h1>{booking.movieTitle}</h1>
      <p>Reference: <b>{booking.bookingReference}</b></p>
      <p>{booking.theaterName} · {booking.screenName}</p>
      <p>{booking.showDate} · {booking.startTime?.slice(0, 5)}</p>
      <p>Seats: {booking.selectedSeats?.join(', ')}</p>
      <p>Amount: ₹{booking.totalAmount}</p>
      <p>Status: {booking.bookingStatus} · Payment: {booking.paymentStatus}</p>
      <Link className="btn" to="/bookings">My bookings</Link>
    </div>
  );
}
