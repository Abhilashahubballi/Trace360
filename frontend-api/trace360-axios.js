// ══════════════════════════════════════════════════════════
//  src/api/axiosConfig.js
//  Base Axios instance — used by all API calls
// ══════════════════════════════════════════════════════════

import axios from 'axios';

/**
 * axiosInstance — the base configured Axios client
 *
 * Why a custom instance instead of plain axios?
 * → Set base URL once — all calls use it automatically
 * → Add JWT token to every request automatically (interceptor)
 * → Handle 401 (token expired) globally in one place
 * → Set default headers and timeout
 */
const axiosInstance = axios.create({
  // Reads from .env file:
  // REACT_APP_API_URL=http://localhost:8080  (development)
  // REACT_APP_API_URL=https://trace360.onrender.com  (production)
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  timeout: 15000, // 15 seconds — Render free tier can be slow on first wake
  headers: {
    'Content-Type': 'application/json',
  },
});

// ── REQUEST INTERCEPTOR ──
// Runs BEFORE every request is sent
// Automatically attaches JWT token from localStorage
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('trace360_token');
    if (token) {
      // Adds: Authorization: Bearer eyJhbGci...
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// ── RESPONSE INTERCEPTOR ──
// Runs AFTER every response is received
// Handles token expiry globally
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid — clear storage and redirect to login
      localStorage.removeItem('trace360_token');
      localStorage.removeItem('trace360_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;


// ══════════════════════════════════════════════════════════
//  src/api/authApi.js
//  All authentication API calls
// ══════════════════════════════════════════════════════════

import axios from './axiosConfig';

/**
 * authApi — wraps all /api/auth/* endpoints
 *
 * Every function uses try-catch + async/await.
 * On success → returns data
 * On failure → throws error with meaningful message
 */
const authApi = {

  /**
   * Send OTP to email before registration
   * POST /api/auth/send-otp
   */
  sendOtp: async (email) => {
    try {
      const response = await axios.post('/api/auth/send-otp', { email });
      return response.data; // ApiResponse<String>
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Failed to send OTP. Please try again.'
      );
    }
  },

  /**
   * Register new user
   * POST /api/auth/register
   *
   * @param {Object} registerData - { fullName, email, password, phone, role, otp }
   * @returns {Object} - { token, userId, fullName, email, role }
   */
  register: async (registerData) => {
    try {
      const response = await axios.post('/api/auth/register', registerData);
      const { token, ...user } = response.data;

      // Store token and user info in localStorage
      if (token) {
        localStorage.setItem('trace360_token', token);
        localStorage.setItem('trace360_user', JSON.stringify(user));
      }

      return response.data;
    } catch (error) {
      // Extract validation errors if present
      if (error.response?.data?.data) {
        const validationErrors = error.response.data.data;
        const firstError = Object.values(validationErrors)[0];
        throw new Error(firstError);
      }
      throw new Error(
        error.response?.data?.message || 'Registration failed. Please try again.'
      );
    }
  },

  /**
   * Login user
   * POST /api/auth/login
   *
   * @param {string} email
   * @param {string} password
   * @returns {Object} - { token, userId, fullName, email, role }
   */
  login: async (email, password) => {
    try {
      const response = await axios.post('/api/auth/login', { email, password });
      const { token, ...user } = response.data;

      // Persist token and user for future requests
      if (token) {
        localStorage.setItem('trace360_token', token);
        localStorage.setItem('trace360_user', JSON.stringify(user));
      }

      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Invalid email or password.'
      );
    }
  },

  /**
   * Logout — clear local storage
   * No backend call needed (JWT is stateless)
   */
  logout: () => {
    localStorage.removeItem('trace360_token');
    localStorage.removeItem('trace360_user');
    window.location.href = '/login';
  },

  /**
   * Check if user is currently logged in
   */
  isLoggedIn: () => {
    return !!localStorage.getItem('trace360_token');
  },

  /**
   * Get current logged-in user from localStorage
   */
  getCurrentUser: () => {
    const user = localStorage.getItem('trace360_user');
    return user ? JSON.parse(user) : null;
  },

  /**
   * Check if current user has a specific role
   */
  hasRole: (role) => {
    const user = authApi.getCurrentUser();
    return user?.role === role;
  },
};

