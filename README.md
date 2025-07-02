# PhotoFilter — Lightweight Offline Photo‑Editing for Android

## Product Description

PhotoFilter is a **100 % offline** Android app that lets users load or capture an image and instantly apply one of twelve handcrafted photo filters. No accounts, no cloud processing—just open, tweak, and save. Perfect for travelers with spotty connectivity, privacy‑conscious creators, or anyone who wants quick, beautiful edits without bloat.

---

## Feature Highlights

| Core Feature          | Details                                                                                                                                                                        |
| --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Import & Capture**  | • Pick from device gallery via system picker<br>• Snap new photo using *CameraX*                                                                                               |
| **12 Custom Filters** | Vintage, Warm Sunset, Cool Breeze, Black‑&‑White Classic, Cinematic Teal‑Orange, High‑Contrast Mono, Pastel Dream, Retro Film, HDR Pop, Sepia Fade, Cyberpunk Neon, Soft Matte |
| **Real‑Time Preview** | GPU‑accelerated preview with a slider to compare *Before* ↔ *After*                                                                                                            |
| **Adjust & Save**     | Intensity slider (0‑100 %), save to Photos/PhotoFilter folder (JPEG/PNG)                                                                                                       |
| **Completely Local**  | No Internet permission; all processing done on‑device                                                                                                                          |

---

## Technical Overview

| Topic              | Choice                                                  |
| ------------------ | ------------------------------------------------------- |
| **Language**       | Kotlin 1.10, Coroutines                                 |
| **Min SDK**        | 24 (Android 7.0)                                        |
| **UI**             | *Jetpack Compose* + Material 3                          |
| **Image Pipeline** | Android *GPUImage* (OpenGL ES) for shader‑based filters |
| **Architecture**   | MVVM • Hilt DI • Single‑activity pattern                |
| **Persistence**    | MediaStore + DataStore (settings)                       |
| **Testing**        | JUnit5, Turbine, Compose UI tests                       |

---

## Project Structure

```
photo-filter/
 ├─ app/
 │   └─ src/main/
 │       ├─ java/com/example/photofilter/
 │       │   ├─ ui/            ← Compose screens & components
 │       │   ├─ di/            ← Hilt modules
 │       │   ├─ domain/        ← Use‑cases & models
 │       │   ├─ data/          ← Repositories, filter shaders
 │       │   └─ util/          ← Helpers & extensions
 │       └─ res/
 │           ├─ drawable/
 │           └─ values/
 └─ build.gradle.kts
```

---

## Getting Started

1. **Clone** the repo

   ```bash
   git clone https://github.com/your‑org/photo‑filter.git
   ```
2. **Open** in Android Studio *Iguana* (or newer).
3. Ensure **Android SDK 34**, **NDK 26**, and **JDK 17** are installed.
4. **Run** on a physical device or emulator:

   ```bash
   ./gradlew installDebug
   ```

> **Tip:** Because the app requests only *CAMERA* and *READ/WRITE\_MEDIA\_IMAGES* runtime permissions, make sure to grant them on first launch.

---

## Development & Contribution

* **Issue tracking:** GitHub Issues.
* **Code style:** `ktlint` & `detekt` (run `./gradlew ktlintFormat detekt`).
* **Pull requests:** Fork → feature branch → PR to `main` with a clear description.
* All new code must have unit/UI tests and pass CI.

---

## Roadmap

| Release  | Focus                                      |
| -------- | ------------------------------------------ |
| **v1.0** | Core features above                        |
| **v1.1** | Crop & rotate tools • EXIF preservation    |
| **v1.2** | Batch processing • Share sheet integration |
| **v2.0** | Plug‑in filter SDK for community presets   |

---

## License

```text
Apache License 2.0
Copyright © 2025 Your Name or Organization
```

---

### Maintainers

* **@tigran‑ghazinyan** — creator & lead dev

Feel free to open an issue if you hit any snags or have feature ideas! 🎨
