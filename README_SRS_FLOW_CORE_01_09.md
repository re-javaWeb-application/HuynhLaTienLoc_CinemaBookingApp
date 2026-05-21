# SRS Flow — CORE-01 → CORE-09 (Chi tiết từng tầng)

Tài liệu mô tả **luồng dữ liệu** qua 3 lớp: **Controller → Service → Repository → Database**, kèm **DTO** và **Entity**. Khớp source code project `cinema_manager`.

---

## 0. Kiến trúc chung

```
[Browser/Client]
      ↓ HTTP (form / session)
[Controller]          ← nhận request, validate @Valid, không truy vấn DB trực tiếp
      ↓ DTO
[Service]             ← nghiệp vụ, @Transactional
      ↓ Entity / query
[Repository]          ← JPA / native SQL
      ↓ SQL
[MySQL]               ← users, movies, showtimes, bookings, tickets, payments...
```

| Thành phần | Package / ví dụ |
|------------|-----------------|
| Controller | `com.re.cinema_manager.controller.*` |
| Service | `com.re.cinema_manager.service` + `service.impl` |
| Repository | `com.re.cinema_manager.repository` |
| Entity | `com.re.cinema_manager.model.entity` |
| DTO | `com.re.cinema_manager.dto.*` (auth, profile, customer, movie, showtime, booking, admin, staff) |
| Interceptor | `AdminInterceptor`, `StaffInterceptor`, `CustomerBookingInterceptor` |

**Session:** Sau đăng nhập, `HttpSession` lưu `loggedInUser` (Entity `User`).

---

# CORE-01 — Đăng ký & Đăng nhập (mật khẩu hash)

## Mục tiêu SRS
- Đăng ký tài khoản CUSTOMER.
- Đăng nhập; `password_hash` lưu dạng `salt:sha256(salt+password)`.

## 1.1 Đăng ký

### Luồng

```
User (form home) 
  → POST /register 
  → UserController.register()
  → UserServiceImpl.Register()
  → UserRepository + UserProfileRepository
  → INSERT users, user_profiles
```

### Từng tầng

| Tầng | Class / Method | Input | Output |
|------|----------------|-------|--------|
| **View** | `home.html` (modal đăng ký) | Form fields | POST body |
| **Controller** | `UserController.register()` | `RegisterRequestDTO` + `BindingResult` | redirect `/home` |
| **DTO In** | `RegisterRequestDTO` | username, password, fullName, email, phone | — |
| **Service** | `UserServiceImpl.Register()` | DTO | void (hoặc throw) |
| **Util** | `PasswordUtil.createHash(password)` | plain password | `salt:hash` |
| **Repository** | `UserRepository.existsByUsername()` | username | boolean |
| **Repository** | `UserRepository.save(User)` | Entity `User` | `User` (có id) |
| **Repository** | `UserProfileRepository.save()` | Entity `UserProfile` | — |
| **DB** | `users`, `user_profiles` | INSERT | PK mới |

### Chi tiết Service `Register()`

1. `existsByUsername` → nếu true → `IllegalArgumentException("Tên đăng nhập đã tồn tại")`.
2. Tạo `User`: role = `CUSTOMER`, password = hash.
3. `save(user)` → tạo `UserProfile` gắn `user_id`.
4. `@Transactional` — rollback nếu lỗi FK/email trùng.

---

## 1.2 Đăng nhập

### Luồng

```
User → POST /login 
  → UserController.login() 
  → UserServiceImpl.Login() 
  → UserRepository.findByUsername() 
  → PasswordUtil.verifyPassword() 
  → session.setAttribute("loggedInUser", user)
```

| Tầng | Method | Input | Output |
|------|--------|-------|--------|
| **Controller** | `UserController.login()` | `LoginDto` | redirect (STAFF → `/staff/dashboard`, khác → `/home`) |
| **DTO In** | `LoginDto` | userName, passWord | — |
| **Service** | `UserServiceImpl.Login()` | LoginDto | Entity `User` |
| **Repository** | `UserRepository.findByUsername()` | username | `Optional<User>` |
| **Util** | `PasswordUtil.verifyPassword()` | raw, stored hash | Boolean |
| **Session** | `loggedInUser` | User entity | — |

---

# CORE-02 — Phân quyền (RBAC)

## Mục tiêu SRS
- CUSTOMER không vào `/admin/**`, `/staff/**`.
- STAFF không vào `/admin/**`.
- ADMIN không đặt vé qua `/booking/**` (customer flow).

## Interceptor (trước Controller)

| Path | Interceptor | Điều kiện cho qua |
|------|-------------|-------------------|
| `/admin/**` | `AdminInterceptor` | `loggedInUser.role == ADMIN` |
| `/staff/**` | `StaffInterceptor` | `loggedInUser.role == STAFF` |
| `/booking/**` | `CustomerBookingInterceptor` | `role == CUSTOMER` hoặc chưa login (redirect login) |

