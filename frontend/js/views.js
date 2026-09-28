import { $, esc, money, toast } from './utils.js';
import { state, isAdmin, checkoutData, setCheckoutData } from './state.js';
import { request, loadCatalog, logout } from './api.js';
import { productCard, categoryOptions } from './components.js';

export async function nav() {
    $('#nav-links').innerHTML = `<a href="#products">Məhsullar</a>${state.token ? '<a href="#cart">Səbət</a><a href="#orders">Sifarişlər</a>' : ''}${isAdmin() ? '<a href="#admin">İdarəetmə</a>' : ''}`;
    $('#nav-auth').innerHTML = state.token
        ? `<span class="tag">${esc(state.user.username)} · ${state.user.role}</span><button id="logout" class="btn btn-plain">Çıxış</button>`
        : '<a href="login.html">Daxil ol</a><a class="btn btn-primary" href="register.html">Qeydiyyat</a>';

    $('#logout')?.addEventListener('click', logout);

    if (state.token && !isAdmin()) {
        try {
            const items = await request('/cart');
            const link = Array.from(document.querySelectorAll('#nav-links a')).find(a => a.getAttribute('href') === '#cart');
            if (link) {
                link.className = 'cart-link';
                link.innerHTML = `Səbət <span class="cart-badge">${items.reduce((sum, item) => sum + Number(item.quantity || 0), 0)}</span>`;
            }
        } catch (_) { }
    }
}

export function renderHome() {
    $('#app').innerHTML = `<section class="hero"><div class="hero-inner"><p class="eyebrow">Gündəlik seçilmiş məhsullar</p><h1>Sevdiyiniz şeylər, bir yerdə.</h1><p>Shoply ilə məhsulları kəşf edin, səbətə əlavə edin və sifarişinizi tamamlayın.</p><a class="btn btn-accent" href="#products">Məhsullara baxın</a></div></section><section id="products" class="page"><div class="section-head"><h2>Məhsullar</h2><input id="search" class="search" placeholder="Məhsul axtarın..." /></div><div id="product-grid" class="grid"></div></section>`;
    renderProducts();
    $('#search').oninput = e => renderProducts(e.target.value);
}

export function renderProducts(query = '') {
    const items = state.products.filter(p => p.name.toLowerCase().includes(query.toLowerCase()));
    $('#product-grid').innerHTML = items.length
        ? items.map(productCard).join('')
        : '<div class="empty">Axtarışınıza uyğun məhsul tapılmadı.</div>';
    bindProductActions();
}

function bindProductActions() {
    document.querySelectorAll('.product-link').forEach(card => {
        const open = e => {
            if (e.target.closest('button,a')) return;
            location.hash = `product?id=${card.dataset.id}`;
        };
        card.onclick = open;
        card.onkeydown = e => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                open(e);
            }
        };
    });

    document.querySelectorAll('.add-cart').forEach(b => b.onclick = async e => {
        e.stopPropagation();
        const quantity = Number(prompt('Miqdarı yazın:', '1'));
        if (!Number.isInteger(quantity) || quantity < 1) return;
        try {
            await request('/cart', {
                method: 'POST',
                body: JSON.stringify({ productId: Number(b.dataset.id), quantity })
            });
            toast('Məhsul səbətə əlavə edildi.');
        } catch (x) {
            toast(x.message, true);
        }
    });

    document.querySelectorAll('.edit-product').forEach(b => b.onclick = e => {
        e.stopPropagation();
        location.hash = `admin?edit=${b.dataset.id}`;
    });
}

