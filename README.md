# 非遗纹样临摹 APP

本仓库用于开发一款前后端分离的非遗纹样学习与临摹创作 APP。用户可以浏览非遗项目、选择纹样底图进行手绘临摹、保存本地草稿，并在联网后将作品同步到后端。

## 仓库结构

```text
feiyikaifa/
├─ android/   Android 客户端
├─ server/    Spring Boot 后端
└─ docs/      接口、数据格式与协作文档
```

## 成员分工

- A：Spring Boot、MySQL、文件上传、远程接口和部署。
- B：Android 首页、分类、列表、详情、搜索、收藏和导航。
- C：自定义绘图画布、Room 本地草稿、文件存储和离线同步。

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

## 当前状态

当前仅完成仓库结构和协作规范，Android 与后端工程将在各自目录中继续初始化。

