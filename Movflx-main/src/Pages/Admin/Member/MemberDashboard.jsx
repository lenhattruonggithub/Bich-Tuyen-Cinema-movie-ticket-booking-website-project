// MemberDashboard.jsx
import React, { useEffect, useState } from "react";
import { FiSearch } from "react-icons/fi";
import { fetchMembersApi, toggleBanStatusApi } from "./api";
import BanModal from "./BanModal";

const MemberDashboard = () => {
    const [members, setMembers] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(true);
    const [showBanModal, setShowBanModal] = useState(false);
    const [banMessage, setBanMessage] = useState("");
    const [selectedAccountId, setSelectedAccountId] = useState(null);
    const [selectedStatus, setSelectedStatus] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        fetchMembersApi()
            .then((data) => {
                // Combine accounts and bans into enriched records
                const merged = data.accounts.map((acc) => {
                    const ban = data.bans.find((b) => b.accountId === acc.accountId);
                    return {
                        ...acc,
                        banMessage: ban?.message || null
                    };
                });
                setMembers(merged);
            })
            .catch((err) => {
                console.error("Error fetching members:", err);
                setMembers([]);
            })
            .finally(() => setLoading(false));
    }, []);

    const filteredMembers = members.filter((m) =>
        [
            m.name || "",
            m.email || "",
            m.username || "",
            m.phoneNumber || "",
        ].some((field) => field.toLowerCase().includes(searchTerm.toLowerCase()))
    );

    const handleToggleBan = (accountId, currentStatus) => {
        setSelectedAccountId(accountId);
        setSelectedStatus(currentStatus);
        setBanMessage("");
        setError("");
        if (currentStatus) {
            setShowBanModal(true);
        } else {
            confirmToggleBan(accountId, currentStatus, "");
        }
    };

    const confirmToggleBan = async (accountId, currentStatus, message) => {
        setLoading(true);
        try {
            await toggleBanStatusApi(accountId, message);
            // After success, re-fetch all members + bans
            const data = await fetchMembersApi();
            const merged = data.accounts.map((acc) => {
                const ban = data.bans.find((b) => b.accountId === acc.accountId);
                return {
                    ...acc,
                    banMessage: ban?.message || null
                };
            });
            setMembers(merged);
            setShowBanModal(false);
        } catch (error) {
            console.error("Failed to toggle ban status:", error);
            setError(`Failed to toggle ban status: ${error.message}`);
            fetchMembersApi()
                .then((data) => {
                    const merged = data.accounts.map((acc) => {
                        const ban = data.bans.find((b) => b.accountId === acc.accountId);
                        return {
                            ...acc,
                            banMessage: ban?.message || null
                        };
                    });
                    setMembers(merged);
                })
                .catch((err) => {
                    console.error("Error refreshing members:", err);
                    setMembers([]);
                });
        } finally {
            setLoading(false);
        }
    };

    const handleBanSubmit = () => {
        if (!banMessage.trim()) {
            setError("Ban message cannot be empty");
            return;
        }
        if (banMessage.length > 255) {
            setError("Ban message must be 255 characters or less");
            return;
        }
        confirmToggleBan(selectedAccountId, selectedStatus, banMessage);
    };

    return (
        <div className="p-8">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-2xl font-bold">User Management</h1>
            </div>
            <div className="flex gap-4 mb-6">
                <div className="flex-1">
                    <div className="relative">
                        <FiSearch className="absolute left-3 top-3 text-gray-400" />
                        <input
                            type="text"
                            placeholder="Search members..."
                            className="w-full pl-10 pr-4 py-2 border rounded-lg"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>
            </div>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full bg-white rounded shadow">
                        <thead>
                        <tr>
                            <th className="px-4 py-2">ID</th>
                            <th className="px-4 py-2">Name</th>
                            <th className="px-4 py-2">Email</th>
                            <th className="px-4 py-2">Username</th>
                            <th className="px-4 py-2">Phone</th>
                            <th className="px-4 py-2">Role/Status</th>
                            <th className="px-4 py-2">Ban Message</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredMembers.length === 0 ? (
                            <tr>
                                <td colSpan={7} className="text-center py-4 text-gray-500">
                                    No members found.
                                </td>
                            </tr>
                        ) : (
                            filteredMembers.map((m, idx) => (
                                <tr key={m.accountId}>
                                    <td className="border px-4 py-2">{idx + 1}</td>
                                    <td className="border px-4 py-2">{m.name}</td>
                                    <td className="border px-4 py-2">{m.email}</td>
                                    <td className="border px-4 py-2">{m.username}</td>
                                    <td className="border px-4 py-2">{m.phoneNumber}</td>
                                    <td className="border px-4 py-2">
                                        <button
                                            className={`px-3 py-1 rounded text-white w-28 transition-colors duration-200 ${
                                                m.status
                                                    ? "bg-green-500 hover:bg-green-600"
                                                    : "bg-red-500 hover:bg-red-600"
                                            }`}
                                            title={
                                                m.status
                                                    ? "Click to ban this user"
                                                    : "Click to unban this user"
                                            }
                                            onClick={() => handleToggleBan(m.accountId, m.status)}
                                        >
                                            {m.status ? `${m.role} (Active)` : `${m.role} (Banned)`}
                                        </button>
                                    </td>
                                    <td className="border px-4 py-2">{m.banMessage || "-"}</td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            )}
            <BanModal
                show={showBanModal}
                onClose={() => setShowBanModal(false)}
                onSubmit={handleBanSubmit}
                banMessage={banMessage}
                setBanMessage={setBanMessage}
                error={error}
                setError={setError}
            />
        </div>
    );
};

export default MemberDashboard;