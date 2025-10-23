# 🌱 Plant Tracker - Hệ thống quản lý cây trồng

Plant Tracker là một ứng dụng web hoàn chỉnh để quản lý cây trồng với các tính năng theo dõi, chăm sóc và báo cáo thông minh.

## ✨ Tính năng chính

### 🔐 Authentication System

- Đăng nhập/Đăng ký tài khoản
- Bảo vệ routes với JWT token
- Quản lý phiên đăng nhập

### 🌿 Quản lý cây trồng

- CRUD operations cho cây trồng
- Upload và hiển thị hình ảnh
- Phân loại cây theo loại
- Theo dõi thông tin chi tiết (tên, loài, mô tả)

### 📅 Lịch chăm sóc

- Tạo và quản lý lịch chăm sóc
- Đánh dấu hoàn thành/bỏ qua
- Lọc theo cây cụ thể
- Sinh lịch tự động bằng AI (sắp có)

### 📊 Báo cáo & Thống kê

- Dashboard tổng quan với thống kê
- Biểu đồ phân tích dữ liệu
- Báo cáo chi tiết theo cây
- Xuất báo cáo Excel

### 🏷️ Quản lý loại cây

- CRUD operations cho loại cây
- Thống kê số lượng cây theo loại
- Quản lý mô tả loại cây

## 🛠️ Công nghệ sử dụng

### Backend (Spring Boot)

- **Framework**: Spring Boot 3.x
- **Database**: MySQL 8.0
- **Security**: Spring Security + JWT
- **ORM**: JPA/Hibernate
- **AI Integration**: OpenAI API

### Frontend (React)

- **Framework**: React 18
- **UI Library**: Material-UI (MUI) v5
- **Charts**: Chart.js + react-chartjs-2
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **Date Handling**: date-fns

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống

- Node.js 16+
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Backend Setup

1. **Cấu hình database**:

   ```sql
   CREATE DATABASE planttracker;
   ```

2. **Cập nhật cấu hình** trong `backend/src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/planttracker
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   openai.api.key=your_openai_api_key
   ```

3. **Chạy backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

### Frontend Setup

1. **Cài đặt dependencies**:

   ```bash
   cd frontend
   npm install
   ```

2. **Chạy development server**:

   ```bash
   npm start
   ```

3. **Truy cập ứng dụng**:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8081

## 📱 Hướng dẫn sử dụng

### 1. Đăng ký/Đăng nhập

- Truy cập `/authentication/sign-up` để tạo tài khoản mới
- Truy cập `/authentication/sign-in` để đăng nhập

### 2. Quản lý loại cây

- Vào **Plant Types** để thêm các loại cây (VD: Cây cảnh, Cây ăn quả, Cây thuốc)
- Có thể chỉnh sửa hoặc xóa loại cây

### 3. Quản lý cây trồng

- Vào **Plants** để thêm cây mới
- Chọn loại cây, thêm hình ảnh và mô tả
- Có thể chỉnh sửa hoặc xóa cây

### 4. Lập lịch chăm sóc

- Vào **Care Schedules** để tạo lịch chăm sóc
- Chọn cây và loại công việc (tưới nước, bón phân, kiểm tra...)
- Đánh dấu hoàn thành khi thực hiện

### 5. Xem báo cáo

- Vào **Reports** để xem thống kê tổng quan
- Có thể xem báo cáo chi tiết theo từng cây
- Xuất báo cáo Excel

## 🔧 API Endpoints

### Authentication

- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký

### Plants

- `GET /api/plants` - Lấy danh sách cây
- `POST /api/plants` - Thêm cây mới
- `PUT /api/plants/{id}` - Cập nhật cây
- `DELETE /api/plants/{id}` - Xóa cây

### Plant Types

- `GET /api/plant-types` - Lấy danh sách loại cây
- `POST /api/plant-types` - Thêm loại cây mới
- `PUT /api/plant-types/{id}` - Cập nhật loại cây
- `DELETE /api/plant-types/{id}` - Xóa loại cây

### Care Schedules

- `GET /api/care-schedules` - Lấy danh sách lịch chăm sóc
- `POST /api/care-schedules` - Thêm lịch mới
- `PUT /api/care-schedules/{id}` - Cập nhật lịch
- `DELETE /api/care-schedules/{id}` - Xóa lịch

### Reports

- `GET /api/reports/summary` - Thống kê tổng quan
- `GET /api/reports/plant/{id}` - Báo cáo theo cây
- `GET /api/analytics/{id}` - Phân tích cây

## 🎨 UI/UX Features

- **Responsive Design**: Tương thích với mọi thiết bị
- **Dark/Light Mode**: Chuyển đổi theme dễ dàng
- **Material Design**: Giao diện hiện đại, thân thiện
- **Real-time Updates**: Cập nhật dữ liệu real-time
- **Interactive Charts**: Biểu đồ tương tác với Chart.js
- **Form Validation**: Validation form đầy đủ
- **Loading States**: Hiển thị trạng thái loading
- **Error Handling**: Xử lý lỗi thân thiện

## 🔮 Tính năng sắp có

- [ ] Sinh lịch chăm sóc tự động bằng AI
- [ ] Thông báo nhắc nhở chăm sóc
- [ ] Upload ảnh từ camera
- [ ] Chia sẻ cây trồng với cộng đồng
- [ ] Mobile app (React Native)
- [ ] Tích hợp IoT sensors

## 🤝 Đóng góp

Mọi đóng góp đều được chào đón! Hãy tạo issue hoặc pull request.

## 📄 License

Dự án này được phát hành dưới MIT License.

## 👨‍💻 Tác giả

Được phát triển bởi [Tên tác giả] với ❤️

---

**Lưu ý**: Đây là phiên bản demo. Để sử dụng trong production, vui lòng cấu hình bảo mật và tối ưu hóa phù hợp.
