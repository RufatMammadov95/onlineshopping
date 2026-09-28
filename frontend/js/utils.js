export const $ = s => document.querySelector(s);

export const esc = (v = '') => {
    const d = document.createElement('div');
    d.textContent = v;
    return d.innerHTML;
};

export const money = v => new Intl.NumberFormat('az-AZ', { style: 'currency', currency: 'AZN' }).format(v);

export function toast(message, error = false) {
    const el = $('#toast');
    if (!el) return;
    el.textContent = message;
    el.className = `toast show${error ? ' error' : ''}`;
    setTimeout(() => el.className = 'toast', 3400);
}