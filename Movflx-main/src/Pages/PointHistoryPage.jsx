import React, { useEffect, useState } from "react";
import axios from "axios";

const PAGE_SIZE = 5;

const PointHistoryPage = () => {
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filterDate, setFilterDate] = useState("");
    const [currentPage, setCurrentPage] = useState(1);

    const accountId = JSON.parse(localStorage.getItem("user"))?.accountId;

    useEffect(() => {
        if (!accountId) return setLoading(false);
        axios
            .get(`http://localhost:8080/api/member/points/history/${accountId}`)
            .then((res) => {
                setHistory(res.data);
                setLoading(false);
            })
            .catch(() => setLoading(false));
    }, [accountId]);

    const filtered = filterDate
        ? history.filter((item) => item.rewardDate?.slice(0, 10) === filterDate)
        : history;

    const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
    const pagedHistory = filtered.slice(
        (currentPage - 1) * PAGE_SIZE,
        currentPage * PAGE_SIZE
    );

    const uniqueDates = Array.from(new Set(history.map((item) => item.rewardDate?.slice(0, 10))));

    if (loading)
        return (
            <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-gray-900 to-gray-800">
                <div className="text-yellow-400 text-lg animate-pulse">Loading point history...</div>
            </div>
        );

    if (!accountId)
        return (
            <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-gray-900 to-gray-800">
                <div className="text-white text-lg">You need to log in to view point history!</div>
            </div>
        );

    return (
        <section className="min-h-screen bg-gradient-to-br from-gray-900 to-gray-800 flex items-center justify-center py-12">
            <div className="bg-gray-800 rounded-2xl shadow-2xl p-8 w-full max-w-2xl border border-gray-700">
                <div className="mb-8 text-center">
                    <h5 className="text-yellow-400 text-lg font-semibold tracking-widest uppercase mb-2">Point History</h5>
                    <h2 className="text-3xl font-bold text-white mb-2">Earning / Deduction Records</h2>
                    <p className="text-gray-400">Review your reward point transactions</p>
                </div>
                <div className="mb-6 flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                    <div>
                        <label className="text-white mr-2 font-medium">Filter by date:</label>
                        <select
                            className="rounded px-3 py-2 bg-gray-900 text-white border border-gray-700 focus:ring-2 focus:ring-yellow-400"
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
                        Page <span className="text-yellow-400 font-bold">{currentPage}</span> / {totalPages || 1}
                    </div>
                </div>
                <div className="overflow-x-auto rounded-lg shadow-inner">
                    <table className="min-w-full bg-gray-900 rounded-lg shadow text-white">
                        <thead>
                            <tr>
                                <th className="py-3 px-4 text-left">No.</th>
                                <th className="py-3 px-4 text-left">Points</th>
                                <th className="py-3 px-4 text-left">Date</th>
                                <th className="py-3 px-4 text-left">Type</th>
                            </tr>
                        </thead>
                        <tbody>
                            {pagedHistory.length === 0 ? (
                                <tr>
                                    <td colSpan={4} className="text-center py-6 text-gray-400">
                                        No point history available.
                                    </td>
                                </tr>
                            ) : (
                                pagedHistory.map((item, index) => (
                                    <tr
                                        key={item.rewardId}
                                        className="border-b border-gray-700 hover:bg-gray-700 transition"
                                    >
                                        <td className="py-2 px-4">{(currentPage - 1) * PAGE_SIZE + index + 1}</td>
                                        <td
                                            className={`py-2 px-4 font-semibold text-lg ${
                                                item.type === "ADD"
                                                    ? "text-green-400"
                                                    : item.type === "DEDUCT"
                                                    ? "text-red-400"
                                                    : "text-gray-300"
                                            }`}
                                        >
                                            {item.type === "ADD"
                                                ? `+${item.points}`
                                                : item.type === "DEDUCT"
                                                ? `-${item.points}`
                                                : item.points}
                                        </td>
                                        <td className="py-2 px-4">
                                            {new Date(item.rewardDate).toLocaleString("en-GB")}
                                        </td>
                                        <td className="py-2 px-4">
                                            <span
                                                className={`px-3 py-1 rounded-full text-xs font-bold ${
                                                    item.type === "ADD"
                                                        ? "bg-green-500/20 text-green-300 border border-green-400"
                                                        : item.type === "DEDUCT"
                                                        ? "bg-red-500/20 text-red-300 border border-red-400"
                                                        : "bg-gray-700 text-gray-300 border border-gray-500"
                                                }`}
                                            >
                                                {item.type === "ADD"
                                                    ? "Earned"
                                                    : item.type === "DEDUCT"
                                                    ? "Used"
                                                    : "Other"}
                                            </span>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
                {totalPages > 1 && (
                    <div className="flex justify-center items-center gap-2 mt-8">
                        <button
                            onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                            disabled={currentPage === 1}
                            className="px-4 py-2 rounded bg-gray-700 text-white font-semibold hover:bg-yellow-400 hover:text-black transition disabled:opacity-50"
                        >
                            Prev
                        </button>
                        <span className="text-yellow-400 font-bold">
                            Page {currentPage} / {totalPages}
                        </span>
                        <button
                            onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                            disabled={currentPage === totalPages}
                            className="px-4 py-2 rounded bg-gray-700 text-white font-semibold hover:bg-yellow-400 hover:text-black transition disabled:opacity-50"
                        >
                            Next
                        </button>
                    </div>
                )}
            </div>
        </section>
    );
};

export default PointHistoryPage;
