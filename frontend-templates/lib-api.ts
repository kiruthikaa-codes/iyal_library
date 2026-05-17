// src/lib/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;

// API functions
export const authAPI = {
  register: (data: { name: string; email: string; password: string }) =>
    api.post('/auth/register', data),
  login: (data: { email: string; password: string }) =>
    api.post('/auth/login', data),
};

export const booksAPI = {
  getAll: () => api.get('/books'),
  getById: (id: number) => api.get(`/books/${id}`),
  search: (params: any) => api.get('/books/search', { params }),
  getGenres: () => api.get('/books/genres'),
  getRecent: () => api.get('/books/recent'),
  create: (formData: FormData) => api.post('/books', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  update: (id: number, formData: FormData) => api.put(`/books/${id}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  delete: (id: number) => api.delete(`/books/${id}`),
};

export const borrowingAPI = {
  borrow: (bookId: number) => api.post(`/books/${bookId}/borrow`),
  return: (bookId: number) => api.post(`/books/${bookId}/return`),
  getMyBorrowings: () => api.get('/books/borrowings/my'),
};

export const readingStatusAPI = {
  set: (bookId: number, status: string) => 
    api.post(`/books/${bookId}/status`, { status }),
  getMy: () => api.get('/books/reading-status/my'),
  getMyForBook: (bookId: number) => api.get(`/books/${bookId}/status/my`),
  getCurrentlyReading: () => api.get('/books/reading-status/currently-reading'),
  getByStatus: (status: string) => api.get('/books/reading-status/filter', { params: { status } }),
};

export const userAPI = {
  getMe: () => api.get('/users/me'),
  getProfile: (id: number) => api.get(`/users/${id}/profile`),
  updateVisibility: (isPublic: boolean) => 
    api.patch('/users/profile-visibility', { isPublic }),
};

export const dashboardAPI = {
  get: () => api.get('/dashboard'),
};

export const adminAPI = {
  getUsers: () => api.get('/admin/users'),
  deleteUser: (id: number) => api.delete(`/admin/users/${id}`),
  getBorrowings: () => api.get('/admin/borrowings'),
  getAnalytics: () => api.get('/admin/analytics'),
};
