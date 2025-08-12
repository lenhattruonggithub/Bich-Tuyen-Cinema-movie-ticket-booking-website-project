import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { FaUserCircle, FaEdit, FaHistory, FaKey, FaStar } from 'react-icons/fa';
import { toast } from "react-toastify";

function Personal({ user, setUser }) {
  const navigate = useNavigate();
  const [points, setPoints] = useState(0);
  const [editMode, setEditMode] = useState(false);
  const [form, setForm] = useState({ ...user });
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (user) {
      axios.get(`http://localhost:8080/api/member/points/${user.accountId}`)
        .then(response => {
          setPoints(response.data.points);
        })
        .catch(error => {
          console.error('Error fetching points:', error);
        });
    }
  }, [user]);

  const handleEditChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };
  const handleProfileSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const res = await fetch("http://localhost:8080/api/profile", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(form),
      });

      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(errorText || "Update failed");
      }

      let updated;
      try {
        updated = await res.json();
      } catch (jsonErr) {
        toast.error("Server returned invalid JSON")
      }
      setUser(updated);
      setEditMode(false);
    } catch (err) {
      toast.error(`Failed: ${err.message}`);
    } finally {
      toast.success("Profile updated successfully!");
      setSaving(false);
    }
  };

  const getInitial = (name) => {
    if (!name) return '?';
    return name.trim().charAt(0).toUpperCase();
  };

  return (
    <div
        className="relative min-h-screen flex items-center justify-center text-gray-100 overflow-hidden bg-cover bg-center"
        style={{ backgroundImage: 'url("/images/anh bia personal.jpg")' }}
      >
      {/* Harry Potter characters on the left */}
      <img
        src="/characters/Harry-Potter-Character-PNG-Image.png"
        alt="Harry Potter Characters"
        className="hidden md:block absolute left-0 bottom-0 h-[320px] opacity-90 pointer-events-none select-none"
        style={{ zIndex: 1 }}
      />
      {/* Iron Man on the right with flying animation */}
      <img
        src="/characters/iron_man_punching_png_image.png"
        alt="Iron Man"
        className="hidden md:block absolute right-0 bottom-0 h-[220px] opacity-90 pointer-events-none select-none animate-ironman-fly"
        style={{ zIndex: 1 }}
      />
      {/* Main content */}
      <div className="relative z-10 w-full flex flex-col items-center">
        {/* Avatar and welcome message */}
        <div className="flex flex-col items-center w-full pt-8 pb-4">
          <div className="bg-gradient-to-br from-[#e4d804] to-[#f7f7a1] rounded-full w-20 h-20 flex items-center justify-center shadow-lg mb-2">
            {user.avatarUrl ? (
              <img src={user.avatarUrl} alt="avatar" className="w-20 h-20 rounded-full object-cover" />
            ) : (
              <span className="text-3xl text-gray-800 font-bold">{getInitial(user.name || user.username)}</span>
            )}
          </div>
          <h1 className="text-xl font-bold text-[#e4d804] mt-2 mb-1 drop-shadow">Welcome back, {user.name || user.username}!</h1>
          <p className="text-gray-300 text-sm mb-2">Wishing you a day full of energy and joy!</p>
        </div>
        {/* Personal info card - centered and compact */}
        <div className="bg-black bg-opacity-20 rounded-2xl shadow-inner w-full max-w-lg px-6 py-6 mb-4 flex flex-col gap-2 mx-auto">
        {editMode ? (
            <form onSubmit={handleProfileSave} className="flex flex-col gap-3">
              <input
                className="p-2 rounded bg-gray-800 text-gray-400 cursor-not-allowed"
                name="username"
                value={user.username}
                placeholder="Username"
                readOnly
                disabled
              />
              <input
                className="p-2 rounded bg-gray-800 text-gray-400 cursor-not-allowed"
                name="email"
                value={user.email}
                placeholder="Email"
                readOnly
                disabled
              />
              <input
                className="p-2 rounded bg-gray-800 text-gray-400 cursor-not-allowed"
                name="phoneNumber"
                value={user.phoneNumber}
                placeholder="Phone number"
                readOnly
                disabled
              />
              <input
                className="p-2 rounded bg-gray-700 focus:outline-none"
                name="name"
                value={form.name}
                onChange={handleEditChange}
                placeholder="Full name"
              />
              <input
                className="p-2 rounded bg-gray-700 focus:outline-none"
                name="address"
                value={form.address}
                onChange={handleEditChange}
                placeholder="Address"
              />
              <input
                className="p-2 rounded bg-gray-700 focus:outline-none"
                name="birthday"
                type="date"
                value={form.birthday}
                onChange={handleEditChange}
              />
              <select
                className="p-2 rounded bg-gray-700 focus:outline-none"
                name="gender"
                value={form.gender}
                onChange={handleEditChange}
              >
                <option value="">-- Select gender --</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
              <div className="flex gap-2 mt-2">
                <button
                  type="submit"
                  disabled={saving}
                  className="flex-1 px-4 py-2 bg-[#e4d804] hover:bg-[#cfc200] text-black rounded-full font-semibold transition disabled:opacity-50 flex items-center justify-center gap-2"
                >
                  {saving ? 'Saving...' : (<><FaEdit /> Save</>)}
                </button>
                <button
                  type="button"
                  onClick={() => setEditMode(false)}
                  className="flex-1 px-4 py-2 bg-red-600 hover:bg-red-500 text-white rounded-full font-semibold transition flex items-center justify-center gap-2"
                >
                  Cancel
                </button>
              </div>
            </form>
          ) : (
            <div className="flex flex-col gap-1">
              <div className="flex items-center justify-between">
                <span className="font-semibold text-gray-200">Full name:</span>
                <span className="text-gray-100">{user.name}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="font-semibold text-gray-200">Email:</span>
                <span className="text-gray-100">{user.email}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="font-semibold text-gray-200">Phone number:</span>
                <span className="text-gray-100">{user.phoneNumber}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="font-semibold text-gray-200">Address:</span>
                <span className="text-gray-100">{user.address}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="font-semibold text-gray-200">Birthday:</span>
                <span className="text-gray-100">{user.birthday}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="font-semibold text-gray-200">Gender:</span>
                <span className="text-gray-100">{user.gender === 'MALE' ? 'Male' : user.gender === 'FEMALE' ? 'Female' : user.gender === 'OTHER' ? 'Other' : ''}</span>
              </div>
              <div className="flex items-center justify-between mt-2">
                <span className="font-semibold text-[#e4d804] flex items-center gap-1"><FaStar className="text-[#e4d804]" /> Points:</span>
                <span className="text-[#e4d804] font-bold">{points}</span>
              </div>
            </div>
          )}
        </div>
        {/* Action buttons */}
        {!editMode && (
          <div className="flex flex-wrap justify-center gap-3 w-full max-w-lg pb-8 mx-auto">
              <button
                onClick={() => navigate('/booking-history')}
              className="flex items-center gap-2 px-4 py-2 bg-[#e4d804] hover:bg-[#cfc200] text-black rounded-full font-semibold transition shadow"
              >
              <FaHistory /> Booking history
              </button>
              <button
                onClick={() => navigate('/point-history')}
              className="flex items-center gap-2 px-4 py-2 bg-[#e4d804] hover:bg-[#cfc200] text-black rounded-full font-semibold transition shadow"
              >
              <FaStar /> Points history
              </button>
              <button
                onClick={() => setEditMode(true)}
              className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white rounded-full font-semibold transition shadow"
              >
              <FaEdit /> Edit
              </button>
              <button
                onClick={() => navigate('/change-password')}
              className="flex items-center gap-2 px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded-full font-semibold transition shadow"
              >
              <FaKey /> Change password
              </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default Personal;

// Thêm vào Movflx-main/src/index.css:
/*
@keyframes ironman-fly {
  0% { transform: translateY(0) scale(1) rotate(8deg); filter: brightness(1); }
  50% { transform: translateY(-30px) scale(1.05) rotate(4deg); filter: brightness(1.15); }
  100% { transform: translateY(0) scale(1) rotate(8deg); filter: brightness(1); }
}
.animate-ironman-fly {
  animation: ironman-fly 2.5s ease-in-out infinite;
}
*/