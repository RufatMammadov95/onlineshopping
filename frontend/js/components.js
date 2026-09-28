import { esc, money } from './utils.js';
import { state, isAdmin } from './state.js';

export function productCard(p) {
  const stockInfo = isAdmin()
    ? ` · Stok: ${p.stockQuantity}`
    : (p.available ? '' : ' · <span class="stock-out">Stok bitib</span>');

  const categoryNames = esc((p.categoryNames || [p.categoryName || 'Kateqoriyasız']).join(', '));

  return `<article class="product-card product-link" data-id="${p.id}" tabindex="0">
    <div class="product-image ${p.imageUrl ? '' : 'empty'}">
      ${p.imageUrl ? `<img class="product-image" src="${esc(p.imageUrl)}" alt="${esc(p.name)}">` : '◈'}
    </div>
    <div class="product-body">
      <span class="tag">Kateqoriya: ${categoryNames}${stockInfo}</span>
      <h3>${esc(p.name)}</h3>
      <p>${esc(p.description || 'Məhsul təsviri əlavə edilməyib.')}</p>
      <div class="price">${money(p.price)}</div>
      <div class="product-actions">
        ${state.token && p.available ? `<button class="btn btn-primary add-cart" data-id="${p.id}">Səbətə əlavə et</button>` : !state.token ? '<a class="btn btn-primary" href="login.html">Daxil ol</a>' : '<span class="stock-out">Stok bitib</span>'}
        ${isAdmin() ? `<button class="btn btn-danger edit-product" data-id="${p.id}">İdarə et</button>` : ''}
      </div>
    </div>
  </article>`;
}

export function categoryOptions(selectedIds = []) {
  return state.categories.map(c =>
    `<option value="${c.id}" ${selectedIds.includes(c.id) ? 'selected' : ''}>${esc(c.name)}</option>`
  ).join('');
}