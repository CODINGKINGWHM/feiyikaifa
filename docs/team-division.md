# 三人开发分工

## A 后端与远程数据

负责：

- Spring Boot 工程和 MySQL 数据库。
- 纹样查询、作品上传和作品管理接口。
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

- 首页、分类、纹样列表和详情。
- 搜索、收藏、个人中心。
- Android 页面导航和整体视觉风格。
- 设计文档及答辩 PPT 整合。

主要目录：

```text
android/app/src/main/java/.../feature/home/
android/app/src/main/java/.../feature/detail/
android/app/src/main/java/.../feature/search/
```

## C 绘图与离线存储

负责：

- 自定义纹样临摹画布。
- 画笔、橡皮擦、撤销、重做和底图透明度。
- Room 草稿数据库和本地文件存储。
- PNG 导出、草稿恢复和离线同步队列。
- 绘图模块测试和技术亮点说明。

主要目录：

```text
android/app/src/main/java/.../feature/drawing/
android/app/src/main/java/.../feature/drafts/
android/app/src/main/java/.../data/local/
android/app/src/main/java/.../sync/
```

## 共同责任

- 每个人测试自己负责的模块。
- 公共字段变更必须同步接口文档。
- 禁止在没有来源登记的情况下加入正式素材。
- 合并前确认没有覆盖其他成员的代码。

