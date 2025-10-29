-- ===================================
-- SCRIPT THÊM DỮ LIỆU MẪU CHO PLANT TRACKER
-- ===================================

USE planttracker;

-- 1. Thêm Plant Types (nếu chưa có)
INSERT IGNORE INTO plant_types (id, type_name, description) VALUES
(1, 'Cây cảnh lá', 'Cây trồng có lá đẹp, thích hợp trong nhà'),
(2, 'Cây hoa', 'Cây cho hoa đẹp, trang trí'),
(3, 'Cây thủy sinh', 'Cây trồng trong nước hoặc môi trường ẩm'),
(4, 'Cây ăn quả', 'Cây cho quả ăn được'),
(5, 'Cây thảo mộc', 'Cây làm gia vị hoặc làm thuốc');

-- 2. Kiểm tra user hiện tại (giả sử username là 'admin' hoặc 'user')
-- Bạn cần thay đổi user_id phù hợp với user trong bảng users

-- Lấy user_id của admin (thay đổi theo database của bạn)
SET @admin_user_id = (SELECT id FROM users WHERE username = 'admin' LIMIT 1);
SET @user_id = COALESCE(@admin_user_id, 1); -- Mặc định là 1 nếu không tìm thấy

-- 3. Thêm Plants mới
INSERT INTO plants (name, description, plant_type_id, user_id, create_date, created_at, updated_at, image_url) VALUES
('Sen Đá Xanh', 'Cây sen đá màu xanh tươi, dễ chăm sóc, để ban công tầng 2', 1, @user_id, '2024-08-15 10:00:00', NOW(), NOW(), NULL),
('Hoa Hồng Đỏ', 'Hoa hồng đỏ thắm, hương thơm ngát, trồng ở sân vườn', 2, @user_id, '2024-09-01 14:30:00', NOW(), NOW(), NULL),
('Cây Lưỡi Hổ', 'Cây lưỡi hổ vàng viền, lọc không khí tốt, để phòng khách', 1, @user_id, '2024-09-15 09:00:00', NOW(), NOW(), NULL),
('Cây Tầm Xuân', 'Tầm xuân nhiều màu, dễ chăm sóc, trồng ban công', 2, @user_id, '2024-10-01 11:00:00', NOW(), NOW(), NULL),
('Cây Bạc Hà', 'Bạc hà thơm, dùng làm trà, trồng ở bếp', 5, @user_id, '2024-10-10 16:00:00', NOW(), NOW(), NULL),
('Cây Trúc Nhật', 'Trúc để bàn, may mắn, đặt bàn làm việc', 1, @user_id, '2024-07-20 08:30:00', NOW(), NOW(), NULL),
('Hoa Lan Hồ Điệp', 'Lan hồ điệp trắng tinh khôi, để phòng khách', 2, @user_id, '2024-08-25 13:00:00', NOW(), NOW(), NULL),
('Cây Kim Tiền', 'Cây kim tiền tài lộc, lá xanh bóng, phòng làm việc', 1, @user_id, '2024-06-10 10:30:00', NOW(), NOW(), NULL),
('Hoa Cúc Vàng', 'Hoa cúc vàng rực rỡ, trồng sân trước', 2, @user_id, '2024-09-10 15:00:00', NOW(), NOW(), NULL),
('Cây Phát Tài', 'Cây phát tài lá to, xanh mướt, đặt cửa chính', 1, @user_id, '2024-07-05 09:15:00', NOW(), NOW(), NULL),
('Cây Trầu Bà', 'Trầu bà leo tường, dễ sống, leo tường rào', 1, @user_id, '2024-08-05 11:45:00', NOW(), NOW(), NULL),
('Hoa Dạ Yến Thảo', 'Dạ yến thảo tím, hoa dày, ban công tầng 3', 2, @user_id, '2024-09-20 14:00:00', NOW(), NOW(), NULL),
('Cây Xương Rồng', 'Xương rồng gai, ít nước, để bàn học', 1, @user_id, '2024-10-05 10:00:00', NOW(), NOW(), NULL),
('Cây Ngọc Ngân', 'Ngọc ngân lá nhỏ, xinh xắn, treo giá', 1, @user_id, '2024-08-20 13:30:00', NOW(), NOW(), NULL),
('Hoa Phong Lan', 'Phong lan tím nhạt, thanh lịch, phòng ngủ', 2, @user_id, '2024-07-15 16:20:00', NOW(), NOW(), NULL),
('Cây Lô Hội', 'Lô hội dùng làm mặt nạ, để phòng tắm', 5, @user_id, '2024-09-05 09:40:00', NOW(), NOW(), NULL),
('Cây Húng Quế', 'Húng quế thơm, làm gia vị, trồng ở bếp', 5, @user_id, '2024-10-12 08:00:00', NOW(), NOW(), NULL),
('Hoa Đồng Tiền', 'Đồng tiền vàng, nhiều hoa, treo giỏ', 2, @user_id, '2024-08-30 12:00:00', NOW(), NOW(), NULL),
('Cây Dương Xỉ', 'Dương xỉ lá xanh mướt, ưa bóng, góc nhà', 1, @user_id, '2024-07-25 10:50:00', NOW(), NOW(), NULL),
('Cây Vạn Niên Thanh', 'Vạn niên thanh lá đỏ, bóng đẹp, hành lang', 1, @user_id, '2024-06-20 14:15:00', NOW(), NOW(), NULL),
('Hoa Sứ', 'Hoa sứ trắng tinh khôi, trồng sân sau', 2, @user_id, '2024-09-25 11:30:00', NOW(), NOW(), NULL),
('Cây Cọ Nhật', 'Cọ nhật nhỏ, để bàn làm việc', 1, @user_id, '2024-08-12 15:45:00', NOW(), NOW(), NULL),
('Cây Cỏ Lạc', 'Cỏ lạc trồng chậu nhỏ, để cửa sổ', 1, @user_id, '2024-10-08 09:20:00', NOW(), NOW(), NULL),
('Hoa Dừa Cạn', 'Dừa cạn hồng, tím, trồng ban công', 2, @user_id, '2024-09-15 13:10:00', NOW(), NOW(), NULL),
('Cây Tía Tô', 'Tía tô làm gia vị, trồng ở bếp', 5, @user_id, '2024-10-15 10:40:00', NOW(), NOW(), NULL);