export function renderProductDetail() {
    const id = new URLSearchParams(location.hash.split('?')[1] || '').get('id');
    const p = state.products.find(x => x.id == id);
    if (!p) {
        $('#app').innerHTML = '<section class="page"><div class="empty">Məhsul tapılmadı.</div></section>';
        return;
    }
    $('#app').innerHTML = `<section class="page product-detail"><a class="btn btn-plain" href="#products">← Məhsullara qayıt</a><div class="detail-card"><div class="detail-image ${p.imageUrl ? '' : 'empty'}">${p.imageUrl ? `<img src="${esc(p.imageUrl)}" alt="${esc(p.name)}">` : '◈'}</div><div class="detail-content"><span class="tag">Kateqoriya: ${esc(p.categoryName || 'Kateqoriyasız')}</span><h1>${esc(p.name)}</h1><p class="muted">${esc(p.description || 'Məhsul təsviri əlavə edilməyib.')}</p><div class="price">${money(p.price)}</div>${isAdmin() ? `<p>Stok: ${p.stockQuantity}</p>` : ''}${!isAdmin() && !p.available ? '<strong class="stock-out">Stok bitib</strong>' : ''}${state.token && p.available ? `<button id="detail-add" class="btn btn-primary">Səbətə əlavə et</button>` : !state.token ? '<a class="btn btn-primary" href="login.html">Daxil ol</a>' : ''}</div></div></section>`;

    $('#detail-add')?.addEventListener('click', async () => {
        try {
            await request('/cart', {
                method: 'POST',
                body: JSON.stringify({ productId: p.id, quantity: 1 })
            });
            toast('Məhsul səbətə əlavə edildi.');
        } catch (x) {
            toast(x.message, true);
        }
    });
}

export function renderAdmin() {
    if (!isAdmin()) return location.hash = 'products';
    const editId = new URLSearchParams(location.hash.split('?')[1] || '').get('edit');
    const p = state.products.find(x => x.id == editId);

    $('#app').innerHTML = `<section class="page"><div class="section-head"><h2>Məhsul və kateqoriya idarəetməsi</h2></div><div class="dashboard-grid"><div class="panel"><h2>${p ? 'Məhsulu yenilə' : 'Yeni məhsul'}</h2><form id="product-form" class="form-grid"><input type="hidden" name="id" value="${p?.id || ''}"><input type="hidden" name="imageUrl" value="${esc(p?.imageUrl || '')}"><label>Ad<input name="name" required value="${esc(p?.name || '')}"></label><label>Kateqoriya<select name="categoryIds" multiple size="${Math.min(5, Math.max(2, state.categories.length))}" required>${categoryOptions(p?.categoryIds || [p?.categoryId])}</select></label><label>Qiymət<input type="number" min="0" step="0.01" name="price" required value="${p?.price || ''}"></label><label>Stok<input type="number" min="0" name="stockQuantity" required value="${p?.stockQuantity || ''}"></label><label class="full">Şəkil faylı<input type="file" name="imageFile" accept="image/*"></label><label class="full">Təsvir<textarea name="description">${esc(p?.description || '')}</textarea></label><div class="full"><button class="btn btn-primary">${p ? 'Dəyişiklikləri saxla' : 'Məhsul əlavə et'}</button></div></form></div><div class="panel"><h2>Kateqoriyalar</h2><form id="category-form" class="form-stack"><label>Ad<input name="name" required></label><label>Təsvir<textarea name="description"></textarea></label><button class="btn btn-accent">Kateqoriya əlavə et</button></form>${state.categories.map(c => `<div class="list-row"><strong>${esc(c.name)}</strong><button class="btn btn-danger delete-category" data-id="${c.id}">Sil</button></div>`).join('')}</div></div><div class="panel"><h2>Məhsullar</h2><div class="table-wrap"><table><thead><tr><th>Ad</th><th>Kateqoriya</th><th>Qiymət</th><th>Stok</th><th></th></tr></thead><tbody>${state.products.map(x => `<tr><td>${esc(x.name)}</td><td>${esc(x.categoryName || '—')}</td><td>${money(x.price)}</td><td>${x.stockQuantity}</td><td><a class="btn btn-plain" href="#admin?edit=${x.id}">Redaktə</a><button class="btn btn-danger delete-product" data-id="${x.id}">Sil</button></td></tr>`).join('')}</tbody></table></div></div></section>`;

    $('#product-form').onsubmit = saveProduct;
    $('#category-form').onsubmit = saveCategory;
    document.querySelectorAll('.delete-category').forEach(b => b.onclick = () => deleteCategory(b.dataset.id));
    document.querySelectorAll('.delete-product').forEach(b => b.onclick = () => deleteProduct(b.dataset.id));
}