export default authApi;


// ══════════════════════════════════════════════════════════
//  src/api/packageApi.js
//  All package-related API calls
// ══════════════════════════════════════════════════════════

import axios from './axiosConfig';

const packageApi = {

  /**
   * Track a package — PUBLIC, no token needed
   * GET /api/packages/track/{trackingId}
   *
   * Called every 30 seconds for live updates (polling)
   */
  trackPackage: async (trackingId) => {
    try {
      const response = await axios.get(`/api/packages/track/${trackingId}`);
      return response.data.data; // Returns PackageResponse
    } catch (error) {
      if (error.response?.status === 404) {
        throw new Error(`No package found with tracking ID: ${trackingId}`);
      }
      throw new Error(
        error.response?.data?.message || 'Failed to fetch package details.'
      );
    }
  },

  /**
   * Get latest GPS location — PUBLIC
   * GET /api/packages/track/{trackingId}/location
   *
   * Called every 30 seconds to update the map pin
   */
  getLatestLocation: async (trackingId) => {
    try {
      const response = await axios.get(`/api/packages/track/${trackingId}/location`);
      return response.data.data; // Returns LocationResponse
    } catch (error) {
      // 404 means no location yet — agent hasn't updated yet
      if (error.response?.status === 404) {
        return null; // Return null, don't throw
      }
      throw new Error('Failed to fetch location.');
    }
  },

  /**
   * Admin: Get all packages
   * GET /api/admin/packages
   * Requires ADMIN token
   */
  getAllPackages: async (statusFilter = null) => {
    try {
      const url = statusFilter
        ? `/api/admin/packages?status=${statusFilter}`
        : '/api/admin/packages';
      const response = await axios.get(url);
      return response.data.data; // Returns List<PackageResponse>
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Failed to fetch packages.'
      );
    }
  },

  /**
   * Admin: Create a new package
   * POST /api/admin/packages
   * Requires ADMIN token
   *
   * @param {Object} packageData - { senderName, receiverName, receiverEmail, ... }
   */
  createPackage: async (packageData) => {
    try {
      const response = await axios.post('/api/admin/packages', packageData);
      return response.data.data; // Returns created PackageResponse
    } catch (error) {
      if (error.response?.data?.data) {
        const validationErrors = error.response.data.data;
        const firstError = Object.values(validationErrors)[0];
        throw new Error(firstError);
      }
      throw new Error(
        error.response?.data?.message || 'Failed to create package.'
      );
    }
  },

  /**
   * Admin: Update package status
   * PUT /api/admin/packages/{trackingId}/status?status=DELIVERED
   */
  updateStatus: async (trackingId, newStatus) => {
    try {
      const response = await axios.put(
        `/api/admin/packages/${trackingId}/status?status=${newStatus}`
      );
      return response.data.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Failed to update status.'
      );
    }
  },

  /**
   * Admin: Delete a package
   * DELETE /api/admin/packages/{trackingId}
   */
  deletePackage: async (trackingId) => {
    try {
      await axios.delete(`/api/admin/packages/${trackingId}`);
      return true;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Failed to delete package.'
      );
    }
  },

  /**
   * Admin: Get full GPS route history
   * GET /api/admin/packages/{trackingId}/route
   */
  getRouteHistory: async (trackingId) => {
    try {
      const response = await axios.get(`/api/admin/packages/${trackingId}/route`);
      return response.data.data; // Returns List<LocationResponse>
    } catch (error) {
      throw new Error('Failed to fetch route history.');
    }
  },

  /**
   * Agent: Get my assigned packages
   * GET /api/agent/packages
   * Requires AGENT token
   */
  getMyPackages: async () => {
    try {
      const response = await axios.get('/api/agent/packages');
      return response.data.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Failed to fetch your packages.'
      );
    }
  },
};

export default packageApi;


// ══════════════════════════════════════════════════════════
//  src/api/locationApi.js
//  GPS location API calls
// ══════════════════════════════════════════════════════════

