# Online Shopping API

Java 21 və Spring Boot ilə hazırlanmış onlayn alış-veriş REST API-si. Layihədə JWT autentifikasiyası, `CUSTOMER` və `ADMIN` rolları, məhsul/kateqoriya idarəetməsi, səbət, sifariş və Swagger UI mövcuddur. Frontend ayrıca `frontend/` qovluğundadır.

## Texnologiyalar

- Java 21, Spring Boot 3.2.5, Maven
- Spring Data JPA / Hibernate və PostgreSQL 15
- Spring Security və JWT
- Springdoc OpenAPI / Swagger UI
- Docker Compose
- JUnit 5, Mockito və H2 (testlər üçün)

## İşə salma

### Tələblər

- Java 21
- PostgreSQL **və ya** Docker Desktop
- Lokal frontend üçün Node.js (istəyə bağlı)

### Mühit dəyişənləri

Layihənin kökündə `.env` faylı yaradın. Nümunə:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/online_shopping_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
APP_JWT_SECRET=minimum_32_simvoldan_uzun_gizli_jwt_acari_yazin
APP_JWT_EXPIRATIONMS=86400000
```

`APP_JWT_SECRET` ən azı 32 simvol olmalıdır. Lokal işə salmada PostgreSQL-də `online_shopping_db` bazasını yaradın.

### Docker ilə

```bash
docker compose up --build
```

- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- Frontend: `http://localhost:3000`

Dayandırmaq üçün:

```bash
docker compose down
```

### Lokal işə salma

```bash
./mvnw spring-boot:run
```

Windows PowerShell-də:

```powershell
.\mvnw.cmd spring-boot:run
```

Frontend-i ayrıca başlatmaq üçün:

```powershell
cd frontend
node server.js
```

Sonra `http://localhost:5500` ünvanını açın.

## Swagger-dən istifadə

Swagger ünvanı: `http://localhost:8080/swagger-ui/index.html`

Swagger-də endpoint-i açın, **Try it out** düyməsini sıxın, aşağıdakı uyğun JSON-u **Request body** sahəsinə yazın və **Execute** edin. `GET` və `DELETE` sorğularına JSON yazılmır; onlar yalnız parametr (`id`, `itemId`, `name`) və ya token tələb edə bilər.

### 1. Qeydiyyat və giriş

**POST `/auth/register`** — adi `CUSTOMER` istifadəçisi yaradır:

```json
{
  "username": "ali",
  "email": "ali@example.com",
  "password": "StrongPassword123"
}
```

**POST `/auth/login`** — token almaq üçün:

```json
{
  "username": "ali",
  "password": "StrongPassword123"
}
```

Cavabdakı `token` dəyərini kopyalayın. Swagger-in yuxarısındakı **Authorize** düyməsində `bearerAuth` sahəsinə yalnız tokenin özünü yapışdırın (adətən `Bearer ` prefiksi yazılmır), sonra **Authorize** edin.

### 2. Admin yaratmaq və ya rolu vermək

İlk admin istifadəçisini yaratmaq üçün aşağıdakı SQL sorğusundan istifadə edin. Sonra admin tokeni ilə **POST `/admin/users`** endpoint-i yeni admin yaratmaq üçün istifadə edilə bilər:

```json
{
  "username": "admin2",
  "email": "admin2@example.com",
  "password": "StrongPassword123"
}
```

### 3. Kateqoriya və məhsul

Bu endpoint-lər üçün admin tokeni ilə authorize olun.

**POST `/categories`**:

```json
{
  "name": "Elektronika",
  "description": "Telefon, kompüter və aksesuarlar"
}
```

**POST `/products`** və **PUT `/products/{id}`**:

```json
{
  "name": "Wireless Mouse",
  "description": "Simsiz optik siçan",
  "price": 29.99,
  "stockQuantity": 50,
  "imageUrl": "https://example.com/images/mouse.jpg",
  "categoryId": 1
}
```

`categoryId` əvvəl yaradılmış kateqoriyanın ID-sidir. Məhsulları `GET /products`, kateqoriyaya görə `GET /products/category/{categoryId}`, ada görə isə `GET /products/search?name=mouse` ilə görə bilərsiniz.

