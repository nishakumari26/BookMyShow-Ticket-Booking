export default function SeatMap({ seats, selected, onToggle }) {
  const rows = {};
  seats.forEach((s) => {
    rows[s.rowNumber] = rows[s.rowNumber] || [];
    rows[s.rowNumber].push(s);
  });

  return (
    <>
      <div className="screen-label">SCREEN</div>
      {Object.keys(rows).sort().map((row) => (
        <div className="seat-row" key={row}>
          <span className="label">{row}</span>
          {rows[row].map((seat) => {
            const booked = seat.status === 'BOOKED';
            const isSelected = selected.includes(seat.showSeatId);
            const type = (seat.seatType || 'REGULAR').toLowerCase();
            return (
              <button
                key={seat.showSeatId}
                className={`seat ${type} ${booked ? 'booked' : ''} ${isSelected ? 'selected' : ''}`}
                disabled={booked}
                title={`${seat.seatNumber} ₹${seat.price}`}
                onClick={() => onToggle(seat)}
              >
                {seat.seatNumber.replace(row, '')}
              </button>
            );
          })}
        </div>
      ))}
      <div className="legend">
        <span><i className="dot" style={{ background: '#2d6a4f' }} /> Regular</span>
        <span><i className="dot" style={{ background: '#4361ee' }} /> Premium</span>
        <span><i className="dot" style={{ background: '#9b2226' }} /> VIP</span>
        <span><i className="dot" style={{ background: '#4a4e5a' }} /> Booked</span>
        <span><i className="dot" style={{ background: '#ffc857' }} /> Selected</span>
      </div>
    </>
  );
}