async function saveProduct(e) {
    e.preventDefault();
    const form = e.target,
        d = Object.fromEntries(new FormData(form)),
        id = d.id,
        categoryIds = Array.from(form.elements.categoryIds.selectedOptions).map(o => Number(o.value)),
        file = form.elements.imageFile.files[0];

    delete d.id;
    delete d.imageFile;
    delete d.categoryIds;
    d.categoryIds = categoryIds;
    d.categoryId = categoryIds[0];
    d.price = Number(d.price);
    d.stockQuantity = Number(d.stockQuantity);

    try {
        if (file) {
            d.imageUrl = await new Promise((resolve, reject) => {
                const r = new FileReader();
                r.onload = () => resolve(r.result);
                r.onerror = reject;
                r.readAsDataURL(file);
            });
        }
        await request(id ? `/products/${id}` : '/products', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(d)
        });
        await loadCatalog();
        toast(id ? 'Məhsul yeniləndi.' : 'Məhsul əlavə edildi.');
        location.hash = 'admin';
        renderAdmin();
    } catch (x) {
        toast(x.message, true);
    }
}

async function saveCategory(e) {
    e.preventDefault();
    try {
        await request('/categories', {
            method: 'POST',
            body: JSON.stringify(Object.fromEntries(new FormData(e.target)))
        });
        await loadCatalog();
        renderAdmin();
        toast('Kateqoriya əlavə edildi.');
    } catch (x) {
        toast(x.message, true);
    }
}

async function deleteProduct(id) {
    if (!confirm('Məhsul silinsin?')) return;
    try {
        await request(`/products/${id}`, { method: 'DELETE' });
        await loadCatalog();
        renderAdmin();
        toast('Məhsul silindi.');
    } catch (x) {
        toast(x.message, true);
    }
}

async function deleteCategory(id) {
    if (!confirm('Kateqoriya silinsin?')) return;
    try {
        await request(`/categories/${id}`, { method: 'DELETE' });
        await loadCatalog();
        renderAdmin();
        toast('Kateqoriya silindi.');
    } catch (x) {
        toast(x.message, true);
    }
}

export async function renderCart() {
    $('#app').innerHTML = '<section class="page"><h2>Səbətim</h2><div id="cart-content" class="panel">Yüklənir...</div></section>';
    try {
        const items = await request('/cart');
        const total = items.reduce((sum, i) => sum + Number(i.productPrice) * i.quantity, 0);

        $('#cart-content').innerHTML = items.length
            ? `<div class="table-wrap"><table><thead><tr><th>Məhsul</th><th>Vahid qiymət</th><th>Miqdar</th><th>Cəm</th><th></th></tr></thead><tbody>${items.map(i => `<tr><td>${esc(i.productName)}</td><td>${money(i.productPrice)}</td><td>${i.quantity}</td><td>${money(Number(i.productPrice) * i.quantity)}</td><td><button class="btn btn-danger remove-item" data-id="${i.id}">Sil</button></td></tr>`).join('')}</tbody></table></div><div class="section-head cart-total"><h3>Ümumi: ${money(total)}</h3><button id="checkout" class="btn btn-primary">Sifariş ver</button></div>`
            : '<div class="empty">Səbətiniz boşdur.</div>';

        document.querySelectorAll('.remove-item').forEach(b => b.onclick = async () => {
            try {
                await request(`/cart/items/${b.dataset.id}`, { method: 'DELETE' });
                renderCart();
            } catch (x) {
                toast(x.message, true);
            }
        });
        $('#checkout')?.addEventListener('click', () => { location.hash = 'checkout'; });
    } catch (x) {
        $('#cart-content').textContent = x.message;
    }
}

export function renderCheckout() {
    $('#app').innerHTML = `<section class="page"><a class="btn btn-plain" href="#cart">← Səbətə qayıt</a><div class="panel"><h2>Sifariş məlumatları</h2><form id="checkout-form" class="form-grid"><label>Ad<input name="firstName" required></label><label>Soyad<input name="lastName" required></label><label>Telefon nömrəsi<input name="phone" type="tel" required></label><label>Email<input name="email" type="email" required value="${esc(state.user?.email || '')}"></label><label class="full">Sifariş ünvanı<textarea name="shippingAddress" required></textarea></label><div class="full"><button class="btn btn-primary">Davam et — ödəniş</button></div></form></div></section>`;
    $('#checkout-form').onsubmit = e => {
        e.preventDefault();
        setCheckoutData(Object.fromEntries(new FormData(e.target)));
        location.hash = 'payment';
    };
}