-- Lấy ID của các plants vừa thêm
SET @plant1 = (SELECT id FROM plants WHERE name = 'Sen Đá Xanh' LIMIT 1);
SET @plant2 = (SELECT id FROM plants WHERE name = 'Hoa Hồng Đỏ' LIMIT 1);
SET @plant3 = (SELECT id FROM plants WHERE name = 'Cây Lưỡi Hổ' LIMIT 1);
SET @plant4 = (SELECT id FROM plants WHERE name = 'Cây Tầm Xuân' LIMIT 1);
SET @plant5 = (SELECT id FROM plants WHERE name = 'Cây Bạc Hà' LIMIT 1);
SET @plant6 = (SELECT id FROM plants WHERE name = 'Cây Trúc Nhật' LIMIT 1);
SET @plant7 = (SELECT id FROM plants WHERE name = 'Hoa Lan Hồ Điệp' LIMIT 1);
SET @plant8 = (SELECT id FROM plants WHERE name = 'Cây Kim Tiền' LIMIT 1);
SET @plant9 = (SELECT id FROM plants WHERE name = 'Hoa Cúc Vàng' LIMIT 1);
SET @plant10 = (SELECT id FROM plants WHERE name = 'Cây Phát Tài' LIMIT 1);
SET @plant11 = (SELECT id FROM plants WHERE name = 'Cây Trầu Bà' LIMIT 1);
SET @plant12 = (SELECT id FROM plants WHERE name = 'Hoa Dạ Yến Thảo' LIMIT 1);
SET @plant13 = (SELECT id FROM plants WHERE name = 'Cây Xương Rồng' LIMIT 1);
SET @plant14 = (SELECT id FROM plants WHERE name = 'Cây Ngọc Ngân' LIMIT 1);
SET @plant15 = (SELECT id FROM plants WHERE name = 'Hoa Phong Lan' LIMIT 1);
SET @plant16 = (SELECT id FROM plants WHERE name = 'Cây Lô Hội' LIMIT 1);
SET @plant17 = (SELECT id FROM plants WHERE name = 'Cây Húng Quế' LIMIT 1);
SET @plant18 = (SELECT id FROM plants WHERE name = 'Hoa Đồng Tiền' LIMIT 1);
SET @plant19 = (SELECT id FROM plants WHERE name = 'Cây Dương Xỉ' LIMIT 1);
SET @plant20 = (SELECT id FROM plants WHERE name = 'Cây Vạn Niên Thanh' LIMIT 1);
SET @plant21 = (SELECT id FROM plants WHERE name = 'Hoa Sứ' LIMIT 1);
SET @plant22 = (SELECT id FROM plants WHERE name = 'Cây Cọ Nhật' LIMIT 1);
SET @plant23 = (SELECT id FROM plants WHERE name = 'Cây Cỏ Lạc' LIMIT 1);
SET @plant24 = (SELECT id FROM plants WHERE name = 'Hoa Dừa Cạn' LIMIT 1);
SET @plant25 = (SELECT id FROM plants WHERE name = 'Cây Tía Tô' LIMIT 1);

