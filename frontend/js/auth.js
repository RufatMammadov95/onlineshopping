import { $, toast } from './utils.js';
import { request, setUser } from './api.js';

const login = $('#login-form');
const register = $('#register-form');

if (login) {
    login.onsubmit = async event => {
        event.preventDefault();
        try {
            const data = Object.fromEntries(new FormData(login));
            const response = await request('/auth/login', { method: 'POST', body: JSON.stringify(data) });
            setUser(response.token, data.username);
            location.href = 'index.html';
        } catch (error) { toast(error.message, true); }
    };
}

if (register) {
    register.onsubmit = async event => {
        event.preventDefault();
        try {
            await request('/auth/register', { method: 'POST', body: JSON.stringify(Object.fromEntries(new FormData(register))) });
            toast('Qeydiyyat tamamlandı. Daxil olun.');
            setTimeout(() => location.href = 'login.html', 700);
        } catch (error) { toast(error.message, true); }
    };
}
