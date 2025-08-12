import React from "react";
import { useLocation, Link } from "react-router-dom";

const BanNotification = () => {
  const { state } = useLocation();
  if (!state) return <div>No ban information found.</div>;
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white">
      <div className="bg-gray-800 p-8 rounded-xl shadow-lg w-96 text-center">
        <h2 className="text-2xl font-bold mb-4">Account Banned</h2>
        <p className="mb-2 text-red-400">{state.message}</p>
        <p className="mb-4 text-gray-400">
          Ban Date: {state.date
            ? new Date(state.date).toLocaleString("vi-VN", {
              timeZone: "Asia/Ho_Chi_Minh",
              hour12: false,
              year: "numeric",
              month: "2-digit",
              day: "2-digit",
              hour: "2-digit",
              minute: "2-digit",
              second: "2-digit"
            })
            : "Unknown"}
        </p>


        <Link to="/login" className="text-teal-400 hover:underline">
          Back to Login
        </Link>
      </div>
    </div>
  );
};

export default BanNotification;