-- 4. Thêm Care Schedules
INSERT INTO care_schedules (plant_id, type, frequency, last_performed_at, next_at, active) VALUES
(@plant1, 'Tưới nước', 'Mỗi 3 ngày', '2025-10-27 08:00:00', '2025-10-30 08:00:00', true),
(@plant1, 'Bón phân', 'Mỗi 2 tuần', '2025-10-15 09:00:00', '2025-10-29 09:00:00', true),
(@plant2, 'Tưới nước', 'Hàng ngày', '2025-10-28 07:00:00', '2025-10-29 07:00:00', true),
(@plant2, 'Cắt tỉa', 'Mỗi tuần', '2025-10-25 10:00:00', '2025-11-01 10:00:00', true),
(@plant3, 'Tưới nước', 'Mỗi 5 ngày', '2025-10-26 08:00:00', '2025-10-31 08:00:00', true),
(@plant4, 'Tưới nước', 'Mỗi 2 ngày', '2025-10-27 08:30:00', '2025-10-29 08:30:00', true),
(@plant5, 'Tưới nước', 'Hàng ngày', '2025-10-28 09:00:00', '2025-10-29 09:00:00', true),
(@plant5, 'Thu hoạch', 'Mỗi tuần', '2025-10-24 10:00:00', '2025-10-31 10:00:00', true),
(@plant6, 'Tưới nước', 'Mỗi 3 ngày', '2025-10-27 07:30:00', '2025-10-30 07:30:00', true),
(@plant7, 'Tưới nước', 'Mỗi 4 ngày', '2025-10-26 08:00:00', '2025-10-30 08:00:00', true),
(@plant8, 'Tưới nước', 'Mỗi 7 ngày', '2025-10-25 08:00:00', '2025-11-01 08:00:00', true),
(@plant8, 'Bón phân', 'Mỗi tháng', '2025-10-01 09:00:00', '2025-11-01 09:00:00', true),
(@plant9, 'Tưới nước', 'Hàng ngày', '2025-10-28 17:00:00', '2025-10-29 17:00:00', true),
(@plant9, 'Bón phân', 'Mỗi 2 tuần', '2025-10-18 09:00:00', '2025-11-01 09:00:00', true),
(@plant10, 'Tưới nước', 'Mỗi 4 ngày', '2025-10-27 08:00:00', '2025-10-31 08:00:00', true),
(@plant11, 'Tưới nước', 'Mỗi 3 ngày', '2025-10-28 08:00:00', '2025-10-31 08:00:00', true),
(@plant11, 'Cắt tỉa', 'Mỗi 2 tuần', '2025-10-20 10:00:00', '2025-11-03 10:00:00', true),
(@plant12, 'Tưới nước', 'Hàng ngày', '2025-10-28 08:00:00', '2025-10-29 08:00:00', true),
(@plant13, 'Tưới nước', 'Mỗi 10 ngày', '2025-10-22 08:00:00', '2025-11-01 08:00:00', true),
(@plant14, 'Tưới nước', 'Mỗi 2 ngày', '2025-10-28 08:00:00', '2025-10-30 08:00:00', true),
(@plant15, 'Tưới nước', 'Mỗi 5 ngày', '2025-10-26 08:00:00', '2025-10-31 08:00:00', true),
(@plant15, 'Bón phân', 'Mỗi tháng', '2025-10-05 09:00:00', '2025-11-05 09:00:00', true),
(@plant16, 'Tưới nước', 'Mỗi 7 ngày', '2025-10-24 08:00:00', '2025-10-31 08:00:00', true),
(@plant16, 'Thu hoạch', 'Khi cần', '2025-10-10 10:00:00', NULL, true),
(@plant17, 'Tưới nước', 'Hàng ngày', '2025-10-28 08:00:00', '2025-10-29 08:00:00', true),
(@plant17, 'Thu hoạch', 'Mỗi tuần', '2025-10-25 10:00:00', '2025-11-01 10:00:00', true),
(@plant18, 'Tưới nước', 'Mỗi 2 ngày', '2025-10-28 07:00:00', '2025-10-30 07:00:00', true),
(@plant19, 'Tưới nước', 'Hàng ngày', '2025-10-28 08:00:00', '2025-10-29 08:00:00', true),
(@plant19, 'Phun sương', 'Hàng ngày', '2025-10-28 08:00:00', '2025-10-29 08:00:00', true),
(@plant20, 'Tưới nước', 'Mỗi 3 ngày', '2025-10-27 08:00:00', '2025-10-30 08:00:00', true),
(@plant21, 'Tưới nước', 'Mỗi 4 ngày', '2025-10-26 08:00:00', '2025-10-30 08:00:00', true),
(@plant21, 'Bón phân', 'Mỗi 2 tuần', '2025-10-18 09:00:00', '2025-11-01 09:00:00', true),
(@plant22, 'Tưới nước', 'Mỗi 5 ngày', '2025-10-25 08:00:00', '2025-10-30 08:00:00', true),
(@plant23, 'Tưới nước', 'Mỗi 2 ngày', '2025-10-28 08:00:00', '2025-10-30 08:00:00', true),
(@plant24, 'Tưới nước', 'Hàng ngày', '2025-10-28 08:00:00', '2025-10-29 08:00:00', true),
(@plant25, 'Tưới nước', 'Hàng ngày', '2025-10-28 08:00:00', '2025-10-29 08:00:00', true),
(@plant25, 'Thu hoạch', 'Mỗi tuần', '2025-10-24 10:00:00', '2025-10-31 10:00:00', true);