### Luồng từ chối truy cập

```
Request /admin/movies 
  → AdminInterceptor.preHandle() 
  → đọc session.loggedInUser 
  → nếu null hoặc role != ADMIN 
      → session.accessDeniedMessage 
      → redirect /home 
      → return false (chặn controller)
```

**Cấu hình:** `WebConfig.addInterceptors()`.

---

# CORE-03 — Hồ sơ cá nhân

## Mục tiêu SRS
Mọi role xem/sửa họ tên, email, phone (không đổi username qua form profile).

## Luồng xem hồ sơ

```
GET /profile 
  → ProfileController.showProfile() 
  → ProfileService.getProfileByUserId(userId) 
  → UserRepository + UserProfile (JOIN FETCH) 
  → ProfileViewModel → profile.html
```

## Luồng cập nhật

```
POST /profile 
  → ProfileController.updateProfile() 
  → ProfileService.updateProfile(userId, UpdateProfileRequest) 
  → UserProfileRepository.save()
```

| Tầng | Method | DTO |
|------|--------|-----|
| Controller | `showProfile`, `updateProfile` | `UpdateProfileRequest`, `ProfileViewModel` |
| Service | `ProfileServiceImpl` | Map Entity → ViewModel |
| Repository | `UserRepository.findByIdWithProfile` | — |
| DB | `user_profiles` | UPDATE full_name, email, phone |

---

# CORE-04 — Quản lý phim (Admin CRUD)

## Mục tiêu SRS
Admin thêm/sửa/xóa/xem phim. Thể loại (`genres`) seed sẵn.

## 4.1 Danh sách phim

```
GET /admin/movies 
  → MovieController.showMovieList() 
  → MovieService.listMoviesForAdmin() 
  → MovieRepository.findAllWithGenre() 
  → List<MovieListItemDto> 
  → admin/movie-list.html
```

| DTO Out | `MovieListItemDto` — id, title, poster, genreName, releaseDate... |

## 4.2 Tạo phim

```
GET /admin/movies/create → form trống MovieRequestDTO
POST /admin/movies/create 
  → MovieController.processCreateMovie(@Valid MovieRequestDTO) 
  → MovieService.createMovie(dto) 
  → GenreRepository.findById(genreId) 
  → MovieRepository.save(Movie)
```

| Entity | `Movie` + FK `genre_id` |

## 4.3 Sửa phim

```
GET /admin/movies/edit/{id} 
  → MovieService.getMovieRequestById(id) → MovieRequestDTO (prefill)
POST /admin/movies/edit/{id} 
  → MovieService.updateMovie(id, dto)
```

**Lưu ý:** `releaseDate` bind `yyyy-MM-dd` (`@DateTimeFormat`, `WebConfig` formatter).

## 4.4 Xóa phim

```
GET /admin/movies/delete/{id} 
  → MovieService.deletedMovie(id)
```

| Bước Service | Logic |
|--------------|-------|
| 1 | `countActiveBookingsByMovieId` (PAID/PENDING) > 0 → **không xóa**, báo lỗi |
| 2 | `deleteByShowtime_Movie_Id` — xóa booking đã hủy (nếu có) |
| 3 | `deleteByMovie_Id` — xóa showtimes |
| 4 | `deleteById` — xóa movie |

---

# CORE-05 — Xếp lịch chiếu & chống trùng phòng

## Mục tiêu SRS
Admin chọn **Phim + Phòng + Giờ bắt đầu**. Không cho 2 suất cùng phòng chồng thời gian (phim dài + **15 phút** dọn phòng).

## Công thức (ShowtimeConflictChecker)

```
Cửa sổ chiếm phòng = [startTime, startTime + duration + 15 phút)
Giao nhau ⇔ conflict → IllegalArgumentException
```

## 4.1 Tạo suất chiếu

```
POST /admin/showtimes/create 
  → ShowtimeController.processCreate(ShowtimeRequestDTO) 
  → ShowtimeServiceImpl.createShowtime(dto)
```

### Service `createShowtime()` — từng bước

| # | Hàm / Repository | Mô tả |
|---|------------------|--------|
| 1 | `validateRequest(dto)` | movieId, roomId, startTime không null; startTime > now |
| 2 | `movieRepository.findById` | Load `Movie` (lấy duration) |
| 3 | `roomRepository.findById` | Load `Room` |
| 4 | `showtimeRepository.findByRoomIdWithMovieAndRoom(roomId)` | Lấy **toàn bộ** suất phòng |
| 5 | `ShowtimeConflictChecker.findConflictWithExisting()` | Vòng lặp so conflict (logic nghiệp vụ, không phải tính tổng tiền) |
| 6 | `showtimeRepository.save(Showtime)` | INSERT `showtimes` |

