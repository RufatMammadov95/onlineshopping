import { $, toast } from './utils.js';
import { state } from './state.js';
import { loadCatalog, request, setUser } from './api.js';
import {
    nav,
    renderHome,
    renderProductDetail,
    renderAdmin,
    renderCart,
    renderCheckout,
    renderPayment,
    renderOrders
} from './views.js';

async function route() {
    if (!$('#app')) return;
    try {
        if (!state.products.length) await loadCatalog();
        nav();

        const view = location.hash.split('?')[0].replace('#', '') || 'home';

        if (view === 'admin') renderAdmin();
        else if (view === 'product') renderProductDetail();
        else if (view === 'checkout' && state.token) renderCheckout();
        else if (view === 'payment' && state.token) renderPayment();
        else if (view === 'cart' && state.token) renderCart();
        else if (view === 'orders' && state.token) renderOrders();
        else renderHome();
    } catch (x) {
        $('#app').innerHTML = `<section class="page"><div class="empty">${x.message}</div></section>`;
    }
}

function authPages() {
    const login = $('#login-form'),
        register = $('#register-form');

    if (login) {
        login.onsubmit = async e => {
            e.preventDefault();
            try {
                const d = Object.fromEntries(new FormData(login)),
                    r = await request('/auth/login', {
                        method: 'POST',
                        body: JSON.stringify(d)
                    });
                setUser(r.token, d.username);
                location.href = 'index.html';
            } catch (x) {
                toast(x.message, true);
            }
        };
    }

    if (register) {
        register.onsubmit = async e => {
            e.preventDefault();
            try {
                await request('/auth/register', {
                    method: 'POST',
                    body: JSON.stringify(Object.fromEntries(new FormData(register)))
                });
                toast('Qeydiyyat tamamlandı.');
                setTimeout(() => location.href = 'login.html', 700);
            } catch (x) {
                toast(x.message, true);
            }
        };
    }
}

authPages();
window.addEventListener('hashchange', route);
route();