-- 5. Thêm Plant Reports (dữ liệu từ 6 tháng trước)
INSERT INTO plant_reports (plant_id, user_id, date, note, health_status, image_url) VALUES
-- ========== THÁNG 5/2025 ==========
(@plant6, @user_id, '2025-05-05', 'Cây mới trồng, đang thích nghi', 'Good', NULL),
(@plant6, @user_id, '2025-05-20', 'Lá bắt đầu xanh tốt', 'Excellent', NULL),
(@plant8, @user_id, '2025-05-10', 'Kim tiền mới về, trạng thái tốt', 'Good', NULL),
(@plant10, @user_id, '2025-05-12', 'Phát tài đang phát triển', 'Good', NULL),
(@plant15, @user_id, '2025-05-18', 'Phong lan ra nụ mới', 'Good', NULL),
(@plant20, @user_id, '2025-05-25', 'Vạn niên thanh lá đỏ đẹp', 'Excellent', NULL),

-- ========== THÁNG 6/2025 ==========
(@plant6, @user_id, '2025-06-10', 'Phát triển ổn định', 'Good', NULL),
(@plant1, @user_id, '2025-06-15', 'Cây mới về, đang làm quen môi trường', 'Fair', NULL),
(@plant8, @user_id, '2025-06-08', 'Kim tiền lá xanh bóng', 'Excellent', NULL),
(@plant10, @user_id, '2025-06-12', 'Phát tài lá to ra', 'Good', NULL),
(@plant15, @user_id, '2025-06-20', 'Phong lan cần tưới nước', 'Fair', NULL),
(@plant19, @user_id, '2025-06-18', 'Dương xỉ lá vàng một chút', 'Warning', NULL),
(@plant20, @user_id, '2025-06-22', 'Vạn niên thanh khỏe mạnh', 'Good', NULL),

