# Quy Trình Quản Lý Git Cho Team TrendyBox 🚀

Để tránh tình trạng xung đột code (conflict) hoặc lỡ tay gộp nhầm nhánh giống vừa rồi, toàn bộ team **Frontend (FE)** và **Backend (BE)** cần tuân thủ quy trình quản lý Git thống nhất dưới đây. Bạn có thể gửi thẳng tài liệu này cho các thành viên trong nhóm đoạn dưới đây nhé.

---

## 1. Tổ Chức Nhánh (Git Branching Model)

Dự án hiện tại sử dụng 3 cấp độ nhánh cơ bản:
- **`main`**: Nhánh MẶC ĐỊNH (Production). Code ở đây phải **sạch 100% và chạy được**. Không ai được tự ý push thẳng vào nhánh này. Nó dùng để chạy tự động quy trình CI/CD.
- **`develop`**: Nhánh KIỂM THỬ (Integration). Giữ mã nguồn đang phát triển nhưng đã tương đối ổn định. Mọi người sẽ gom tính năng vào đây để chạy thử chung.
- **`feature/<tên-tính-năng>`**: Nhánh TÍNH NĂNG (Ví dụ: `feature/login`, `feature/cart-ui`). Nhánh dành riêng cho từng thành viên làm việc cá nhân.

---

## 2. Quy Trình Làm Việc Hàng Ngày (Cả FE & BE)

Bất kể bạn ở team FE làm repo Angular hay team BE làm repo SpringBoot, quy tắc đẩy code (Push) đều giống hệt nhau:

### BƯỚC 1: Lấy code mới nhất trước khi làm nghiệm vụ
Luôn luôn bắt đầu công việc từ nhánh `develop` sạch sẽ.
```bash
git checkout develop
git pull origin develop
```

### BƯỚC 2: Tạo nhánh riêng theo tính năng mình đang làm
Đừng bao giờ code trực tiếp trên nhánh gốc.
```bash
git checkout -b feature/ten-thu-ban-dang-lam
# Ví dụ FE: git checkout -b feature/login-ui
# Ví dụ BE: git checkout -b feature/product-api
```

### BƯỚC 3: Code và Commit
```bash
git add .
git commit -m "Thêm mô tả công việc (VD: feat: add login form)"
```

### BƯỚC 4: Đẩy nhánh tính năng của bạn lên mạng
```bash
git push -u origin feature/ten-thu-ban-dang-lam
```

### BƯỚC 5: Lên Github và Yêu Cầu Gộp Code (Pull Request - PR)
1. Lên trang web Github của Repository bạn đang làm (Frontend hoặc Backend).
2. Sẽ có nút xanh lá hiện ra **"Compare & pull request"** => Bấm vào đó.
3. Chỉnh đích đến (Base): **`develop`** (TUYỆT ĐỐI KHÔNG chọn `main`).
4. Ngỏ lời cho Team Leader/Người khác review.
5. Khi PR xanh lá và sếp duyệt => Nhấn **Merge pull request**.

---

## 3. Lưu Ý Riêng & Phân Chia Repo

> [!TIP]
> **Team Frontend (UI/UX)**
> - Các bạn không cần phải clone repo Backend về máy nặng máy.
> - Các bạn CHỈ CẦN clone kho Github Độc lập: `minhuiiy/Clothes-Angular-Frontend`.
> - Code và thực hiện đẩy code lên repo này y như Bước 2.

> [!WARNING]
> **Team Backend / Quản trị hệ thống**
> - Backend hiện đang ôm trọn Frontend ở dạng **Submodule** (Thư mục con `frontend` bên trong Backend).
> - Để lấy những thay đổi mới nhất mà Team UI/UX vừa Merge, BE làm như sau:
>   1. Mở Terminal, trỏ vào thư mục `frontend`
>   2. Chạy: `git pull origin develop` (kéo UI mới nhất về)
>   3. Trở ra thư mục Backend gốc (cd ..)
>   4. Chạy: `git add frontend` -> `git commit -m "chore: sync frontend code"` -> `git push`
> - Động tác này là để báo cho Repo Backend biết rằng: "À, Frontend vừa có bản mới, hãy liên kết với bản mới đó!".

---

## Khi Nào Merge Vào MAIN ?
Chỉ có **Lead Team** mới làm việc này. Cuối tuần (Sprint), khi mọi nhánh tính năng đã hội tụ đầy đủ trên `develop` và Test chạy mượt mà, Lead sẽ tạo 1 Pull Request từ nhánh `develop` sang `main` để chốt tính năng tuần đó! Hệ thống CI/CD sẽ tự đem Main đóng gói (Docker) lên Server.
