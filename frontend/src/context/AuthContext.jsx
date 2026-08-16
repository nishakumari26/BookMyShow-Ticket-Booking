import { createContext, useContext, useMemo, useState } from 'react';
import api, { unwrap } from '../services/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('bms_token'));
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem('bms_user');
    return raw ? JSON.parse(raw) : null;
  });

  const loginWithPayload = (data) => {
    localStorage.setItem('bms_token', data.token);
    const profile = { id: data.userId, name: data.name, email: data.email, role: data.role };
    localStorage.setItem('bms_user', JSON.stringify(profile));
    setToken(data.token);
    setUser(profile);
  };

  const value = useMemo(() => ({
    token,
    user,
    isAdmin: user?.role === 'ADMIN',
    login: async (email, password) => {
      const data = unwrap(await api.post('/auth/login', { email, password }));
      loginWithPayload(data);
      return data;
    },
    register: async (payload) => {
      const data = unwrap(await api.post('/auth/register', payload));
      loginWithPayload(data);
      return data;
    },
    logout: () => {
      localStorage.removeItem('bms_token');
      localStorage.removeItem('bms_user');
      setToken(null);
      setUser(null);
    },
    refreshProfile: async () => {
      const data = unwrap(await api.get('/users/me'));
      localStorage.setItem('bms_user', JSON.stringify(data));
      setUser(data);
      return data;
    },
  }), [token, user]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