-- ========== THÁNG 7/2025 ==========
(@plant6, @user_id, '2025-07-05', 'Lá vàng một chút, cần kiểm tra', 'Warning', NULL),
(@plant6, @user_id, '2025-07-20', 'Đã phục hồi sau khi bón phân', 'Good', NULL),
(@plant1, @user_id, '2025-07-25', 'Cây đã ổn định, lá xanh tốt', 'Good', NULL),
(@plant8, @user_id, '2025-07-10', 'Kim tiền phát triển chậm', 'Fair', NULL),
(@plant10, @user_id, '2025-07-15', 'Phát tài rất khỏe', 'Excellent', NULL),
(@plant11, @user_id, '2025-07-08', 'Trầu bà leo tốt', 'Good', NULL),
(@plant15, @user_id, '2025-07-18', 'Phong lan đã khỏe lại', 'Good', NULL),
(@plant19, @user_id, '2025-07-12', 'Dương xỉ phục hồi', 'Good', NULL),
(@plant22, @user_id, '2025-07-20', 'Cọ nhật mới về', 'Good', NULL),

-- ========== THÁNG 8/2025 ==========
(@plant1, @user_id, '2025-08-10', 'Phát triển tốt, nhiều lá non', 'Excellent', NULL),
(@plant6, @user_id, '2025-08-15', 'Cây khỏe mạnh', 'Good', NULL),
(@plant7, @user_id, '2025-08-28', 'Lan mới về, trạng thái tốt', 'Good', NULL),
(@plant8, @user_id, '2025-08-12', 'Kim tiền lại khỏe', 'Good', NULL),
(@plant10, @user_id, '2025-08-18', 'Phát tài tiếp tục tốt', 'Good', NULL),
(@plant11, @user_id, '2025-08-05', 'Trầu bà cần cắt tỉa', 'Good', NULL),
(@plant11, @user_id, '2025-08-22', 'Đã cắt tỉa xong', 'Excellent', NULL),
(@plant14, @user_id, '2025-08-20', 'Ngọc ngân lá đẹp', 'Good', NULL),
(@plant18, @user_id, '2025-08-30', 'Đồng tiền nở hoa vàng', 'Excellent', NULL),
(@plant19, @user_id, '2025-08-25', 'Dương xỉ xanh tốt', 'Good', NULL),
(@plant20, @user_id, '2025-08-15', 'Vạn niên thanh lá đỏ thắm', 'Excellent', NULL),
(@plant22, @user_id, '2025-08-28', 'Cọ nhật ổn định', 'Good', NULL),

-- ========== THÁNG 9/2025 ==========
(@plant1, @user_id, '2025-09-05', 'Tiếp tục phát triển tốt', 'Good', NULL),
(@plant2, @user_id, '2025-09-03', 'Hoa hồng mới trồng, đang ra rễ', 'Fair', NULL),
(@plant2, @user_id, '2025-09-15', 'Đã thích nghi, bắt đầu nở nụ', 'Good', NULL),
(@plant2, @user_id, '2025-09-28', 'Hoa nở đẹp', 'Excellent', NULL),
(@plant3, @user_id, '2025-09-18', 'Lưỡi hổ khỏe mạnh', 'Excellent', NULL),
(@plant6, @user_id, '2025-09-22', 'Cây phát triển tốt', 'Good', NULL),
(@plant7, @user_id, '2025-09-25', 'Lan đang có nụ', 'Good', NULL),
(@plant8, @user_id, '2025-09-10', 'Kim tiền ổn định', 'Good', NULL),
(@plant9, @user_id, '2025-09-12', 'Cúc vàng mới trồng', 'Fair', NULL),
(@plant9, @user_id, '2025-09-25', 'Cúc đang nở', 'Good', NULL),
(@plant10, @user_id, '2025-09-08', 'Phát tài khỏe', 'Good', NULL),
(@plant11, @user_id, '2025-09-15', 'Trầu bà leo nhanh', 'Good', NULL),
(@plant12, @user_id, '2025-09-20', 'Dạ yến thảo mới về', 'Good', NULL),
(@plant14, @user_id, '2025-09-18', 'Ngọc ngân đẹp', 'Good', NULL),
(@plant16, @user_id, '2025-09-10', 'Lô hội lá dày', 'Excellent', NULL),
(@plant19, @user_id, '2025-09-22', 'Dương xỉ tươi tốt', 'Good', NULL),
(@plant20, @user_id, '2025-09-12', 'Vạn niên thanh khỏe', 'Good', NULL),
(@plant21, @user_id, '2025-09-25', 'Hoa sứ có nụ', 'Good', NULL),

