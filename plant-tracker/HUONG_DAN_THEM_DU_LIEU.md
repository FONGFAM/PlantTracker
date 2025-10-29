# 🌱 HƯỚNG DẪN THÊM DỮ LIỆU MẪU

## ✅ Dữ liệu đã tạo sẵn trong file: `sample_data.sql`

### 📊 Nội dung dữ liệu:

- **7 cây mới** với các loại khác nhau:

  - Sen Đá Xanh (Cây cảnh lá)
  - Hoa Hồng Đỏ (Cây hoa)
  - Cây Lưỡi Hổ (Cây cảnh lá)
  - Cây Tầm Xuân (Cây hoa)
  - Cây Bạc Hà (Cây thảo mộc)
  - Cây Trúc Nhật (Cây cảnh lá)
  - Hoa Lan Hồ Điệp (Cây hoa)

- **10 lịch chăm sóc** cho các cây
- **28 báo cáo** phân bố qua 6 tháng (từ tháng 5 đến tháng 10/2025)
  - Dữ liệu này sẽ hiển thị trên biểu đồ xu hướng
- **7 trạng thái hiện tại** của các cây

### 🔧 Cách 1: Sử dụng MySQL Workbench

1. Mở **MySQL Workbench**
2. Kết nối đến database `planttracker`
3. Mở file `backend/sample_data.sql`
4. Nhấn nút **Execute** (⚡️) hoặc nhấn `Ctrl+Shift+Enter`
5. Kiểm tra kết quả

### 🔧 Cách 2: Sử dụng phpMyAdmin

1. Truy cập **phpMyAdmin** (thường là `http://localhost/phpmyadmin`)
2. Chọn database `planttracker`
3. Chuyển đến tab **SQL**
4. Copy nội dung file `sample_data.sql` và paste vào
5. Nhấn **Go** để thực thi

### 🔧 Cách 3: Sử dụng Terminal (nếu có MySQL CLI)

```bash
# Kiểm tra MySQL có sẵn không
which mysql

# Nếu có, chạy lệnh:
mysql -u root -p12062004 planttracker < backend/sample_data.sql
```

### 🔧 Cách 4: Sử dụng DBeaver hoặc DataGrip

1. Kết nối đến database `planttracker`
2. Mở file `sample_data.sql`
3. Chạy script (Execute SQL Script)

### ✅ Kiểm tra sau khi thêm dữ liệu

Chạy các query sau để kiểm tra:

```sql
USE planttracker;

-- Xem tổng số cây
SELECT COUNT(*) AS 'Tổng số cây' FROM plants;

-- Xem cây mới thêm
SELECT id, name, location, create_date FROM plants ORDER BY id DESC LIMIT 7;

-- Xem báo cáo theo tháng
SELECT
    MONTH(date) as Thang,
    YEAR(date) as Nam,
    COUNT(*) as SoBaoCao
FROM plant_reports
GROUP BY YEAR(date), MONTH(date)
ORDER BY Nam DESC, Thang DESC;

-- Xem trạng thái sức khỏe
SELECT health_status, COUNT(*) as SoLuong
FROM plant_status
GROUP BY health_status;
```

### 📝 Lưu ý

- Script tự động lấy `user_id` của user có username là `admin`
- Nếu không tìm thấy, mặc định dùng `user_id = 1`
- Bạn có thể sửa trong script nếu cần
- **Ảnh (image_url) để NULL** - bạn sẽ thêm ảnh sau qua giao diện web

### 🎯 Sau khi thêm dữ liệu

1. Reload trang web frontend
2. Vào trang **Báo cáo và Thống kê**
3. Bạn sẽ thấy:
   - Biểu đồ **"Tỷ lệ loại cây"** có dữ liệu
   - Biểu đồ **"Tỷ lệ sức khỏe cây trồng"** có màu sắc
   - Biểu đồ **"Xu hướng báo cáo (6 tháng gần đây)"** có đường line

### 🖼️ Thêm ảnh sau

1. Vào trang **Quản lý cây trồng**
2. Chọn cây cần thêm ảnh
3. Nhấn **Edit** và upload ảnh
4. Hoặc thêm ảnh khi tạo báo cáo mới

---

**File:** `/Users/phonguni/workspace/gittest/PlantTracker/plant-tracker/backend/sample_data.sql`

**Tạo bởi:** GitHub Copilot
**Ngày:** 29/10/2025
