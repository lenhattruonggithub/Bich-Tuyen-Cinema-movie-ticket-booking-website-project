import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';

function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [captchaToken, setCaptchaToken] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const recaptchaRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (!window.grecaptcha) {
      const script = document.createElement('script');
      script.src = 'https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit';
      script.async = true;
      script.defer = true;
      document.body.appendChild(script);
    } else {
      renderRecaptcha();
    }
    window.onloadCallback = () => {
      renderRecaptcha();
    };
    function renderRecaptcha() {
      if (window.grecaptcha && recaptchaRef.current) {
        window.grecaptcha.render(recaptchaRef.current, {
          sitekey: '6LdHoGwrAAAAAAUFOtgoRnZhUkgeT6Yh4VafD-CI',
          callback: (token) => setCaptchaToken(token),
        });
      }
    }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email) {
      toast.error('Please enter your email');
      return;
    }
    if (!captchaToken) {
      toast.error('Please verify the Captcha!');
      return;
    }
    setIsLoading(true);
    try {
      const response = await axios.post('/api/forgot-password', {
        email: email,
        captchaToken: captchaToken
      });
      if (response.data.message) {
        toast.success(response.data.message);
        navigate('/login');
      }
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'An error occurred. Please try again.';
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-gray-900 to-gray-800 text-gray-100">
      <div className="bg-white bg-opacity-5 p-8 rounded-xl backdrop-blur-lg shadow-lg w-96">
        <h2 className="text-2xl text-center mb-6">Forgot Password</h2>
        <p className="text-center text-gray-300 mb-6">
          Enter your email to receive your account information
        </p>
        <form onSubmit={handleSubmit} className="flex flex-col">
          <label htmlFor="email" className="text-sm mb-2">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="your@email.com"
            required
            className="p-2 mb-6 bg-gray-700 text-gray-100 rounded-md"
          />
          <div ref={recaptchaRef} className="mb-6"></div>
          <button
            type="submit"
            disabled={isLoading}
            className={`p-3 text-white font-bold rounded-md transition-colors ${
              isLoading
                ? 'bg-gray-600 cursor-not-allowed'
                : 'bg-teal-600 hover:bg-teal-700'
            }`}
          >
            {isLoading ? 'Processing...' : 'Send Information'}
          </button>
        </form>
        <div className="mt-6 text-center">
          <button
            onClick={() => navigate('/login')}
            className="text-teal-600 hover:underline"
          >
            Back to Login
          </button>
        </div>
      </div>
    </div>
  );
}

export default ForgotPassword;