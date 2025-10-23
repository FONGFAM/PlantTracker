# 🌱 HƯỚNG DẪN CHẠY DỰ ÁN PLANT TRACKER

## 📋 Yêu cầu hệ thống
- **Node.js** (phiên bản 16 trở lên)
- **Java** (phiên bản 21) - *Tùy chọn, chỉ cần nếu muốn chạy backend*
- **Maven** - *Tùy chọn, chỉ cần nếu muốn chạy backend*

## 🚀 CÁCH 1: CHẠY CHỈ FRONTEND (Đơn giản nhất)

### Bước 1: Mở Terminal/PowerShell
```bash
# Mở Command Prompt hoặc PowerShell
# Nhấn Windows + R, gõ "cmd" hoặc "powershell"
```

### Bước 2: Di chuyển đến thư mục frontend
```bash
cd C:\Users\Admin\Downloads\plant-tracker\plant-tracker\frontend
```

### Bước 3: Cài đặt dependencies (chỉ lần đầu)
```bash
npm install
```

### Bước 4: Khởi động frontend
```bash
npm start
```

### Bước 5: Mở trình duyệt
- Truy cập: `http://localhost:3000`
- **Vào thẳng Dashboard** mà không cần đăng nhập
- Có sẵn dữ liệu mẫu để test

---

## 🔧 CÁCH 2: CHẠY CẢ FRONTEND + BACKEND (Đầy đủ)

### Phần A: Chạy Backend

#### Bước 1: Mở Terminal thứ nhất
```bash
cd C:\Users\Admin\Downloads\plant-tracker\plant-tracker\backend
```

#### Bước 2: Kiểm tra Java
```bash
java -version
# Phải hiển thị Java 21
```

#### Bước 3: Chạy backend (chọn 1 trong 2 cách)

**Cách 3a: Sử dụng Maven (nếu có Maven)**
```bash
mvn spring-boot:run
```

**Cách 3b: Sử dụng JAR file**
```bash
java -jar target/backend-1.0.0.jar
```

#### Bước 4: Kiểm tra backend
- Backend chạy trên: `http://localhost:8081`
- H2 Console: `http://localhost:8081/h2-console`

### Phần B: Chạy Frontend

#### Bước 1: Mở Terminal thứ hai
```bash
cd C:\Users\Admin\Downloads\plant-tracker\plant-tracker\frontend
```

#### Bước 2: Khởi động frontend
```bash
npm start
```

#### Bước 3: Mở trình duyệt
- Truy cập: `http://localhost:3000`

---

## 🎯 TÀI KHOẢN TEST (nếu cần đăng nhập)

- **Username**: `test`
- **Password**: `123456`

---

## 🚀 TÍNH NĂNG ĐÃ BẬT

### ✅ Bypass Authentication
- Không cần đăng nhập, vào thẳng Dashboard
- File: `frontend/src/components/ProtectedRoute.js`

### ✅ Mock API
- Frontend hoạt động độc lập, không cần backend
- File: `frontend/src/utils/mockApi.js`
- Có sẵn dữ liệu mẫu: 3 cây cảnh, lịch chăm sóc, báo cáo

### ✅ H2 Database
- Database in-memory, không cần cài MySQL
- File: `backend/src/main/resources/application.properties`

### ✅ Code Refactoring & Optimization
- **MDAlertRoot.js**: Loại bỏ header bản quyền dài dòng, tối ưu hóa destructuring
- **App.js**: Thêm lazy loading cho components với loading spinner xoay giữa màn hình

---

## 🔧 XỬ LÝ LỖI THƯỜNG GẶP

### Lỗi: "npm start" không tìm thấy script
```bash
# Kiểm tra đang ở đúng thư mục
pwd
# Phải hiển thị: .../plant-tracker/frontend

# Nếu sai, di chuyển lại
cd C:\Users\Admin\Downloads\plant-tracker\plant-tracker\frontend
```

### Lỗi: "mvn" không được nhận diện
```bash
# Cài đặt Maven hoặc sử dụng JAR file
java -jar target/backend-1.0.0.jar
```

### Lỗi: "java" không được nhận diện
```bash
# Cài đặt Java 21 từ: https://adoptium.net/
# Hoặc chỉ chạy frontend với Mock API
```

### Lỗi: Port 3000 đã được sử dụng
```bash
# Tìm và kill process đang dùng port 3000
netstat -ano | findstr :3000
taskkill /PID <PID_NUMBER> /F

# Hoặc chạy trên port khác
npm start -- --port 3001
```

### Lỗi: Port 8081 đã được sử dụng
```bash
# Tìm và kill process đang dùng port 8081
netstat -ano | findstr :8081
taskkill /PID <PID_NUMBER> /F
```

---

## 📱 TRUY CẬP ỨNG DỤNG

### Frontend (Giao diện chính)
- **URL**: `http://localhost:3000`
- **Tính năng**: Dashboard, Plants, Schedules, Reports, Profile, Settings

### Backend API (nếu chạy backend)
- **URL**: `http://localhost:8081/api`
- **Endpoints**: `/plants`, `/schedules`, `/reports`, `/auth`

### H2 Database Console (nếu chạy backend)
- **URL**: `http://localhost:8081/h2-console`
- **JDBC URL**: `jdbc:h2:mem:planttracker`
- **Username**: `sa`
- **Password**: (để trống)

---

## 🎉 KẾT QUẢ MONG ĐỢI

Sau khi chạy thành công, bạn sẽ thấy:

1. **Dashboard** với thống kê tổng quan
2. **Plants** - Danh sách 3 cây cảnh mẫu
3. **Schedules** - Lịch chăm sóc
4. **Reports** - Báo cáo thống kê
5. **Profile & Settings** - Cài đặt cá nhân

---

## 💡 MẸO NHỎ

- **Lần đầu**: Chỉ cần chạy frontend với `npm start`
- **Phát triển**: Chạy cả frontend + backend để test API
- **Demo**: Sử dụng Mock API, không cần backend
- **Production**: Tắt Mock API, bật authentication

---

## 📞 HỖ TRỢ

Nếu gặp lỗi, hãy:
1. Kiểm tra đang ở đúng thư mục
2. Đảm bảo đã cài đặt Node.js
3. Thử chạy lại từ đầu
4. Kiểm tra port có bị chiếm không

**Chúc bạn sử dụng vui vẻ! 🌱**