import axios from './axiosConfig';

const locationApi = {

  /**
   * Agent sends GPS location update
   * POST /api/location/update
   * Requires AGENT token
   *
   * Called every 5 minutes from agent's mobile UI
   *
   * @param {string} trackingId
   * @param {number} latitude
   * @param {number} longitude
   * @param {string} status - optional status update
   */
  updateLocation: async (trackingId, latitude, longitude, status = null) => {
    try {
      const payload = { trackingId, latitude, longitude };
      if (status) payload.status = status;

      const response = await axios.post('/api/location/update', payload);
      return response.data.data; // Returns LocationResponse
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Failed to send location update.'
      );
    }
  },

  /**
   * Start auto-sending GPS updates every 5 minutes
   * Uses browser's Geolocation API to get real coordinates
   *
   * @param {string} trackingId
   * @param {string} status
   * @param {Function} onSuccess - callback after each update
   * @param {Function} onError - callback on error
   * @returns {number} intervalId - use to stop with clearInterval()
   */
  startAutoUpdate: (trackingId, status, onSuccess, onError) => {
    const sendUpdate = () => {
      if (!navigator.geolocation) {
        onError('Geolocation not supported by your browser');
        return;
      }

      navigator.geolocation.getCurrentPosition(
        async (position) => {
          try {
            const result = await locationApi.updateLocation(
              trackingId,
              position.coords.latitude,
              position.coords.longitude,
              status
            );
            onSuccess(result);
          } catch (err) {
            onError(err.message);
          }
        },
        (geoError) => {
          onError(`GPS error: ${geoError.message}`);
        },
        { enableHighAccuracy: true, timeout: 10000 }
      );
    };

    // Send immediately on start
    sendUpdate();

    // Then every 5 minutes (300,000 ms)
    const intervalId = setInterval(sendUpdate, 5 * 60 * 1000);
    return intervalId;
  },

  /**
   * Stop auto-updates
   */
  stopAutoUpdate: (intervalId) => {
    if (intervalId) clearInterval(intervalId);
  },
};

export default locationApi;


// ══════════════════════════════════════════════════════════
//  src/api/index.js
//  Single export — import everything from one place
// ══════════════════════════════════════════════════════════

export { default as authApi } from './authApi';
export { default as packageApi } from './packageApi';
export { default as locationApi } from './locationApi';


// ══════════════════════════════════════════════════════════
//  src/hooks/usePackageTracking.js
//  Custom React hook for live package tracking
// ══════════════════════════════════════════════════════════

import { useState, useEffect, useCallback } from 'react';
import { packageApi } from '../api';

/**
 * usePackageTracking — custom hook for customer tracking page
 *
 * Handles:
 * → Initial package fetch
 * → Auto-polling every 30 seconds for live updates
 * → Loading and error states
 *
 * Usage in component:
 * const { packageData, location, loading, error, track } = usePackageTracking();
 */
const usePackageTracking = () => {
  const [packageData, setPackageData] = useState(null);
  const [location, setLocation] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [trackingId, setTrackingId] = useState(null);

  // Fetch package + location data
  const fetchData = useCallback(async (tid) => {
    try {
      // Fetch package details and latest location in parallel
      const [pkg, loc] = await Promise.all([
        packageApi.trackPackage(tid),
        packageApi.getLatestLocation(tid),
      ]);
      setPackageData(pkg);
      setLocation(loc);
      setError(null);
    } catch (err) {
      setError(err.message);
    }
  }, []);

  // Initial track — called when user clicks Track button
  const track = async (tid) => {
    setLoading(true);
    setError(null);
    setPackageData(null);
    setLocation(null);
    try {
      await fetchData(tid);
      setTrackingId(tid);
    } finally {
      setLoading(false);
    }
  };

  // Auto-poll every 30 seconds when tracking is active
  useEffect(() => {
    if (!trackingId) return;

    const interval = setInterval(() => {
      fetchData(trackingId); // silent refresh — no loading spinner
    }, 30000);

    return () => clearInterval(interval); // cleanup on unmount
  }, [trackingId, fetchData]);

  return { packageData, location, loading, error, track };
};

