# 三人开发分工

## A 后端与远程数据

负责：

- Spring Boot 工程和 MySQL 数据库。
- 皮影展馆、纹样、影人、海报模板查询接口。
- 临摹与海报作品上传、查询和删除接口。
- 文件存储及静态资源访问。
- OpenAPI 或 Swagger 接口页面。
- Android 远程 DTO 的接口字段确认。

主要目录：

```text
server/
android/app/src/main/java/.../data/remote/
```

## B 页面与业务交互

负责：

- 皮影展馆首页、展品列表、详情、搜索和收藏。
- 创作入口、工具栏和作品列表等页面界面。
- Android 页面导航和整体视觉风格。
- 设计文档及答辩 PPT 整合。

主要目录：

```text
android/app/src/main/java/.../feature/home/
android/app/src/main/java/.../feature/detail/
android/app/src/main/java/.../feature/works/
```

## C 临摹与海报合成

负责：

- 皮影服饰纹样临摹画布。
- 画笔、橡皮擦、撤销、重做和底图透明度。
- CameraX 相机预览、拍照和系统相册导入。
- 照片方向校正、尺寸限制、简单裁切及持久化存储。
- 照片背景、透明影人、绘画、题字和边框的分层合成。
- 影人拖拽、缩放、旋转和层级调整。
- Room 草稿数据库和本地文件存储。
- PNG 导出、可编辑草稿恢复和离线同步队列。
- 绘图模块测试和技术亮点说明。

主要目录：

```text
android/app/src/main/java/.../feature/drawing/
android/app/src/main/java/.../feature/camera/
android/app/src/main/java/.../feature/poster/
android/app/src/main/java/.../feature/drafts/
android/app/src/main/java/.../data/local/
android/app/src/main/java/.../sync/
```

## 共同责任

- 每个人测试自己负责的模块。
- 公共字段变更必须同步接口文档。
- 禁止在没有来源登记的情况下加入正式素材。
- 合并前确认没有覆盖其他成员的代码。

## 相机模块边界

相机不是独立的拍照功能，而是皮影海报创作的数据入口：用户拍摄校园、庭院、墙面或室内场景作为“幕布背景”，应用完成方向校正、尺寸压缩和本地保存后，将图片 URI 交给海报合成画布。C 再在背景上叠加透明皮影、临摹作品、题字和边框，最终保存草稿或导出 PNG。

首版相机模块不包含实时 AR、人物识别、自动抠图、滤镜系统、视频录制或直播。B 只负责相机入口和页面视觉与全局导航的一致性，C 负责相机能力、图片处理及其到合成画布的完整数据链路。

