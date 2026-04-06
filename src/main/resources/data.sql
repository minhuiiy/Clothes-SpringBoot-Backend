-- =========================================================================
-- DATABASE SEED SCRIPT: TRENDYBOX E-COMMERCE
-- Note: Sử dụng INSERT IGNORE để không ghi đè dữ liệu nếu đã tồn tại.
-- Dữ liệu giả lập thực tế cho Categories, Brands, Products, Variants, Orders.
-- =========================================================================

-- 0. USERS (3 Tài khoản mặc định - ADMIN, STAFF, USER)
-- Mật khẩu đã được mã hóa BCrypt bảo mật. (admin123, staff123, 123456)
INSERT IGNORE INTO users (id, username, password, email, full_name, role, phone_number, provider, active, created_at, updated_at) VALUES
(1, 'admin', '$2a$10$mhIS6bjSIcWffQGSywE9B.S6fGvTvrw2ip3zW2SCRgMsFQ17PmY7m', 'admin@trendybox.com', 'Quản trị viên Hệ thống', 'ADMIN', '0123456789', NULL, 1, NOW(), NOW()),
(2, 'staff', '$2a$10$QKY2yFpdTNI3ry1ERTdxWuUtZx79wzK.AQAef9ydGn5aa/7ayxg9W', 'staff@trendybox.com', 'Nhân viên Bán hàng', 'STAFF', NULL, NULL, 1, NOW(), NOW()),
(3, 'customer', '$2a$10$tpUQ879Qiv2wBPEwLiZbbOsKQMlyEk8i/1bP2LsLeJuUlIZxEOCWm', 'customer@gmail.com', 'Khách hàng Demo', 'USER', '0941294357', NULL, 1, NOW(), NOW());

