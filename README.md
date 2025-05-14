# FFmpeg-Based Logo Overlay App for Photos & Videos

An Android application that allows users to overlay a custom logo or watermark onto images and videos using the FFmpeg multimedia framework.

## Features

- Add a static logo or watermark to any photo.
- Overlay a logo onto videos at a chosen position.
- Support for multiple video formats (e.g., MP4, AVI, MKV).
- Real-time feedback during processing.
- Lightweight and easy-to-use interface.

## Technologies Used

- **Android SDK**
- **Java**
- **FFmpeg** (via prebuilt binaries or mobile-ffmpeg)
- **XML** for UI design

## How It Works

1. The user selects a photo or video from the device storage.
2. It uses selected logo image defined in code and will support user selects or uploads a logo image.
3. The app uses FFmpeg to overlay the logo onto the selected media.
4. The output file is saved to a designated folder on the device.

## FFmpeg Command Example

```bash
ffmpeg -i input.mp4 -i logo.png -filter_complex "overlay=10:10" output.mp4
```

This command overlays `logo.png` on `input.mp4` at the top-left corner (10px from both top and left).

## Installation

1. Clone the repository.
2. Open in Android Studio.
3. Add the FFmpeg library dependency.
4. Build and run on an emulator or physical device.

## Use Cases

- Branding media content with logos.
- Creating watermarked previews for video/photo content.
- Simple video editing for social media or promotional content.

## Future Improvements

- Let users choose logo position and opacity.
- Add batch processing for multiple files.
- Include drag-and-drop positioning in UI.

## License

This project is licensed under the MIT License. See `LICENSE` for details.