export function renderPayment() {
    if (!checkoutData) return location.hash = 'checkout';
    $('#app').innerHTML = `<section class="page"><a class="btn btn-plain" href="#checkout">← Məlumatlara qayıt</a><div class="panel"><h2>Ödəniş üsulu</h2><form id="payment-form" class="form-stack"><label><input type="radio" name="paymentMethod" value="CARD" required> Bank kartı</label><label><input type="radio" name="paymentMethod" value="PAYPAL"> PayPal</label><button class="btn btn-primary">Sifarişi təsdiqlə</button></form></div></section>`;
    $('#payment-form').onsubmit = async e => {
        e.preventDefault();
        const paymentMethod = new FormData(e.target).get('paymentMethod');
        try {
            await request('/orders', {
                method: 'POST',
                body: JSON.stringify({ ...checkoutData, paymentMethod })
            });
            setCheckoutData(null);
            toast('Sifarişiniz uğurla yaradıldı.');
            location.hash = 'orders';
        } catch (x) {
            toast(x.message, true);
        }
    };
}

export async function renderOrders() {
    $('#app').innerHTML = `<section class="page"><h2>${isAdmin() ? 'Bütün sifarişlər' : 'Sifarişlərim'}</h2><div id="orders" class="panel">Yüklənir...</div></section>`;
    try {
        const orders = await request('/orders');
        $('#orders').innerHTML = orders.length
            ? `<div class="table-wrap"><table><thead><tr><th>#</th>${isAdmin() ? '<th>User</th><th>Gmail</th>' : ''}<th>Məbləğ</th><th>Status</th><th>Ünvan</th><th>Tarix</th>${isAdmin() ? '<th>Ləğv istəyi</th><th>Əməl</th>' : '<th></th>'}</tr></thead><tbody>${orders.map(o => `<tr><td>${o.id}</td>${isAdmin() ? `<td>${esc(o.customerUsername || '-')}</td><td>${esc(o.customerEmail || '-')}</td>` : ''}<td>${money(o.totalPrice)}</td><td>${esc(o.status)}</td><td>${esc(o.shippingAddress || '-')}</td><td>${o.createdAt ? new Date(o.createdAt).toLocaleString('az-AZ') : '-'}</td>${isAdmin() ? `<td>${o.cancellationRequested ? `<strong>${esc(o.cancellationReason)}</strong><br><small>${o.cancellationRequestedAt ? new Date(o.cancellationRequestedAt).toLocaleString('az-AZ') : ''}</small>` : '—'}</td><td>${o.status !== 'CANCELLED' && o.status !== 'DELIVERED' ? `<button class="btn btn-danger admin-cancel" data-id="${o.id}">Ləğv et</button>` : '—'}</td>` : `<td>${o.status !== 'CANCELLED' && o.status !== 'DELIVERED' && !o.cancellationRequested ? `<button class="btn btn-danger customer-cancel" data-id="${o.id}">Ləğv istə</button>` : o.cancellationRequested ? 'İstək göndərilib' : '—'}</td>`}</tr>`).join('')}</tbody></table></div>`
            : '<div class="empty">Sifariş yoxdur.</div>';

        document.querySelectorAll('.customer-cancel').forEach(b => b.onclick = () => requestCancellation(b.dataset.id));
        document.querySelectorAll('.admin-cancel').forEach(b => b.onclick = () => cancelOrder(b.dataset.id));
    } catch (x) {
        $('#orders').innerHTML = `<div class="empty">${esc(x.message)}</div>`;
    }
}

async function requestCancellation(id) {
    const reason = prompt('Sifarişin ləğv səbəbini yazın:');
    if (!reason?.trim()) return;
    try {
        await request(`/orders/${id}/cancellation-request`, {
            method: 'POST',
            body: JSON.stringify({ reason: reason.trim() })
        });
        toast('Ləğv istəyi adminə göndərildi.');
        renderOrders();
    } catch (x) {
        toast(x.message, true);
    }
}

async function cancelOrder(id) {
    if (!confirm('Bu sifariş ləğv edilsin?')) return;
    try {
        await request(`/orders/${id}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status: 'CANCELLED' })
        });
        toast('Sifariş ləğv edildi.');
        renderOrders();
    } catch (x) {
        toast(x.message, true);
    }
}