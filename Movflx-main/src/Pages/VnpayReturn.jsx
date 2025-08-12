import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { FiCheckCircle, FiXCircle } from "react-icons/fi";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

const VnpayReturn = () => {
  const query = useQuery();
  const navigate = useNavigate();
  const [status, setStatus] = useState(null);

  useEffect(() => {
    const responseCode = query.get("vnp_ResponseCode");
    const transactionStatus = query.get("vnp_TransactionStatus");
    if (responseCode === "00" && transactionStatus === "00") {
      setStatus("success");
      const bookingInfo = JSON.parse(localStorage.getItem("bookingInfo"));
      const accountId = bookingInfo?.accountId || JSON.parse(localStorage.getItem("user"))?.accountId;
      if (bookingInfo && accountId) {
        fetch(`http://localhost:8080/api/booking/confirm?accountId=${accountId}`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(bookingInfo),
        })
          .then(res => res.json())
          .then(() => {
            if (bookingInfo.useScore > 0) {
              fetch(`http://localhost:8080/api/member/points/deduct/${accountId}?points=${bookingInfo.useScore}`, {
                method: "PUT"
              });
            }
            localStorage.removeItem("bookingInfo");
          });
      }
    } else {
      setStatus("fail");
    }
  }, []);

  return (
    <section className="results-sec">
      <div className="container flex justify-center items-center min-h-screen">
        <div className="bg-gray-800 rounded-lg shadow-2xl p-10 max-w-md w-full text-center">
          {status === "success" ? (
            <>
              <FiCheckCircle className="mx-auto text-emerald-400" size={64} />
              <h2 className="text-2xl font-bold text-white mt-4 mb-2">Payment Successful!</h2>
              <p className="text-gray-300 mb-6">Thank you for booking with Movflx.</p>
              <button
                className="px-6 py-2 bg-[#e4d804] hover:bg-[#cfc200] text-black rounded-full font-semibold transition"
                onClick={() => navigate("/booking-history")}
              >
                View Booking History
              </button>
            </>
          ) : status === "fail" ? (
            <>
              <FiXCircle className="mx-auto text-red-400" size={64} />
              <h2 className="text-2xl font-bold text-white mt-4 mb-2">Payment Failed!</h2>
              <p className="text-gray-300 mb-6">Please try again or contact support.</p>
              <button
                className="px-6 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded-full font-semibold transition"
                onClick={() => navigate("/")}
              >
                Return to Home
              </button>
            </>
          ) : (
            <div className="text-white text-lg">Processing payment result...</div>
          )}
        </div>
      </div>
    </section>
  );
};

export default VnpayReturn;
