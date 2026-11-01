# Xperia KitKat Icons 📱✨

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://android.com)
[![SDK](https://img.shields.io/badge/Target%20SDK-36-blue.svg)](https://developer.android.com)
[![Icons Count](https://img.shields.io/badge/Icons-595%2B-orange.svg)](#features)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE.md)

A nostalgic icon pack bringing the classic design of **Sony Xperia Z-series** and **Android 4.4 KitKat** to modern Android smartphones. Built on the open-source **CandyBar Material Dashboard**, this icon pack seamlessly integrates with modern third-party launchers such as **Nova Launcher**, **Lawnchair**, **Niagara**, and **Smart Launcher**.

---

## 🌟 Features

- **595 High-Definition Icons**: Standardized to **192×192 px** (`xxxhdpi`) with 32-bit RGBA crisp transparency.
- **Alternative Icon Variations ("Alts")**: Multiple icon styles and retro colorways for core apps:
  - **Walkman / Sony Music**: Blue, Red, Purple, Crystal, Orange, Violet, Silver, and classic Walkman 'W' styles.
  - **YouTube & Media**: 11 unique variations including classic retro TV, KitKat, and modern styles.
  - **WhatsApp & Social**: Classic, glassy, retro, and flat variants for WhatsApp, Instagram, Twitter, Telegram, and Reddit.
  - **System Apps**: Multiple variations for Camera, Phone, Browser, Settings, Calculator, Calendar, and Clock.
- **CandyBar Material Dashboard**:
  - One-tap launcher application.
  - Categorized icon catalog and real-time search.
  - In-app icon request tool.
  - Dynamic calendar support.
- **Adaptive App Icon**: Custom adaptive launcher icon compatible with Circle, Squircle, Rounded Square, and Pebble mask shapes.

---

## 🚀 Supported Launchers

| Launcher | Quick Apply | Manual Icon Picker ("Alts") |
| :--- | :---: | :---: |
| **Nova Launcher** | ✅ | ✅ (Tap icon in Edit menu) |
| **Lawnchair** | ✅ | ✅ (Tap icon in Edit menu) |
| **Smart Launcher** | ✅ | ✅ |
| **Niagara Launcher** | ✅ | ✅ |
| **Apex Launcher** | ✅ | ✅ |
| **Action Launcher** | ✅ | ✅ |
| **OnePlus Launcher** | ✅ | ✅ |
| **Hyperion Launcher** | ✅ | ✅ |
| **Microsoft Launcher** | ✅ | ✅ |
| **ADW Launcher** | ✅ | ✅ |
| **Go Launcher** | ✅ | ✅ |
| **Samsung One UI** *(via Theme Park)* | ✅ | ✅ |

---

## 📥 Installation

1. Go to the [**Releases**](https://github.com/Yash-Kashyap/xperia_Kitkat_Icons/releases) tab.
2. Download the latest **`Xperia_KitKat_Icons_v1.0.apk`**.
3. Tap the file on your Android device to install (allow *"Install unknown apps"* if prompted).
4. Open the app and tap **Apply**, or apply it through your launcher's settings.

### How to Choose Alternative Icons:
1. Long-press any app icon on your home screen or app drawer.
2. Tap **Edit** (or pencil icon).
3. Tap on the icon image.
4. Select **Xperia KitKat**.
5. Pick any alternative variant or retro colorway!

---

## 🛠️ Building from Source

### Prerequisites:
- **JDK 17** or newer
- **Android SDK** (Build-Tools 36, Platform 36)

### Build Commands:
```bash
# Clone the repository
git clone https://github.com/Yash-Kashyap/xperia_Kitkat_Icons.git
cd xperia_Kitkat_Icons

# Build the debug APK
./gradlew assembleFossDebug

# Built APK output location:
# app/build/outputs/apk/foss/debug/app-foss-debug.apk
```

---

## 📂 Project Architecture

```text
xperia-kitkat-icon-pack/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml       # Launcher intent-filter bindings
│   │   │   ├── assets/
│   │   │   │   ├── appfilter.xml         # Component to drawable mapping
│   │   │   │   └── drawable.xml          # Categorized icon directory
│   │   │   └── res/
│   │   │       ├── drawable-nodpi/       # 595 crisp 192x192 PNG icons
│   │   │       ├── mipmap-*/             # Adaptive & density launcher icons
│   │   │       └── xml/                  # Configuration & shortcuts
│   │   └── foss/
│   │       └── java/                     # CandyBar Application & Activity
├── build.gradle                          # AGP 9.2 + SDK 36 config
└── README.md
```

---

## 👤 Author & Maintainer

- **Yash Kashyap**
- Email: [yashkashyap1110@gmail.com](mailto:yashkashyap1110@gmail.com)
- GitHub: [@Yash-Kashyap](https://github.com/Yash-Kashyap)

---

## 📄 License

This project is licensed under the Apache 2.0 License - see the [LICENSE.md](LICENSE.md) file for details.
