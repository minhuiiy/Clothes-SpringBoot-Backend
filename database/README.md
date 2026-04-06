# Database Migrations & Seeds - TrendyBox

Thư mục này chứa các kịch bản định nghĩa cấu trúc (Schema Migrations) và dữ liệu mẫu khởi tạo (Seed Data) dùng cho dự án TrendyBox.

## Cấu trúc thư mục

```text
database/
├── migrations/          # (Nơi lưu các file .sql Migration nếu mở rộng Flyway sau này)
├── seeds/               
│   └── seed_data.sql    # Dữ liệu 10 categories, 20 products, 5 completed orders
└── README.md            # Tài liệu hướng dẫn bạn đang đọc
```

## Cơ chế tự động hoá của Spring Boot

Hệ thống đã được lập trình để **tự động chèn dữ liệu Seed** ngay khi clone dự án về và khởi chạy mà không cần setup phức tạp:
- Mọi câu lệnh bên trong `seed_data.sql` (bản sao của `src/main/resources/data.sql`) đều dùng cơ chế `INSERT IGNORE`.
- Cấu trúc Migration bảo toàn **3 tài khoản gốc** (Passwords đã được giải thuật BCrypt băm `$2a$10$...`):
  1. **Admin**: `admin` / `admin123`
  2. **Staff**: `staff` / `staff123`
  3. **Customer**: `customer` / `123456`
- Chế độ tự động nạp được kích hoạt tại `application.properties`:
  ```properties
  spring.sql.init.mode=always
  spring.jpa.defer-datasource-initialization=true
  ```

> [!TIP]
> Việc dùng `INSERT IGNORE` luôn đảm bảo 3 tài khoản Users có sẵn, hay bất kỳ dữ liệu cũ nào của bạn không bị ghi đè, xóa hoặc thay đổi. Hệ thống chỉ bồi thêm dữ liệu Sản phẩm, Đơn hàng mẫu bị thiếu.

## Chạy Migration / Seed dữ liệu bằng tay (Thủ công)
Nếu trong quá trình test bạn lỡ tay xóa mất database và muốn kéo lại 100% data mẫu mới:
1. Bạn có thể mở công cụ quản trị MySQL (DBeaver, DataGrip, MySQL Workbench).
2. Copy toàn bộ code trong file `database/seeds/seed_data.sql`
3. Paste và Execute (Run) trực tiếp trên giao diện Query.

## Chi tiết dữ liệu mẫu (Seeded Data)
- **10 Danh mục**: Đa dạng từ Thời trang Nam, Nữ, Giày Dép, Phụ Kiện. (Liên kết Parent-Child logic).
- **20 Mẫu Sản phẩm & Biến thể**: Tên gọi sang trọng, Cấu trúc giá VND thực tế (Từ 150.000đ đến 2.500.000đ). Link hình ảnh từ Unsplash đúng chuẩn.
- **5 Đơn hàng**: Map thẳng vào `user_id` = 1, 2, 3. Trạng thái giao hàng là `DELIVERED`, có note, ghi chú và địa chỉ cực kì Việt Nam (Landmark 81, Bitexco, Q.1...). Đơn được chèn kèm theo các giỏ hàng con `order_items` tương ứng với biến thể sản phẩm.
