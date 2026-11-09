# Spring Boot 后端

后端计划使用 Spring Boot、MySQL 和 REST API，为 Android 客户端提供皮影展馆、纹样素材、海报模板、作品上传和云端作品管理能力。

## 包结构建议

```text
com.codingkingwhm.feiyikaifa
├─ common
├─ config
├─ controller
├─ dto
├─ entity
├─ repository
├─ service
└─ storage
```

## 第一阶段接口

- 查询皮影展馆条目与详情。
- 查询纹样底图、皮影人物素材和海报模板。
- 上传临摹或海报作品 PNG，以及可选的可编辑草稿 JSON。
- 查询、删除当前用户的作品。
- 提供上传文件的访问地址。

接口契约见 [../docs/api-contract.md](../docs/api-contract.md)。

正式 Spring Boot 工程尚未生成。

