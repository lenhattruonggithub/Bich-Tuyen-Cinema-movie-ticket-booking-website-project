import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';

function Login({ setUser }) {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: "include",
        body: JSON.stringify(form),
      });
      const data = await res.json();
      if (res.status === 403 && data.banned) {
        // Redirect to ban notification page with message and date
        navigate('/ban-notification', {
          state: {
            message: data.banMessage,
            date: data.banDate,
          },
        });
        return;
      }
      if (!res.ok) {
        setError('Invalid username or password');
        return;
      }
      setUser(data);
      navigate('/');
    } catch (err) {
      setError('Login failed');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-gray-900 to-gray-800 text-gray-100">
      <div className="bg-white bg-opacity-5 p-8 rounded-xl backdrop-blur-lg shadow-lg w-80">
        <h2 className="text-2xl text-center mb-6">Login to your account</h2>
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
          <button
            type="submit"
            className="p-3 bg-teal-600 text-white font-bold rounded-md hover:bg-teal-700 transition-colors"
          >
            Sign In
          </button>
        </form>
        <div className="mt-4 text-center">
          <button
            onClick={() => navigate('/forgot-password')}
            className="text-teal-600 hover:underline text-sm"
          >
            Forget password?
          </button>
        </div>
        <p className="text-center text-sm mt-4">
          Don't have an account?{' '}
          <a href="/register" className="text-teal-600 hover:underline">
            Sign up
          </a>
        </p>
      </div>
    </div>
  );
}

export default Login;