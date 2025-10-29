# Fix: Hiển thị trạng thái cây thực tế trong phần Phân tích

## 🐛 Vấn đề

Trong tab "Phân tích cây trồng", cột **Trạng thái** đang hiển thị giá trị **hardcoded** là "Khỏe mạnh" cho tất cả các cây:

```javascript
// ❌ TRƯỚC KHI SỬA
<Chip
  label="Khỏe mạnh" // ← Hardcoded!
  size="small"
  color="success"
/>
```

## ✅ Giải pháp

### 1. Thêm state để lưu trạng thái cây

```javascript
const [plantStatuses, setPlantStatuses] = useState({});
```

### 2. Import API PlantStatus

```javascript
import plantStatusApi from "../../api/plantStatus";
```

### 3. Load trạng thái mới nhất cho mỗi cây

```javascript
const loadReportsData = async () => {
  // ... load plants ...

  // Load latest status for each plant
  const statusesMap = {};
  await Promise.all(
    plantsData.map(async (plant) => {
      try {
        const statusRes = await plantStatusApi.getLatest(plant.id);
        statusesMap[plant.id] = statusRes.data;
      } catch (err) {
        console.log(`No status for plant ${plant.id}`);
        statusesMap[plant.id] = null;
      }
    })
  );
  setPlantStatuses(statusesMap);
};
```

### 4. Hiển thị trạng thái thực với logic màu sắc

```javascript
{
  (() => {
    const status = plantStatuses[plant.id];
    if (!status) {
      return <Chip label="Chưa có dữ liệu" size="small" color="default" />;
    }

    const statusDisplay = status.status || "Không xác định";
    const getStatusColor = () => {
      const s = statusDisplay.toLowerCase();
      if (
        s.includes("khỏe") ||
        s.includes("tốt") ||
        s.includes("healthy") ||
        s.includes("good")
      )
        return "success";
      if (
        s.includes("cảnh báo") ||
        s.includes("warning") ||
        s.includes("trung bình")
      )
        return "warning";
      if (
        s.includes("yếu") ||
        s.includes("bệnh") ||
        s.includes("sick") ||
        s.includes("critical")
      )
        return "error";
      return "default";
    };

    return <Chip label={statusDisplay} size="small" color={getStatusColor()} />;
  })();
}
```

## 🎨 Logic màu sắc Chip

| Trạng thái        | Màu       | Keywords                      |
| ----------------- | --------- | ----------------------------- |
| 🟢 Khỏe mạnh      | `success` | khỏe, tốt, healthy, good      |
| 🟡 Cảnh báo       | `warning` | cảnh báo, warning, trung bình |
| 🔴 Yếu/Bệnh       | `error`   | yếu, bệnh, sick, critical     |
| ⚪ Không xác định | `default` | Các trạng thái khác           |

## 📡 API Backend sử dụng

```
GET /api/plant-status/plant/{plantId}/latest
```

Trả về:

```json
{
  "id": 1,
  "status": "Khỏe mạnh",
  "description": "Cây đang phát triển tốt",
  "imageurl": "...",
  "updateAt": "2025-10-29T10:30:00",
  "plants": { ... }
}
```

## 🔄 Luồng dữ liệu

1. Component mount → `useEffect` → `loadReportsData()`
2. Load danh sách plants từ API
3. **Song song** load latest status cho mỗi plant
4. Lưu vào `plantStatuses` object với key là `plantId`
5. Render bảng với trạng thái thực từ state

## ⚠️ Xử lý edge cases

- ✅ **Cây chưa có trạng thái:** Hiển thị "Chưa có dữ liệu" với màu mặc định
- ✅ **API lỗi:** Catch error và set `null`, hiển thị "Chưa có dữ liệu"
- ✅ **Status null/undefined:** Fallback sang "Không xác định"
- ✅ **Hỗ trợ đa ngôn ngữ:** Keywords tiếng Việt và tiếng Anh

## 📂 Files thay đổi

- `frontend/src/layouts/reports/index.js`
  - Line 55: Import `plantStatusApi`
  - Line 77: Add `plantStatuses` state
  - Line 102-130: Load plant statuses trong `loadReportsData`
  - Line 795-825: Render dynamic status chip

## 🧪 Testing

### Test Cases:

1. ✅ Cây có trạng thái "Khỏe mạnh" → Chip xanh
2. ✅ Cây có trạng thái "Cảnh báo" → Chip vàng
3. ✅ Cây có trạng thái "Bệnh" → Chip đỏ
4. ✅ Cây chưa có trạng thái → Chip xám "Chưa có dữ liệu"
5. ✅ Load nhiều cây cùng lúc → Promise.all
6. ✅ Refresh data → Trạng thái cập nhật

### Cách test:

```bash
# 1. Đảm bảo backend đang chạy
lsof -ti:8081

# 2. Mở frontend
# 3. Vào tab "Phân tích cây trồng" (tab thứ 3)
# 4. Xem cột "Trạng thái"
# 5. Verify màu sắc hiển thị đúng
```

---

**Ngày sửa:** 29/10/2025
**Status:** ✅ Completed
**Impact:** High - Hiển thị dữ liệu thực thay vì hardcoded