| DTO In | `ShowtimeRequestDTO`: movieId, roomId, startTime |
| Entity | `Showtime` → FK movie_id, room_id |

## 4.2 Sửa / Xóa suất

- **Sửa:** `updateShowtime(id, dto)` — `excludeShowtimeId = id` khi check conflict.
- **Xóa:** `deleteShowtime(id)` — `showtimeRepository.deleteById`.

---

# CORE-06 — Thanh toán vé & Transaction

## Mục tiêu SRS
Chọn ghế → thanh toán → **một transaction**: booking + payment + tickets. Một ghế bị người khác giành → **rollback toàn bộ**.

## 6.1 Xem phim & chọn ghế (phần đầu flow đặt vé)

```
GET /home 
  → UserController.showHomePage() 
  → CustomerHomeService.getUpcomingMoviesByGenre() 
  → ShowtimeRepository.findUpcomingWithMovieGenreAndRoom(now) 
  → List<GenreSectionView> (chứa UpcomingMovieView)
```

```
GET /booking/{showtimeId} 
  → BookingController.showSeatSelection() 
  → BookingService.getSeatSelection(showtimeId) 
  → SeatSelectionView → booking-seats.html
```

| Repository | Query |
|------------|-------|
| `ShowtimeRepository.findByIdWithMovieAndRoom` | JOIN FETCH movie, room |
| `SeatRepository.findByRoomIdOrderBySeatNameAsc` | Danh sách ghế |
| `TicketRepository.findBookedSeatIdsByShowtimeId` | Ghế đã có vé |

## 6.2 Thanh toán — `createBooking()` (CORE chính)

```
POST /booking/{showtimeId}/pay 
  → BookingController.pay(seatIds, paymentMethod) 
  → Build CreateBookingRequest 
  → BookingServiceImpl.createBooking(userId, request) 
  → [@Transactional] 
  → booking-invoice.html
```

### Sequence chi tiết (trong 1 transaction)

```
BEGIN TRANSACTION
│
├─1 Validate input (seatIds không rỗng, không trùng)
├─2 showtimeRepository.findByIdWithMovieAndRoom
├─3 Kiểm tra showtime chưa qua, chưa soldOut
├─4 userRepository.findById
│
├─5 seatRepository.findByIdInAndRoomIdForUpdate(seatIds, roomId)
│     └── @Lock(PESSIMISTIC_WRITE)  ← khóa ghế
│
├─6 ticketRepository.findBookedSeatIdsAmong(showtimeId, seatIds)
│     └── nếu có → throw SeatAlreadyBookedException → ROLLBACK
│
├─7 Tạo Booking (status PAID hoặc PENDING tùy paymentMethod)
├─8 Tạo Ticket cho từng ghế (unit_price)
├─9 Tạo Payment (SUCCESS / PENDING)
├─10 bookingRepository.save(booking)  ← cascade tickets, payment
│     └── catch DataIntegrityViolationException (UK seat) → ROLLBACK
│
COMMIT
└─ return BookingInvoiceDto
```

| DTO In | `CreateBookingRequest`: showtimeId, seatIds, paymentMethod |
| DTO Out | `BookingInvoiceDto`: bookingCode, movieTitle, seatNames, totalAmount... |
| Entity ghi | `Booking`, `Ticket`, `Payment` |
| DB constraint | `UNIQUE(showtime_id, seat_id)` trên `tickets` |

### REST API (song song MVC)

```
POST /api/bookings 
  → BookingApiController.createBooking() 
  → cùng BookingService.createBooking()
  → 201 + JSON BookingInvoiceDto
  → lỗi: GlobalExceptionHandler → 409 SeatAlreadyBookedException
```

---

# CORE-07 — Lịch sử đặt vé (JOIN, pagination)

## Mục tiêu SRS
Trả về đủ: tên phim, poster, phòng, suất, ghế, tổng tiền, trạng thái thanh toán, mã booking, thời gian đặt. Tránh N+1.

## Luồng

```
GET /booking/history?page=0&size=10 
  → BookingHistoryController.history() 
  → BookingHistoryService.getBookingHistory(userId, Pageable) 
  → booking-history.html
```

### Service — 2 query (tối ưu)

| Bước | Repository | Kết quả |
|------|------------|---------|
| 1 | `BookingRepository.findHistoryByUserAndStatuses` + `@EntityGraph` | `Page<Booking>` kèm showtime, movie, room, payment |
| 2 | `TicketRepository.findByBookingIdInWithSeat(bookingIds)` | `List<Ticket>` + seat (1 query batch) |
| 3 | Map → `BookingHistoryItemDto` | Không JOIN ticket trong page query (tránh cartesian) |

| DTO Out | `BookingHistoryItemDto` |
| Pagination | `Pageable` sort `createdAt DESC` |

