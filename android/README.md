# Android 客户端

当前为 C 同学负责的“影间”创作模块技术原型，采用 Java、XML、自定义 View、Room 和本地文件存储。

## 运行要求

- Android Studio
- Gradle JDK 17
- Android SDK 36
- Android 7.0 及以上设备

## 运行步骤

1. 使用 Android Studio 打开本目录 `android/`。
2. 确认 Gradle JDK 为 Android Studio 自带的 JDK 17。
3. 连接已经开启 USB 调试的真机。
4. 运行 `app` 配置。

## 已实现的技术基础

- 三张原创透明占位纹样，用于验证绘图引擎。
- 画笔颜色与粗细调整。
- 橡皮擦、撤销、重做、清空。
- 底图透明度和显隐控制。
- Room 草稿记录与 JSON 笔迹文件。
- 草稿重新打开继续编辑。
- PNG 导出到系统相册。

## 正在升级

- 皮影服饰纹样临摹素材。
- 从相册选择或拍摄海报背景。
- 透明皮影人物拖拽、缩放与旋转。
- 绘画、影人、题字、边框的分层合成。
- 海报草稿 JSON 与最终作品导出。

正式接口契约见 `../docs/api-contract.md`，笔迹格式见 `../docs/stroke-format.md`，海报草稿格式见 `../docs/poster-format.md`。