### 4. Səbət və sifariş

Bu endpoint-lər üçün `CUSTOMER` tokeni ilə authorize olun.

**POST `/cart`** — məhsulu səbətə əlavə edir:

```json
{
  "productId": 1,
  "quantity": 2
}
```

Səbətə baxmaq: **GET `/cart`**. Səbət sətrini silmək: **DELETE `/cart/items/{itemId}`**; `itemId` dəyərini `GET /cart` cavabındakı `id` sahəsindən götürün. Səbəti tam təmizləmək: **DELETE `/cart/clear`**.

**POST `/orders`** — səbətdəki məhsullardan sifariş yaradır:

```json
{
  "shippingAddress": "Bakı şəhəri, Nizami küçəsi 10, mənzil 5"
}
```

Sifarişlərə baxmaq: **GET `/orders`**. Sifariş yaradılan zaman məhsul stokundan səbətdəki miqdar çıxılır.

## Endpoint və giriş hüquqları

| Metod | Endpoint | Giriş |
|---|---|---|
| POST | `/auth/register`, `/auth/login` | Açıq |
| POST | `/admin/users` | ADMIN |
| GET | `/categories`, `/products/**` | Açıq |
| POST, PUT, DELETE | `/categories/**`, `/products/**` | ADMIN |
| GET, POST, DELETE | `/cart/**` | CUSTOMER |
| GET, POST | `/orders` | CUSTOMER və ya ADMIN |

## SQL: istifadəçi rollarının və məlumatlarının idarə edilməsi

Bu sorğuları PostgreSQL-də işlədin. Dəyişiklik/silmə etməzdən əvvəl düzgün istifadəçini seçdiyinizi yoxlayın.

Mövcud istifadəçilərə baxış:

```sql
SELECT id, username, email, role FROM users;
```

İstifadəçiyə `ADMIN` rolu vermək (ilk admin üçün):

```sql
UPDATE users
SET role = 'ADMIN'
WHERE email = 'senin_emailin@gmail.com';
```

İstifadəçini silmək üçün köhnə sorğudakı səbət asılılıqları düzəldildi: əvvəl `cart_items`, sonra `carts` silinməlidir. İstifadəçinin sifarişləri varsa, `order_items` və `orders` da xarici açar məhdudiyyəti yaratdığı üçün onlar da əvvəl silinir. `:user_id` yerinə silinəcək istifadəçinin ID-sini yazın (məsələn, `2`).

```sql
BEGIN;

-- Əvvəl sifarişin asılı sətirlərini silirik.
DELETE FROM order_items
WHERE order_id IN (
    SELECT id FROM orders WHERE user_id = :user_id
);

DELETE FROM orders
WHERE user_id = :user_id;

-- Sonra səbətin asılı sətirlərini, daha sonra səbətin özünü silirik.
DELETE FROM cart_items
WHERE cart_id IN (
    SELECT id FROM carts WHERE user_id = :user_id
);

DELETE FROM carts
WHERE user_id = :user_id;

-- Sonda istifadəçini silirik.
DELETE FROM users
WHERE id = :user_id;

COMMIT;
```

Qeyd: psql-də `:user_id` parametr kimi işləmirsə, onu birbaşa rəqəmlə əvəz edin; məsələn `WHERE user_id = 2`. Səhv istifadəçini seçmisinizsə, `COMMIT` əvəzinə `ROLLBACK;` işlədin.

## Testlər

```bash
./mvnw test
```

Windows PowerShell-də:

```powershell
.\mvnw.cmd test
```

## Layihə quruluşu

```text
src/main/java/com/rufat/onlineshopping
├── config        # OpenAPI və CORS konfiqurasiyası
├── controller    # REST endpoint-ləri
├── dto           # Request/response modelləri
├── entity        # JPA entity-ləri
├── repository    # Verilənlər bazası sorğuları
├── security      # JWT və Spring Security
└── service       # Biznes məntiqi
frontend/         # HTML, CSS və JavaScript istifadəçi interfeysi
```
