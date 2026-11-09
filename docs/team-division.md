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
- CameraX 拍照、系统相册导入和创作入口。
- Android 页面导航和整体视觉风格。
- 设计文档及答辩 PPT 整合。

主要目录：

```text
android/app/src/main/java/.../feature/home/
android/app/src/main/java/.../feature/detail/
android/app/src/main/java/.../feature/camera/
```

## C 临摹与海报合成

负责：

- 皮影服饰纹样临摹画布。
- 画笔、橡皮擦、撤销、重做和底图透明度。
- 照片背景、透明影人、绘画、题字和边框的分层合成。
- 影人拖拽、缩放、旋转和层级调整。
- Room 草稿数据库和本地文件存储。
- PNG 导出、可编辑草稿恢复和离线同步队列。
- 绘图模块测试和技术亮点说明。

主要目录：

```text
android/app/src/main/java/.../feature/drawing/
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

