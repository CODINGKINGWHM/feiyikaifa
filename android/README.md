# Android 客户端

计划采用 Java、XML、AndroidX 和 Material Components。

## 包结构建议

```text
com.codingkingwhm.feiyikaifa
├─ common
├─ data
│  ├─ local
│  ├─ remote
│  ├─ model
│  └─ repository
├─ feature
│  ├─ home
│  ├─ detail
│  ├─ drawing
│  └─ drafts
└─ sync
```

## 模块边界

- B 负责 `feature/home`、`feature/detail` 等普通业务页面。
- C 负责 `feature/drawing`、`feature/drafts`、`data/local` 和 `sync`。
- A 与 Android 端共同维护 `data/remote` 中的接口 DTO。

正式 Android Studio 工程尚未生成。