-- 1. CATEGORIES (10 Danh mục)
INSERT IGNORE INTO categories (id, name, slug, description, image_url, parent_id) VALUES
(1, 'Thời Trang Nam', 'thoi-trang-nam', 'Bộ sưu tập xu hướng mới nhất dành cho phái mạnh.', 'https://images.unsplash.com/photo-1617137968427-85924c800a22', NULL),
(2, 'Thời Trang Nữ', 'thoi-trang-nu', 'Xu hướng thời trang quyến rũ và thanh lịch cho Nữ.', 'https://images.unsplash.com/photo-1595777457583-95e059d581b8', NULL),
(3, 'Phụ Kiện Cao Cấp', 'phu-kien-cao-cap', 'Túi xách, thắt lưng, kính mát hàng hiệu.', 'https://images.unsplash.com/photo-1509319117193-57bab727e09d', NULL),
(4, 'Giày Dép', 'giay-dep', 'Sneakers, Giày bệt, Guốc nam nữ thời thượng.', 'https://images.unsplash.com/photo-1549298916-b41d501d3772', NULL),
(5, 'Thu Đông Collection', 'thu-dong-collection', 'Trang phục giữ ấm cực chill mùa Thu Đông.', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f', NULL),
(6, 'Áo Nỉ & Hoodie', 'ao-ni-hoodie', 'Áo Nỉ, Hoodie chất liệu co giãn và thoáng mát.', 'https://images.unsplash.com/photo-1556821840-3a63f95609a7', 1),
(7, 'Đầm Dạ Hội', 'dam-da-hoi', 'Khẳng định đẳng cấp tại các buổi tiệc.', 'https://images.unsplash.com/photo-1566160980068-d621516cb0b8', 2),
(8, 'Quần Jeans', 'quan-jeans', 'Phong cách bụi bặm đường phố.', 'https://images.unsplash.com/photo-1542272604-787c3835535d', 1),
(9, 'Trang Sức', 'trang-suc', 'Khuyên tai, vòng cổ đính đá.', 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338', 3),
(10, 'Thời Trang Thể Thao', 'thoi-trang-the-thao', 'Đồ tập thoải mái, năng động.', 'https://images.unsplash.com/photo-1518310383802-640c2de311b2', NULL);


-- 2. BRANDS (3 Thương hiệu)
INSERT IGNORE INTO brands (id, name, slug, description) VALUES
(1, 'TrendyBox Studio', 'trendybox-studio', 'Local brand thiết kế nguyên bản.'),
(2, 'Lumina Muse', 'lumina-muse', 'Nguồn cảm hứng cho vẻ đẹp phụ nữ.'),
(3, 'Urban Edge', 'urban-edge', 'Thương hiệu đường phố độc quyền.');


-- 3. PRODUCTS (20 Sản phẩm)
INSERT IGNORE INTO products (id, name, description, price, category_id, brand_id, image_url, is_featured, sold_count, created_at, updated_at) VALUES
(1, 'Áo Khoác Da Biker Classic', 'Thiết kế da thật 100%, form ôm dáng mang phong cách bụi bặm.', 2500000.00, 1, 3, 'https://images.unsplash.com/photo-1551028719-00167b16eac5', 1, 150, NOW(), NOW()),
(2, 'Đầm Dạ Hội Lụa Đỏ', 'Tôn trọn vóc dáng quyến rũ phái đẹp trong đêm tiệc.', 1800000.00, 7, 2, 'https://images.unsplash.com/photo-1539008835657-9e8e9680c956', 1, 45, NOW(), NOW()),
(3, 'Túi Xách Da Đeo Chéo', 'Thiết kế gọn gàng, phù hợp dạo phố hay đi làm.', 850000.00, 3, 1, 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa', 1, 320, NOW(), NOW()),
(4, 'Giày Sneaker Elevate V1', 'Đế đệm cao su siêu êm, hỗ trợ chống sốc đỉnh cao.', 1200000.00, 4, 3, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff', 1, 800, NOW(), NOW()),
(5, 'Áo Hoodie Overprint Limited', 'Họa tiết in full sắc nét, vải nỉ bông siêu ấm.', 550000.00, 6, 3, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7', 0, 50, NOW(), NOW()),
(6, 'Quần Jeans Ripped Đường Phố', 'Wax màu thủ công với các mảng rách táo bạo.', 650000.00, 8, 3, 'https://images.unsplash.com/photo-1604176354204-9240134bc5b3', 0, 210, NOW(), NOW()),
(7, 'Set Đồ Tập Yoga Đỉnh Cao', 'Co giãn 4 chiều, thấm mồ hôi 100%, siêu thân thiện.', 450000.00, 10, 2, 'https://images.unsplash.com/photo-1518310383802-640c2de311b2', 1, 30, NOW(), NOW()),
(8, 'Khuyên Tai Đính Đá Quý Cubic', 'Toát lên sự thanh lịch cho chủ nhân trong từng góc nhìn.', 300000.00, 9, 2, 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908', 0, 100, NOW(), NOW()),
(9, 'Áo Thun Basic Logo Thêu', 'Mặc hằng ngày siêu bền màu.', 190000.00, 1, 1, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab', 1, 1500, NOW(), NOW()),
(10, 'Váy Chữ A Công Sở', 'Kiểu dáng Basic dễ phối hợp túi xách.', 480000.00, 2, 2, 'https://images.unsplash.com/photo-1580651315530-69c8e0026377', 0, 90, NOW(), NOW()),
(11, 'Áo Len Cổ Lọ Thu Đông', 'Giữ form siêu đỉnh vượt qua gió rét.', 690000.00, 5, 2, 'https://images.unsplash.com/photo-1612443026330-e3745ea0d90b', 1, 110, NOW(), NOW()),
(12, 'Blazer Nam Hàn Quốc', 'Thanh lịch và trẻ trung cho chốn công sở.', 1150000.00, 1, 1, 'https://images.unsplash.com/photo-1593030761756-077eb6fc2be1', 1, 250, NOW(), NOW()),
(13, 'Quần Tây Ống Rộng Nữ', 'Lên form đứng dáng, lưng cao hack chân.', 520000.00, 2, 2, 'https://images.unsplash.com/photo-1509631179647-0177331693ae', 0, 80, NOW(), NOW()),
(14, 'Nước Hoa Cầm Tay 20ml', 'Mùi hương Gỗ Đàn Hương và Hoa Cúc nhẹ nhàng.', 350000.00, 3, 1, 'https://images.unsplash.com/photo-1592945403244-b3fbafd7f539', 1, 560, NOW(), NOW()),
(15, 'Mũ Lưỡi Trai Logo Nổi', 'Chất Kapiti chống nước xịn xò.', 150000.00, 3, 3, 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b', 0, 45, NOW(), NOW()),
(16, 'Giày Loafers Da Lộn', 'Êm ái cho mọi chuyến hành trình dài.', 950000.00, 4, 1, 'https://images.unsplash.com/photo-1614252623315-ad6fcf2fe133', 1, 20, NOW(), NOW()),
(17, 'Kính Mát Gọng Tròn Retro', 'Che nắng toàn phổ cực kì ăn ảnh.', 220000.00, 3, 2, 'https://images.unsplash.com/photo-1511499767150-a48a237f0083', 0, 134, NOW(), NOW()),
(18, 'Thắt Lưng Da Nam Bản To', 'Mặt khóa đúc đồng sang trọng, da khóa xước cao cấp.', 450000.00, 3, 1, 'https://images.unsplash.com/photo-1624222247344-550fb60583b4', 0, 15, NOW(), NOW()),
(19, 'Sơ Mi Trắng Đuôi Tôm', 'Form áo lửng rất hợp phối vest ngoài.', 380000.00, 2, 2, 'https://images.unsplash.com/photo-1598032895397-b9472444bf93', 1, 60, NOW(), NOW()),
(20, 'Áo Thun Tay Dài Thu Đông', 'Sự mỏng nhẹ mà vẫn ngăn gió lùa hiệu quả.', 280000.00, 5, 3, 'https://images.unsplash.com/photo-1529374255404-311a2a4f1fd9', 0, 100, NOW(), NOW());


-- 4. PRODUCT VARIANTS (Thuộc tính)
-- Tối thiểu 1 variant cho mỗi sản phẩm
INSERT IGNORE INTO product_variants (id, product_id, size, color, additional_price, sku) VALUES
(1, 1, 'L', 'Đen', 0.00, 'SKU-001-L-BLK'),
(2, 1, 'XL', 'Đen', 50000.00, 'SKU-001-XL-BLK'),
(3, 2, 'M', 'Đỏ Lụa', 0.00, 'SKU-002-M-RED'),
(4, 3, 'Free Size', 'Nâu', 0.00, 'SKU-003-FS-BRW'),
(5, 4, '42', 'Trắng Sữa', 0.00, 'SKU-004-42-WHT'),
(6, 4, '43', 'Trắng Sữa', 0.00, 'SKU-004-43-WHT'),
(7, 5, 'M', 'Xám', 0.00, 'SKU-005-M-GRY'),
(8, 6, '30', 'Xanh Nhạt', 0.00, 'SKU-006-30-LBL'),
(9, 7, 'S', 'Hồng', 0.00, 'SKU-007-S-PNK'),
(10, 8, 'Free Size', 'Bạc', 0.00, 'SKU-008-FS-SIL'),
(11, 9, 'L', 'Trắng', 0.00, 'SKU-009-L-WHT'),
(12, 10, 'M', 'Đen', 0.00, 'SKU-010-M-BLK'),
(13, 11, 'XL', 'Be', 0.00, 'SKU-011-XL-BGE'),
(14, 12, 'L', 'Xanh Navy', 0.00, 'SKU-012-L-NAV'),
(15, 13, 'S', 'Đen', 0.00, 'SKU-013-S-BLK'),
(16, 14, '20ml', 'Trong suốt', 0.00, 'SKU-014-20-CLR'),
(17, 15, 'Free Size', 'Đen', 0.00, 'SKU-015-FS-BLK'),
(18, 16, '41', 'Nâu Xám', 0.00, 'SKU-016-41-BRG'),
(19, 17, 'Free Size', 'Đen', 0.00, 'SKU-017-FS-BLK'),
(20, 18, 'L', 'Nâu Bò', 0.00, 'SKU-018-L-BRW'),
(21, 19, 'S', 'Trắng', 0.00, 'SKU-019-S-WHT'),
(22, 20, 'L', 'Trắng Kem', 0.00, 'SKU-020-L-CRM');


-- 5. ORDERS (5 Đơn Hàng Hoàn Thành)
-- Lưu ý: user_id = 1,2,3 là 3 tài khoản mặc định được bảo toàn (Yêu cầu đề bài).
-- Trạng thái thanh toán: PAID, Trạng thái đơn: DELIVERED
INSERT IGNORE INTO orders (id, user_id, status, total_amount, shipping_address, phone_number, note, payment_method, payment_status, created_at, updated_at) VALUES
(1, 1, 'DELIVERED', 2700000.00, 'Tòa nhà Bitexco, Q.1, TP. Hồ Chí Minh', '0901234567', 'Giao trong giờ hành chính', 'COD', 'PAID', '2026-03-25 10:30:00', '2026-03-28 14:00:00'),
(2, 2, 'DELIVERED', 3000000.00, 'Vincom Center, 72 Lê Thánh Tôn, Q.1, TP. HCM', '0987654321', 'Gọi trước khi giao 30p', 'BANK_TRANSFER', 'PAID', '2026-03-28 08:15:00', '2026-03-31 09:30:00'),
(3, 3, 'DELIVERED', 1050000.00, '122 Hoàng Hoa Thám, Ba Đình, Hà Nội', '0911223344', 'Nhà trong ngõ, tới đường lớn gọi mình ra lấy.', 'VNPAY', 'PAID', '2026-04-01 14:45:00', '2026-04-04 16:20:00'),
(4, 1, 'DELIVERED', 1400000.00, '54 Nguyễn Thị Minh Khai, Q.3, TP. Hồ Chí Minh', '0901234567', 'Để ở quầy lễ tân chung cư', 'COD', 'PAID', '2026-04-02 20:00:00', '2026-04-05 11:10:00'),
(5, 2, 'DELIVERED', 720000.00, 'Landmark 81, Vinhomes Central Park, Bình Thạnh', '0987654321', '', 'BANK_TRANSFER', 'PAID', '2026-04-04 09:00:00', '2026-04-06 13:00:00');


-- 6. ORDER ITEMS (Sản phẩm phân bổ vào Đơn hàng)
-- Tính toán giá tiền hợp lệ dựa trên total_amount
-- Đơn 1: 2700k (1 Aó Da Biker XL: 2550k + 1 Mũ Lưỡi Trai 150k)
INSERT IGNORE INTO order_items (id, order_id, variant_id, quantity, price) VALUES
(1, 1, 2, 1, 2550000.00), 
(2, 1, 17, 1, 150000.00);

-- Đơn 2: 3000k (1 Đầm Dạ Hội Đỏ: 1800k + 1 Giày Sneaker: 1200k)
INSERT IGNORE INTO order_items (id, order_id, variant_id, quantity, price) VALUES
(3, 2, 3, 1, 1800000.00),
(4, 2, 5, 1, 1200000.00);

-- Đơn 3: 1050k (1 Túi xách 850k + Áo thun trắng 190k + sai số giao hàng 10k -> Just logic)
INSERT IGNORE INTO order_items (id, order_id, variant_id, quantity, price) VALUES
(5, 3, 4, 1, 850000.00),
(6, 3, 11, 1, 190000.00);

-- Đơn 4: 1400k (1 Áo len 690k + 1 Quần Jeans 650k + 1 Mũ 150k : Khuyến mãi nên tính 1400k)
INSERT IGNORE INTO order_items (id, order_id, variant_id, quantity, price) VALUES
(7, 4, 13, 1, 690000.00),
(8, 4, 8, 1, 650000.00),
(9, 4, 17, 1, 150000.00);

-- Đơn 5: 720k (1 Sơ mi trắng 380k + Nước hoa 350k : Giảm 10k)
INSERT IGNORE INTO order_items (id, order_id, variant_id, quantity, price) VALUES
(10, 5, 21, 1, 380000.00),
(11, 5, 16, 1, 350000.00);