-- ========== THÁNG 10/2025 ==========
(@plant1, @user_id, '2025-10-02', 'Sen đá rất khỏe', 'Excellent', NULL),
(@plant1, @user_id, '2025-10-20', 'Sen đá tiếp tục khỏe mạnh', 'Good', NULL),
(@plant2, @user_id, '2025-10-05', 'Hoa nở rực rỡ', 'Excellent', NULL),
(@plant2, @user_id, '2025-10-22', 'Hoa hồng nhiều nụ mới', 'Good', NULL),
(@plant3, @user_id, '2025-10-08', 'Lá xanh mượt', 'Good', NULL),
(@plant3, @user_id, '2025-10-27', 'Lưỡi hổ cần tưới nước', 'Fair', NULL),
(@plant4, @user_id, '2025-10-03', 'Tầm xuân mới trồng', 'Fair', NULL),
(@plant4, @user_id, '2025-10-12', 'Đang phát triển', 'Good', NULL),
(@plant4, @user_id, '2025-10-25', 'Tầm xuân nở hoa nhiều màu', 'Excellent', NULL),
(@plant5, @user_id, '2025-10-11', 'Bạc hà mới về', 'Good', NULL),
(@plant5, @user_id, '2025-10-25', 'Bạc hà phát triển nhanh', 'Excellent', NULL),
(@plant6, @user_id, '2025-10-15', 'Trúc vẫn xanh tốt', 'Good', NULL),
(@plant7, @user_id, '2025-10-18', 'Lan đã nở hoa đẹp', 'Excellent', NULL),
(@plant8, @user_id, '2025-10-08', 'Kim tiền khỏe mạnh', 'Good', NULL),
(@plant8, @user_id, '2025-10-24', 'Kim tiền có lá mới', 'Excellent', NULL),
(@plant9, @user_id, '2025-10-10', 'Cúc vàng nở rộ', 'Excellent', NULL),
(@plant9, @user_id, '2025-10-26', 'Cúc vẫn nở đẹp', 'Good', NULL),
(@plant10, @user_id, '2025-10-12', 'Phát tài lá to xanh', 'Excellent', NULL),
(@plant11, @user_id, '2025-10-15', 'Trầu bà leo đầy tường', 'Good', NULL),
(@plant12, @user_id, '2025-10-18', 'Dạ yến thảo nở tím', 'Excellent', NULL),
(@plant13, @user_id, '2025-10-08', 'Xương rồng mới về', 'Good', NULL),
(@plant13, @user_id, '2025-10-22', 'Xương rồng khỏe', 'Good', NULL),
(@plant14, @user_id, '2025-10-10', 'Ngọc ngân lá nhiều', 'Good', NULL),
(@plant15, @user_id, '2025-10-14', 'Phong lan nở hoa', 'Excellent', NULL),
(@plant16, @user_id, '2025-10-09', 'Lô hội lá dày', 'Good', NULL),
(@plant17, @user_id, '2025-10-15', 'Húng quế mới trồng', 'Fair', NULL),
(@plant17, @user_id, '2025-10-26', 'Húng quế thơm tốt', 'Good', NULL),
(@plant18, @user_id, '2025-10-12', 'Đồng tiền nở nhiều', 'Excellent', NULL),
(@plant19, @user_id, '2025-10-18', 'Dương xỉ xanh mướt', 'Good', NULL),
(@plant20, @user_id, '2025-10-20', 'Vạn niên thanh đẹp', 'Good', NULL),
(@plant21, @user_id, '2025-10-16', 'Hoa sứ nở trắng', 'Excellent', NULL),
(@plant22, @user_id, '2025-10-19', 'Cọ nhật khỏe', 'Good', NULL),
(@plant23, @user_id, '2025-10-10', 'Cỏ lạc mới về', 'Fair', NULL),
(@plant23, @user_id, '2025-10-24', 'Cỏ lạc xanh tốt', 'Good', NULL),
(@plant24, @user_id, '2025-10-17', 'Dừa cạn nở hồng', 'Good', NULL),
(@plant25, @user_id, '2025-10-16', 'Tía tô mới trồng', 'Fair', NULL),
(@plant25, @user_id, '2025-10-27', 'Tía tô lá tím đẹp', 'Good', NULL);

