# Android 客户端

当前为“影间”Android 离线 Demo，采用 Java、XML、自定义 View、CameraX、Room 和本地文件存储。后端尚未接入，展馆内容使用本地 Mock 数据。

## 运行要求

- Android Studio
- Gradle JDK 21
- Android SDK 36
- Android 7.0 及以上设备

## 运行步骤

1. 使用 Android Studio 打开本目录 `android/`。
2. 确认 Gradle JDK 为 JDK 21，不要使用 JVM 25。
3. 连接已经开启 USB 调试的真机。
4. 运行 `app` 配置。

## 已实现功能

- 皮影展馆首页、藏品详情、搜索和收藏。
- 三个核心页面：皮影展馆、纹样临摹、拍照搭台。
- 牡丹花旦、朱砂线稿、庭院背景和透明双人皮影 Demo 素材。
- 画笔颜色与粗细调整。
- 橡皮擦、撤销、重做、清空。
- 底图透明度和显隐控制。
- Room 草稿记录与 JSON 笔迹文件。
- 草稿重新打开继续编辑。
- CameraX 预览、拍照和系统相册导入。
- 图片方向校正、尺寸压缩和应用内存储。
- 透明皮影人物拖拽、缩放和旋转。
- 背景、影人、题字和边框的海报合成。
- 互动戏台、动作记录与回放。
- PNG 导出到系统相册及系统分享。

正式接口契约见 `../docs/api-contract.md`，笔迹格式见 `../docs/stroke-format.md`，海报草稿格式见 `../docs/poster-format.md`。
