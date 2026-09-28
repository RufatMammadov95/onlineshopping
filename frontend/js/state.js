export const API = localStorage.getItem('shoplyApi') || 'http://localhost:8080';

export const state = {
    token: localStorage.getItem('shoplyToken'),
    user: JSON.parse(localStorage.getItem('shoplyUser') || 'null'),
    products: [],
    categories: []
};

export const isAdmin = () => state.user?.role === 'ADMIN';

export let checkoutData = null;
export const setCheckoutData = (val) => { checkoutData = val; };