-- 6. Thêm Plant Status (trạng thái hiện tại)
INSERT INTO plant_status (plant_id, status, description, imageurl) VALUES
(@plant1, 'Healthy', 'Sen đá khỏe mạnh, lá xanh tươi. Tưới lần cuối: 27/10/2025', NULL),
(@plant2, 'Healthy', 'Hoa hồng đang nở rộ, trạng thái xuất sắc. Bón phân lần cuối: 20/10/2025', NULL),
(@plant3, 'Needs Water', 'Lưỡi hổ cần tưới nước. Tưới lần cuối: 26/10/2025', NULL),
(@plant4, 'Healthy', 'Tầm xuân đang nở hoa nhiều màu, rất đẹp', NULL),
(@plant5, 'Healthy', 'Bạc hà rất tươi tốt, phát triển nhanh', NULL),
(@plant6, 'Healthy', 'Trúc nhật ổn định, lá xanh tốt', NULL),
(@plant7, 'Healthy', 'Lan hồ điệp đang nở hoa trắng tinh khôi', NULL),
(@plant8, 'Healthy', 'Kim tiền có lá mới, khỏe mạnh. Bón phân: 01/10/2025', NULL),
(@plant9, 'Healthy', 'Cúc vàng đang nở rộ', NULL),
(@plant10, 'Healthy', 'Phát tài lá to, xanh mướt', NULL),
(@plant11, 'Healthy', 'Trầu bà leo tốt, đang leo đầy tường', NULL),
(@plant12, 'Healthy', 'Dạ yến thảo nở tím rực rỡ', NULL),
(@plant13, 'Healthy', 'Xương rồng khỏe mạnh, ít cần chăm sóc', NULL),
(@plant14, 'Healthy', 'Ngọc ngân lá nhiều, xanh tốt', NULL),
(@plant15, 'Healthy', 'Phong lan đang nở hoa đẹp', NULL),
(@plant16, 'Healthy', 'Lô hội lá dày, có thể thu hoạch', NULL),
(@plant17, 'Healthy', 'Húng quế thơm tốt, sẵn sàng thu hoạch', NULL),
(@plant18, 'Healthy', 'Đồng tiền nở nhiều hoa vàng', NULL),
(@plant19, 'Healthy', 'Dương xỉ xanh mướt, ưa bóng mát', NULL),
(@plant20, 'Healthy', 'Vạn niên thanh lá đỏ đẹp', NULL),
(@plant21, 'Healthy', 'Hoa sứ nở trắng tinh khôi, thơm nhẹ', NULL),
(@plant22, 'Healthy', 'Cọ nhật khỏe mạnh, để bàn làm việc', NULL),
(@plant23, 'Healthy', 'Cỏ lạc xanh tốt, phát triển ổn định', NULL),
(@plant24, 'Healthy', 'Dừa cạn nở hồng đẹp', NULL),
(@plant25, 'Healthy', 'Tía tô lá tím, thơm, sẵn sàng thu hoạch', NULL);

-- ===================================
-- HOÀN TẤT! 
-- Đã thêm:
-- - 25 cây mới với các loại khác nhau
-- - 38 lịch chăm sóc chi tiết
-- - 93 báo cáo qua 6 tháng (để có dữ liệu cho biểu đồ xu hướng)
-- - 25 trạng thái hiện tại của cây
-- ===================================

SELECT 'Đã thêm dữ liệu mẫu thành công!' AS Result;
SELECT COUNT(*) AS 'Tổng số cây' FROM plants;
SELECT COUNT(*) AS 'Tổng số lịch chăm sóc' FROM care_schedules;
SELECT COUNT(*) AS 'Tổng số báo cáo' FROM plant_reports;
SELECT COUNT(*) AS 'Tổng số trạng thái' FROM plant_status;
