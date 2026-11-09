# 影间 皮影文化与创作 APP

本仓库用于开发一款前后端分离的皮影文化展示与互动创作 Android APP。用户可以浏览皮影展馆、临摹服饰纹样，将绘画、皮影人物与拍摄照片组合成海报作品，并保存分享或同步到云端。

核心体验：

```text
浏览皮影 → 选择纹样临摹 → 拍照搭建背景 → 合成海报 → 保存分享
```

## 仓库结构

```text
feiyikaifa/
├─ android/   Android 客户端
├─ server/    Spring Boot 后端
└─ docs/      接口、数据格式与协作文档
```

## 成员分工

- A：Spring Boot、MySQL、皮影素材接口、云端作品管理和部署。
- B：整体 UI、皮影展馆、详情、相机与照片导入。
- C：纹样临摹与海报合成画布、Room 本地草稿、图片导出。

详细边界见 [docs/team-division.md](docs/team-division.md)。

## 开发约定

1. `main` 只保存经过验证的稳定版本。
2. 日常集成使用 `develop` 分支。
3. 每名成员从 `develop` 创建自己的功能分支。
4. 接口字段变化必须先修改 `docs/api-contract.md`。
5. 图片和大文件不直接存入 MySQL，数据库只保存元数据和文件地址。
6. 正式素材必须记录来源、作者和许可证。

推荐分支：

```text
feature/a-backend
feature/b-ui
feature/c-drawing
```

## 首版范围

- 6—8 个皮影展示条目。
- 4 张服饰纹样临摹底图。
- 2 套拍照海报模板。
- 作品本地草稿、PNG 导出与云端成品上传。

人物动画、实时 AR 和视频导出不进入首版。

## 当前状态

仓库结构和接口契约已建立；Android 端已有自定义绘图、撤销重做、Room 草稿和 PNG 导出的技术原型，正在升级为皮影主题临摹与海报合成 Demo。Spring Boot 后端尚未初始化。