---

# CORE-08 — Kiểm soát trạng thái suất chiếu

## Mục tiêu SRS
1. Suất **đã qua** → không hiện trên lịch sắp chiếu.
2. Suất **hết vé** → vẫn hiện nhưng nhãn "Hết vé", không cho đặt thêm.

## Luồng ẩn suất đã qua

```
CustomerHomeServiceImpl.getUpcomingMoviesByGenre()
  → ShowtimeRepository.findUpcomingWithMovieGenreAndRoom(from = LocalDateTime.now())
  → SQL: WHERE start_time >= :from
```

## Luồng hết vé

```
ShowtimeAvailabilityService.isSoldOut(showtimeId, roomId)
  → count tickets (non-CANCELLED) >= count seats in room
```

| Nơi dùng | Field DTO |
|----------|-----------|
| `UpcomingMovieView` | `soldOut = true` → UI "Hết vé", nút disabled |
| `SeatSelectionView` | `soldOut` → chặn POST pay |
| `booking-seats.html` | Hiển thị banner hết vé |

---

# CORE-09 — Hủy vé & giải phóng ghế

## Mục tiêu SRS
- **PAID** (online hoặc quầy đã xác nhận): hủy trước **≥ 24 giờ** so với giờ chiếu.
- **PENDING** (chờ thanh toán tại quầy): hủy được đến **trước giờ chiếu**.
Đổi booking → CANCELLED, **xóa tickets** → ghế trống lại cho CORE-06 (UNIQUE `showtime_id+seat_id`).

## Luồng

```
POST /booking/history/{bookingId}/cancel 
  → BookingHistoryController.cancelBooking() 
  → BookingServiceImpl.cancelBooking(userId, bookingId) 
  → [@Transactional]
```

### Service `cancelBooking()` — từng bước

| # | Logic |
|---|--------|
| 1 | `findByIdAndUserIdWithDetails` — đúng chủ đơn |
| 2 | Status phải PAID hoặc PENDING |
| 3 | `BookingPolicy.evaluateCancel(status, showtimeStart)` — PENDING: trước giờ chiếu; PAID: ≥ 24h |
| 4 | `ticketRepository.deleteByBookingId` — **giải phóng ghế** |
| 5 | `payment.status = FAILED` (nếu có) |
| 6 | `booking.status = CANCELLED` |
| 7 | `bookingRepository.save` |

| Util | `BookingPolicy.CANCEL_MIN_HOURS_BEFORE_SHOWTIME = 24` |
| DTO history | `cancellable` flag tính từ `BookingPolicy` |

---

# Phụ lục — Mở rộng đã có trong project

## Staff — Quầy vé

```
/staff/counter → StaffBookingService.lookupByCode / confirmCounterPayment
```

## Staff — Dashboard thống kê phòng/ghế

```
/staff/dashboard → StaffDashboardService → StaffDashboardRepository (GROUP BY native SQL)
```

## Admin — Quản lý tài khoản

```
/admin/users → AdminUserService (CRUD users + profiles)
```

## Hướng 4 — Dashboard doanh thu

```
/admin/reports → RevenueReportService → PaymentRepository (SUM, GROUP BY, JOIN Top 5)
```

---

# Bảng tổng hợp Endpoint ↔ CORE

| CORE | HTTP chính | Controller | Service chính |
|------|------------|------------|---------------|
| 01 | POST /register, /login | UserController | UserServiceImpl |
| 02 | * (interceptor) | Admin/Staff/Customer interceptors | — |
| 03 | GET/POST /profile | ProfileController | ProfileServiceImpl |
| 04 | /admin/movies/** | MovieController | MovieServiceImpl |
| 05 | /admin/showtimes/** | ShowtimeController | ShowtimeServiceImpl |
| 06 | /booking/**, POST /api/bookings | BookingController, BookingApiController | BookingServiceImpl |
| 07 | /booking/history | BookingHistoryController | BookingHistoryServiceImpl |
| 08 | /home, getSeatSelection | UserController, BookingService | CustomerHomeService, ShowtimeAvailabilityService |
| 09 | POST .../cancel | BookingHistoryController | BookingServiceImpl.cancelBooking |

---

# Sơ đồ ER luồng dữ liệu đặt vé (CORE-06 + 07 + 09)

```
users ──< bookings >── showtimes >── movies
              │              └── rooms
              ├──< tickets >── seats
              └── payment (1-1)
```

**Đọc lịch sử (CORE-07):** bookings → showtimes → movies, rooms + tickets → seats.

**Hủy vé (CORE-09):** DELETE tickets WHERE booking_id = ? ; UPDATE bookings SET status = CANCELLED.

---

*Tài liệu sinh từ codebase thực tế — cập nhật khi đổi API hoặc tên class.*
