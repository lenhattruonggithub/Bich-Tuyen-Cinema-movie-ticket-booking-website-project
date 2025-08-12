import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import {FiCheckCircle, FiChevronUp, FiChevronDown } from "react-icons/fi";
const POINT_TO_MONEY = 1000; // 1 point = 1000 VND

const ConfirmBooking = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { accountId } = useParams();

  let bookingInfo = location.state;
  if (!bookingInfo) {
    try {
      bookingInfo = JSON.parse(localStorage.getItem("bookingInfo"));
    } catch {
      bookingInfo = null;
    }
  }

  const [loading, setLoading] = useState(false);
  const [points, setPoints] = useState(0);
  const [useScore, setUseScore] = useState(0);
  const [error, setError] = useState("");

  // Fetch points from API
  useEffect(() => {
    if (accountId) {
      fetch(`http://localhost:8080/api/member/points/${accountId}`)
        .then(res => res.json())
        .then(data => setPoints(data.points || 0));
    }
  }, [accountId]);

  // Validate input points
  const handleUseScoreChange = (e) => {
    const value = Number(e.target.value);
    if (value < 0) {
      setUseScore(0);
      setError("Points used must be greater than or equal to 0");
    } else if (value > points) {
      setUseScore(points);
      setError("Cannot use more points than you have");
    } else if (value * POINT_TO_MONEY > bookingInfo.totalMoney) {
      setUseScore(Math.floor(bookingInfo.totalMoney / POINT_TO_MONEY));
      setError("Cannot use more than the total amount to pay");
    } else {
      setUseScore(value);
      setError("");
    }
  };

  // Calculate total after discount
  const totalAfterDiscount = Math.max(
    bookingInfo.totalMoney - useScore * POINT_TO_MONEY,
    0
  );

  const handleConfirm = async () => {
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8080/api/booking/payment-link", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...bookingInfo,
          accountId: accountId,
          useScore: useScore,
          totalMoney: totalAfterDiscount,
        }),
      });
      if (res.ok) {
        const data = await res.json();
        window.location.href = data.paymentUrl;
      } else {
        alert("Cannot create payment link!");
      }
    } catch (err) {
      alert("An error occurred!");
    }
    setLoading(false);
  };
useEffect(() => {
  if (bookingInfo) {
    const updated = { 
      ...bookingInfo, 
      useScore, 
      totalMoney: Math.max(bookingInfo.totalMoney - useScore * POINT_TO_MONEY, 0)
    };
    localStorage.setItem("bookingInfo", JSON.stringify(updated));
  }
}, [useScore]);

  useEffect(() => {
    const user = localStorage.getItem("user");
    if (!user) {
      navigate("/login");
    }
  }, [navigate]);

  if (!bookingInfo || !bookingInfo.movieName) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white">
        <div className="bg-gray-800 p-8 rounded-lg shadow-2xl">
          <h2 className="text-2xl mb-4">No booking information!</h2>
          <button
            className="bg-blue-600 px-6 py-2 rounded text-white"
            onClick={() => navigate(-1)}
          >
            Go back
          </button>
        </div>
      </div>
    );
  }

  return (
    <>
      <style>
      {`
        input[type=number]::-webkit-inner-spin-button,
        input[type=number]::-webkit-outer-spin-button {
          -webkit-appearance: none;
          margin: 0;
        }
        input[type=number] {
          -moz-appearance: textfield;
        }
      `}
    </style>
    <section className="results-sec">
      <div className="container">
        <div className="section-title">
          <h5 className="sub-title">Confirm Booking</h5>
          <h2 className="title">Booking Information</h2>
        </div>
        <div className="min-h-screen bg-gray-900 p-8 flex justify-center items-center">
          <div className="max-w-lg w-full bg-gray-800 p-10 rounded-2xl shadow-2xl border border-gray-700">
            <FiCheckCircle className="mx-auto text-emerald-400 mb-4" size={48} />
            <div className="mb-8 space-y-3">
              <div className="flex justify-between text-gray-300">
                <span>Movie name:</span>
                <span className="font-bold text-white">{bookingInfo.movieName}</span>
              </div>
              <div className="flex justify-between text-gray-300">
                <span>Showtime:</span>
                <span className="font-bold text-white">{bookingInfo.scheduleShow} | {bookingInfo.scheduleShowTime}</span>
              </div>
              <div className="flex justify-between text-gray-300">
                <span>Booking date:</span>
                <span className="font-bold text-white">
                  {bookingInfo.bookingDate
                    ? (() => {
                        const d = new Date(bookingInfo.bookingDate);
                        const dateStr = d.toLocaleDateString('en-US');
                        const timeStr = d.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
                        return `${dateStr} | ${timeStr}`;
                      })()
                    : ""}
                </span>
              </div>
              <div className="flex justify-between text-gray-300">
                <span>Seat:</span>
                <span className="font-bold text-white">{bookingInfo.seat}</span>
              </div>
              <div className="flex justify-between text-gray-300">
                <span>Original total:</span>
                <span className="font-bold text-green-400 text-lg">{bookingInfo.totalMoney?.toLocaleString("en-US")} VND</span>
              </div>
              <div className="flex justify-between text-gray-300 items-center">
                <span>Current points:</span>
                <span className="font-bold text-yellow-400">{points}</span>
              </div>
              <div className="flex justify-between text-gray-300 items-center">
  <span>Use points (1 point = 1,000 VND):</span>
  <div className="flex items-center gap-2">
    <button
      type="button"
      className="p-1 rounded bg-gray-700 hover:bg-gray-600 text-yellow-300"
      onClick={() => handleUseScoreChange({ target: { value: useScore - 1 } })}
      disabled={useScore <= 0}
      tabIndex={-1}
    >
      <FiChevronDown size={18} />
    </button>
    <input
      type="number"
      min={0}
      max={Math.min(points, Math.floor(bookingInfo.totalMoney / POINT_TO_MONEY))}
      value={useScore}
      onChange={handleUseScoreChange}
      className="w-16 px-2 py-1 rounded bg-gray-700 text-yellow-300 font-bold text-center border-none outline-none"
      style={{ MozAppearance: "textfield" }}
    />
    <button
      type="button"
      className="p-1 rounded bg-gray-700 hover:bg-gray-600 text-yellow-300"
      onClick={() => handleUseScoreChange({ target: { value: useScore + 1 } })}
      disabled={useScore >= Math.min(points, Math.floor(bookingInfo.totalMoney / POINT_TO_MONEY))}
      tabIndex={-1}
    >
      <FiChevronUp size={18} />
    </button>
  </div>
</div>
              {error && <div className="text-red-400 text-sm">{error}</div>}
              <div className="flex justify-between text-gray-300">
                <span>Total after discount:</span>
                <span className="font-bold text-green-400 text-lg">{totalAfterDiscount.toLocaleString("en-US")} VND</span>
              </div>
              <div className="flex justify-between text-gray-300">
                    <span>Points earned:</span>
                    <span className="font-bold text-emerald-400">
                     {Math.floor(totalAfterDiscount / 100000) * 10}
                    </span>
              </div>
            </div>
            <button
              onClick={handleConfirm}
              disabled={loading || !!error}
              className="w-full bg-[#e4d804] hover:bg-[#cfc200] text-black font-bold py-3 px-6 rounded-full transition duration-200 text-lg"
            >
              {loading ? "Redirecting to payment..." : "Pay with VNPay"}
            </button>
          </div>
        </div>
      </div>
    </section>
    </>
  );
};

export default ConfirmBooking;