import { API, state } from './state.js';
import { nav } from './views.js';

export async function request(path, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };
    if (state.token) headers.Authorization = `Bearer ${state.token}`;

    const response = await fetch(`${API}${path}`, { ...options, headers });
    const raw = await response.text();
    let body;
    try {
        body = raw ? JSON.parse(raw) : null;
    } catch {
        body = raw;
    }
    if (!response.ok) throw new Error(typeof body === 'string' ? body : body?.message || 'Sorğu icra olunmadı.');

    if ((path === '/cart' || path.startsWith('/cart/')) && options.method && options.method !== 'GET') {
        setTimeout(() => nav(), 0);
    }

    return body;
}

export function decodeToken(token) {
    try {
        return JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    } catch {
        return {};
    }
}

export function setUser(token, username) {
    const c = decodeToken(token);
    state.token = token;
    state.user = { username, role: c.role || 'CUSTOMER' };
    localStorage.setItem('shoplyToken', token);
    localStorage.setItem('shoplyUser', JSON.stringify(state.user));
}

export function logout() {
    localStorage.removeItem('shoplyToken');
    localStorage.removeItem('shoplyUser');
    location.href = 'index.html';
}

export async function loadCatalog() {
    [state.products, state.categories] = await Promise.all([
        request('/products'),
        request('/categories')
    ]);
}