export default usePackageTracking;


// ══════════════════════════════════════════════════════════
//  src/hooks/useAuth.js
//  Custom React hook for authentication state
// ══════════════════════════════════════════════════════════

import { useState } from 'react';
import { authApi } from '../api';

const useAuth = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const login = async (email, password) => {
    setLoading(true);
    setError(null);
    try {
      const result = await authApi.login(email, password);
      return result;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const register = async (data) => {
    setLoading(true);
    setError(null);
    try {
      const result = await authApi.register(data);
      return result;
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const sendOtp = async (email) => {
    setLoading(true);
    setError(null);
    try {
      return await authApi.sendOtp(email);
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    loading,
    error,
    login,
    register,
    sendOtp,
    logout: authApi.logout,
    isLoggedIn: authApi.isLoggedIn(),
    currentUser: authApi.getCurrentUser(),
  };
};

export default useAuth;


// ══════════════════════════════════════════════════════════
//  .env  (React environment variables)
//  Place this in the ROOT of your React project
// ══════════════════════════════════════════════════════════

// .env.development  (local)
// REACT_APP_API_URL=http://localhost:8080

// .env.production  (deployed)
// REACT_APP_API_URL=https://trace360.onrender.com


// ══════════════════════════════════════════════════════════
//  USAGE EXAMPLES IN COMPONENTS
// ══════════════════════════════════════════════════════════

/**
 * Example 1: Login Page
 */
/*
import useAuth from '../hooks/useAuth';

const LoginPage = () => {
  const { login, loading, error } = useAuth();

  const handleSubmit = async () => {
    try {
      const user = await login(email, password);
      if (user.role === 'ADMIN') navigate('/admin');
      else if (user.role === 'AGENT') navigate('/agent');
      else navigate('/');
    } catch (err) {
      // error is already set in useAuth hook
    }
  };
};
*/

/**
 * Example 2: Customer Tracking Page
 */
/*
import usePackageTracking from '../hooks/usePackageTracking';

const TrackingPage = () => {
  const { packageData, location, loading, error, track } = usePackageTracking();

  const handleTrack = () => track(trackingInputValue);

  return (
    <div>
      {loading && <p>Searching...</p>}
      {error && <p>{error}</p>}
      {packageData && (
        <div>
          <p>Status: {packageData.statusLabel}</p>
          <p>ETA: {packageData.eta}</p>
          {location && <Map lat={location.latitude} lng={location.longitude} />}
        </div>
      )}
    </div>
  );
};
*/

/**
 * Example 3: Admin Create Package
 */
/*
import { packageApi } from '../api';

const AdminPage = () => {
  const handleCreate = async () => {
    try {
      const created = await packageApi.createPackage({
        senderName: 'Meera Traders',
        senderCity: 'Mumbai, MH',
        receiverName: 'Abhilasha Hubballi',
        receiverEmail: 'abhilasha@email.com',
        destination: 'Belagavi, KA',
        weightKg: 2.4,
        agentId: 1,
        sendEmailToReceiver: true,
      });
      alert(`Package created: ${created.trackingId}`);
    } catch (err) {
      alert(err.message);
    }
  };
};
*/

/**
 * Example 4: Agent GPS Auto-Update
 */
/*
import { locationApi } from '../api';
import { useState } from 'react';

const AgentPage = () => {
  const [intervalId, setIntervalId] = useState(null);
  const [broadcasting, setBroadcasting] = useState(false);

  const startBroadcasting = () => {
    const id = locationApi.startAutoUpdate(
      'TRK-20240423-001',
      'IN_TRANSIT',
      (result) => console.log('Location sent:', result),
      (error) => console.error('Error:', error)
    );
    setIntervalId(id);
    setBroadcasting(true);
  };

  const stopBroadcasting = () => {
    locationApi.stopAutoUpdate(intervalId);
    setBroadcasting(false);
  };

  return (
    <button onClick={broadcasting ? stopBroadcasting : startBroadcasting}>
      {broadcasting ? 'Stop Broadcasting' : 'Start Broadcasting'}
    </button>
  );
};
*/
