import React, { useEffect, useState } from "react";

const BookingHistory = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [ticket, setTicket] = useState(null);
  const [ticketLoading, setTicketLoading] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;

  const [filterDate, setFilterDate] = useState("");

  const accountId = JSON.parse(localStorage.getItem("user"))?.accountId;

  useEffect(() => {
    if (!accountId) return;
    fetch(`http://localhost:8080/api/booking/history?accountId=${accountId}`)
        .then(res => res.json())
        .then(data => {
          setHistory((data || []).reverse());
          setLoading(false);
        });
  }, [accountId]);

  const uniqueDates = Array.from(new Set(history.map(item => item.bookingDate?.slice(0, 10))));

  const filtered = filterDate
      ? history.filter(item => item.bookingDate?.slice(0, 10) === filterDate)
      : history;

  const totalPages = Math.ceil(filtered.length / pageSize);
  const pagedHistory = filtered.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  const handleShowTicket = (invoiceId) => {
    const fallbackTicket = history.find(item => item.invoiceId === invoiceId);
    setTicketLoading(true);
    setShowModal(true);

    fetch(`http://localhost:8080/api/booking/ticket/${invoiceId}`)
        .then(res => {
          if (!res.ok) throw new Error("Invalid ticket response");
          return res.json();
        })
        .then(data => {
          if (data && Object.keys(data).length > 0) {
            setTicket(data);
          } else {
            setTicket(fallbackTicket);
          }
        })
        .catch(() => {
          setTicket(fallbackTicket);
        })
        .finally(() => {
          setTicketLoading(false);
        });
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setTicket(null);
  };

  const handleCancelTicket = async (invoiceId, totalMoney) => {
    if (!window.confirm("Are you sure you want to cancel this ticket?")) return;
    try {
      const res = await fetch(`http://localhost:8080/api/booking/cancel/${invoiceId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ totalMoney }),
      });
      if (res.ok) {
        alert("Ticket canceled successfully! Points refunded.");
        setLoading(true);
        fetch(`http://localhost:8080/api/booking/history?accountId=${accountId}`)
            .then(res => res.json())
            .then(data => {
              setHistory((data || []).reverse());
              setLoading(false);
            });
      } else {
        alert("Failed to cancel the ticket!");
      }
    } catch {
      alert("An error occurred while canceling the ticket!");
    }
  };

  if (!accountId) {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white">
          <div className="bg-gray-800 p-8 rounded-lg shadow-2xl">
            <h2 className="text-2xl mb-4">You need to log in to view your booking history!</h2>
          </div>
        </div>
    );
  }

  return (
      <section className="results-sec">
        <div className="container">
          <div className="section-title">
            <h5 className="sub-title">Booking History</h5>
            <h2 className="title">Your Ticket Bookings</h2>
          </div>
          <div className="min-h-screen bg-gray-900 p-8 flex justify-center items-start">
            <div className="w-full max-w-3xl">
              <div className="mb-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                <div>
                  <label className="text-white mr-2">Filter by date:</label>
                  <select
                      className="rounded px-3 py-2 bg-gray-900 text-white border border-gray-700"
                      value={filterDate}
                      onChange={e => {
                        setFilterDate(e.target.value);
                        setCurrentPage(1);
                      }}
                  >
                    <option value="">All</option>
                    {uniqueDates.map(date => (
                        <option key={date} value={date}>{date}</option>
                    ))}
                  </select>
                </div>
                <div className="text-gray-400 text-sm">
                  Page {currentPage} / {totalPages || 1}
                </div>
              </div>
              {loading ? (
                  <div className="text-white text-center">Loading...</div>
              ) : filtered.length === 0 ? (
                  <div className="bg-gray-800 text-white rounded-lg p-8 text-center shadow-xl">
                    <h3 className="text-xl font-medium mb-2">You don't have any booking history.</h3>
                    <p className="text-gray-400">Book your ticket now and enjoy movies at Movflx!</p>
                  </div>
              ) : (
                  <>
                    <div className="space-y-6">
                      {pagedHistory.map(item => (
                          <div
                              key={item.invoiceId}
                              className="bg-gray-800 rounded-lg p-6 shadow-xl flex flex-col md:flex-row md:items-center md:justify-between"
                          >
                            <div>
                              <h3 className="text-lg font-bold text-white mb-2">{item.movieName}</h3>
                              <div className="flex flex-wrap gap-4 text-gray-300 text-sm mb-2">
                          <span>
                            <i className="ri-calendar-2-line text-yellow-400 mr-1"></i>
                            {new Date(item.bookingDate).toLocaleString("en-GB")}
                          </span>
                                <span>
                            <i className="ri-time-line text-yellow-400 mr-1"></i>
                                  {item.scheduleShow}
                          </span>
                                <span>
                            <i className="ri-vip-crown-2-line text-yellow-400 mr-1"></i>
                            Seat: <span className="text-white font-semibold">{item.seat}</span>
                          </span>
                              </div>
                              <span className="text-green-400 font-semibold">
                          {item.totalMoney.toLocaleString("vi-VN")} VND
                        </span>
                            </div>
                            <div className="mt-4 md:mt-0 flex flex-col gap-2 items-end">
                        <span
                            className={`px-4 py-2 rounded-full font-bold text-xs ${
                                item.status
                                    ? "bg-emerald-500 text-white"
                                    : "bg-red-500 text-white"
                            }`}
                        >
                          {item.status ? "Paid" : "Unpaid"}
                        </span>
                              <button
                                  className="mt-2 px-4 py-2 bg-[#e4d804] hover:bg-[#cfc200] text-black rounded-full font-semibold transition"
                                  onClick={() => handleShowTicket(item.invoiceId)}
                              >
                                View Ticket
                              </button>
                              {item.status && (
                                  <button
                                      className="mt-2 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-full font-semibold transition"
                                      onClick={() => handleCancelTicket(item.invoiceId, item.totalMoney)}
                                  >
                                    Cancel Ticket & Refund Points
                                  </button>
                              )}
                            </div>
                          </div>
                      ))}
                    </div>
                    {totalPages > 1 && (
                        <div className="flex justify-center items-center gap-2 mt-8">
                          <button
                              onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                              disabled={currentPage === 1}
                              className="px-3 py-1 rounded bg-indigo-100 text-indigo-700 font-semibold disabled:opacity-50"
                          >
                            Previous
                          </button>
                          <span>
                      Page {currentPage} / {totalPages}
                    </span>
                          <button
                              onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                              disabled={currentPage === totalPages}
                              className="px-3 py-1 rounded bg-indigo-100 text-indigo-700 font-semibold disabled:opacity-50"
                          >
                            Next
                          </button>
                        </div>
                    )}
                  </>
              )}
            </div>
          </div>
        </div>

        {/* ✅ Ticket Modal */}
        {showModal && (
            <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
              <div className="bg-white text-black rounded-lg p-6 w-[90%] max-w-lg relative">
                {ticketLoading ? (
                    <div className="text-center">Loading ticket...</div>
                ) : (
                    <>
                      <h3 className="text-xl font-bold mb-4">Ticket Details</h3>
                      <p><strong>Movie:</strong> {ticket?.movieName}</p>
                      <p><strong>Seat:</strong> {ticket?.seat}</p>
                      <p><strong>Show Time:</strong> {ticket?.scheduleShow}</p>
                      <p><strong>Booking Date:</strong> {new Date(ticket?.bookingDate).toLocaleString()}</p>
                      <p><strong>Total:</strong> {ticket?.totalMoney?.toLocaleString("vi-VN")} VND</p>
                      <p><strong>Status:</strong> {ticket?.status ? "Paid" : "Unpaid"}</p>
                      <button
                          onClick={handleCloseModal}
                          className="mt-4 px-4 py-2 bg-[#e4d804] hover:bg-[#cfc200] text-black rounded-full font-semibold transition"
                      >
                        Close
                      </button>
                    </>
                )}
              </div>
            </div>
        )}
      </section>
  );
};

export default BookingHistory;
