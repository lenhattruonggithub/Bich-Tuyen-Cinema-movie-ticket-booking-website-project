import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';

function Register({ setUser }) {
  const [form, setForm] = useState({
    username: '',
    password: '',
    email: '',
    name: '',
    birthday: '',
    gender: '',
    phoneNumber: '',
    address: '',
    identityCard: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const [verifyCode, setVerifyCode] = useState('');
  const [codeSent, setCodeSent] = useState(false);
  const [sendingCode, setSendingCode] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSendCode = async () => {
    setSendingCode(true);
    if (!form.email) {
      toast.error('Vui lòng nhập email');
      return;
    }
    try {
      await axios.post('/api/send-verify-code', { email: form.email }, { withCredentials: true });
      setCodeSent(true);
      toast.success('Mã xác nhận đã gửi về email!');
    } catch {
      toast.error('Không gửi được mã xác nhận!');
    }
    setSendingCode(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/api/register', { ...form, verifyCode }, { withCredentials: true });
      console.log('Register response:', response);
      if (response.data) {
        setUser(response.data);
        toast.success('Registration successful! Please log in.');
        navigate('/personal');
      } else {
        setError('Registration failed');
        toast.error('Registration failed');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Registration error');
      toast.error(err.response?.data?.message || 'Please verify code sent to your email');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-gray-900 to-gray-800 text-gray-100">
      <div className="bg-white bg-opacity-5 p-8 rounded-xl backdrop-blur-lg shadow-lg w-80 mt-20">
        <h2 className="text-2xl text-center mb-6">Create your account</h2>
        {error && <p className="text-red-500 mb-4">{error}</p>}
        <form onSubmit={handleSubmit} className="flex flex-col">
          <label htmlFor="username" className="text-sm mb-2">Username</label>
          <input
            type="text"
            name="username"
            value={form.username}
            onChange={handleChange}
            placeholder="yourusername"
            required
            className="p-2 mb-4 bg-gray-700 text-gray-100 rounded-md"
          />
          <label htmlFor="password" className="text-sm mb-2">Password</label>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            placeholder="••••••••"
            required
            className="p-2 mb-4 bg-gray-700 text-gray-100 rounded-md"
          />
          <div className="flex items-center mb-4">
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              placeholder="you@example.com"
              required
              className="p-2 bg-white text-gray-900 border border-gray-400 rounded-l-md flex-1 h-10"
              style={{ borderTopRightRadius: 0, borderBottomRightRadius: 0 }}
            />
            <button
              type="button"
              onClick={handleSendCode}
              disabled={sendingCode}
              className="px-3 h-10 bg-yellow-400 hover:bg-yellow-500 text-black font-bold text-xs rounded-r-md border border-l-0 border-gray-400 transition flex items-center justify-center whitespace-nowrap"
              style={{ borderTopLeftRadius: 0, borderBottomLeftRadius: 0 }}
            >
              {sendingCode ? (
                <svg className="animate-spin h-4 w-4 mr-1 text-black" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z"/>
                </svg>
              ) : null}
              Gửi mã
            </button>
          </div>
          {codeSent && (
            <input
              type="text"
              placeholder="Nhập mã xác nhận"
              value={verifyCode}
              onChange={e => setVerifyCode(e.target.value)}
              required
              className="p-2 mb-4 bg-white text-gray-900 rounded-md border border-gray-400"
            />
          )}
          <label htmlFor="name" className="text-sm mb-2">Full Name</label>
          <input
            type="text"
            name="name"
            value={form.name}
            onChange={handleChange}
            required
            className="p-2 mb-4 bg-gray-700 text-gray-100 rounded-md"
          />
          <label htmlFor="birthday" className="text-sm mb-2">Birthday</label>
          <input
            type="date"
            name="birthday"
            value={form.birthday}
            onChange={handleChange}
            className="p-2 mb-4 bg-gray-700 text-gray-100 rounded-md"
          />
          <label htmlFor="gender" className="text-sm mb-2">Gender</label>
          <select
            name="gender"
            value={form.gender}
            onChange={handleChange}
            className="p-2 mb-4 bg-gray-700 text-gray-100 rounded-md"
          >
            <option value="">-- Select Gender --</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
            <option value="OTHER">Other</option>
          </select>
          <label htmlFor="identityCard" className="text-sm mb-2">Identity Card</label>
          <input
            type="text"
            name="identityCard"
            value={form.identityCard}
            onChange={handleChange}
            className="p-2 mb-4 bg-gray-700 text-gray-100 rounded-md"
          />
          <label htmlFor="phoneNumber" className="text-sm mb-2">Phone Number</label>
          <input
            type="text"
            name="phoneNumber"
            value={form.phoneNumber}
            onChange={handleChange}
            required
            className="p-2 mb-4 bg-gray-700 text-gray-100 rounded-md"
          />
          <label htmlFor="address" className="text-sm mb-2">Address</label>
          <input
            type="text"
            name="address"
            value={form.address}
            onChange={handleChange}
            required
            className="p-2 mb-4 bg-gray-700 text-gray-100 rounded-md"
          />
          <button
            type="submit"
            className="p-3 bg-teal-600 text-white font-bold rounded-md hover:bg-teal-700 transition-colors"
          >
            Sign Up
          </button>
        </form>
        <p className="text-center text-sm mt-4">
          Already have an account?{' '}
          <a href="/login" className="text-teal-600 hover:underline">
            Sign in
          </a>
        </p>
      </div>
    </div>
  );
}

export default Register;