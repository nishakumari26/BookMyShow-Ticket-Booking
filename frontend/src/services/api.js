import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('bms_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export function unwrap(response) {
  return response.data?.data;
}

export function errorMessage(err) {
  return err.response?.data?.message || err.message || 'Request failed';
}

export default api;
