# TODO: Tạo báo cáo tổng hợp từ PlantReport và PlantStatus

## Các bước cần thực hiện:

1. **Chỉnh sửa ReportService.java**

   - Thêm injection cho PlantReportRepository và PlantStatusRepository
   - Thêm phương thức `getCombinedReports` để lấy và kết hợp dữ liệu từ PlantReport và PlantStatus cho tất cả cây của user

2. **Chỉnh sửa ReportController.java**

   - Bỏ comment cho controller
   - Thêm endpoint mới `/api/reports/combined` để trả về dữ liệu báo cáo tổng hợp

3. **Kiểm tra và test**
   - Test endpoint mới để đảm bảo trả về dữ liệu kết hợp chính xác
   - Nếu cần, thêm chức năng export Excel cho báo cáo tổng hợp ở bước sau

## Trạng thái:

- [x] Bước 1: Chỉnh sửa ReportService.java
- [x] Bước 2: Chỉnh sửa ReportController.java
- [x] Bước 3: Test và xác minh
