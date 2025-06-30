# 🐸 Frogie – Ứng dụng học tiếng Anh đơn giản & thú vị

[🎬 Xem video demo ngắn](https://youtube.com/shorts/oAWuKw4GTjQ?feature=share)

![image](https://github.com/user-attachments/assets/bcce16b7-e5e6-4b1b-8ce9-b242b33288c0)



**Frogie** là ứng dụng học tiếng Anh trên Android, tập trung vào việc luyện tập từ vựng thông qua các bài điền từ vào chỗ trống. Giao diện tối giản, hoạt động offline, phù hợp với người mới bắt đầu.

---

## ✨ Tính năng chính

* **Bài học & bài tập** chia theo chủ đề
* **Điền từ vào chỗ trống** – đơn giản, hiệu quả
* **Chuỗi học tập (streak)**: đếm số ngày học liên tục
* **Tài khoản offline**: đăng ký, đăng nhập, đổi tên, đổi mật khẩu
* **Theo dõi tiến trình**: lưu kết quả, điểm XP

---

## 🛠️ Công nghệ sử dụng

* **Ngôn ngữ:** Java
* **CSDL:** SQLite (offline)
* **Giao diện:** XML, Fragment, DialogFragment toàn màn hình
* **Không sử dụng:** Firebase, Jetpack Compose

---

## 📅 Luồng màn hình chính

* `SplashScreen` → `MainActivity`
* `MainActivity` chứa các Fragment:

  * `PersonFragment` → `SettingFragment`

    * `InfoFragment` → `UsernameFragment` / `PasswordFragment`
  * `StreakCalendarDialogFragment`: hiện popup toàn màn hình

---

## 📁 Cấu trúc thư mục

```
app/
└── src/
    └── main/
        ├── java/com/example/elearning/         # Mã nguồn chính
        ├── res/                                 # Tài nguyên giao diện
        │   ├── layout/                          # XML bố cục màn hình
        │   ├── drawable/                        # Icon, ảnh nền
        │   ├── menu/                            # Menu app
        │   ├── navigation/                      # Navigation graph
        │   ├── xml/                             # Cấu hình khác (e.g. preferences)
        │   └── values*/                         # Chủ đề, màu sắc, chuỗi đa ngôn ngữ
        └── AndroidManifest.xml                  # Cấu hình app

```

## 🚚 Định hướng tương lai

* Thêm chế độ chơi thử thách
* Nâng cấp hệ thống tính XP và cấp độ
* Tối ưu UI/UX cho người học nhỏ tuổi


---

## 📱 Yêu cầu hệ thống

- Android 5.0 (API 21) trở lên
- Không yêu cầu kết nối internet (offline support)

---

## 📥 Tải về và cài đặt

1. Clone repo:
   ```bash
   git clone https://github.com/your-username/frogie.git
2.Mở trong Android Studio

3.Build và chạy ứng dụng trên thiết bị hoặc trình giả lập

---

**Frogie** – Học từng chút, tiến bộ từng ngày. 🐸
