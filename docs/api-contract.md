# Android 与后端接口契约

版本：0.2 皮影主题草案

## 基本约定

- 基础路径：`/api/v1`
- 数据格式：`application/json; charset=UTF-8`
- 文件上传：`multipart/form-data`
- 时间：ISO 8601 UTC，例如 `2026-11-09T08:00:00Z`
- 主键：服务端使用长整型，Android 使用字符串形式的 UUID 作为 `clientId`

统一响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "timestamp": "2026-11-09T08:00:00Z"
}
```

## 皮影展馆对象

```json
{
  "id": 1,
  "name": "华县皮影旦角",
  "region": "陕西华县",
  "roleType": "DAN",
  "summary": "角色与造型简介",
  "history": "起源与传承介绍",
  "coverUrl": "/files/exhibits/1-cover.webp",
  "detailImages": [
    "/files/exhibits/1-detail-1.webp"
  ],
  "favorite": false,
  "updatedAt": "2026-11-09T08:00:00Z"
}
```

### 查询展馆列表

```http
GET /api/v1/exhibits?keyword=华县&region=陕西&page=1&size=10
```

### 查询展馆详情

```http
GET /api/v1/exhibits/{id}
```

## 皮影纹样对象

```json
{
  "id": 1,
  "name": "牡丹团花",
  "categoryCode": "SHADOW_COSTUME",
  "categoryName": "皮影服饰纹样",
  "description": "牡丹纹样及其寓意",
  "previewUrl": "/files/patterns/1-preview.webp",
  "guideUrl": "/files/patterns/1-guide.png",
  "difficulty": 1,
  "sourceUrl": "",
  "author": "",
  "license": "",
  "updatedAt": "2026-11-09T08:00:00Z"
}
```

约束：

- `difficulty` 只允许 `1`、`2`、`3`。
- `previewUrl` 用于列表与详情展示。
- `guideUrl` 必须指向可用于画布的透明 PNG。
- 正式纹样必须填写 `sourceUrl` 和 `license`。

## 查询纹样列表

```http
GET /api/v1/patterns?categoryCode=SHADOW_COSTUME&keyword=牡丹&page=1&size=10
```

分页数据：

```json
{
  "list": [],
  "page": 1,
  "size": 10,
  "total": 0
}
```

## 查询纹样详情

```http
GET /api/v1/patterns/{id}
```

## 皮影人物素材

```json
{
  "id": 12,
  "name": "旦角影人",
  "roleType": "DAN",
  "region": "陕西华县",
  "previewUrl": "/files/puppets/12-preview.webp",
  "imageUrl": "/files/puppets/12.png",
  "sourceUrl": "",
  "author": "",
  "license": ""
}
```

```http
GET /api/v1/puppets?roleType=DAN&page=1&size=10
GET /api/v1/puppets/{id}
```

`imageUrl` 必须指向透明背景 PNG，供海报合成使用。

## 海报模板

```json
{
  "id": 2,
  "name": "古院开演",
  "previewUrl": "/files/poster-templates/2-preview.webp",
  "backgroundUrl": "/files/poster-templates/2-background.webp",
  "frameUrl": "/files/poster-templates/2-frame.png",
  "defaultTitle": "今日开演",
  "canvasRatio": 0.75
}
```

```http
GET /api/v1/poster-templates
GET /api/v1/poster-templates/{id}
```

## 上传作品

```http
POST /api/v1/artworks
Content-Type: multipart/form-data
```

表单字段：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `image` | PNG 文件 | 是 | 合成后的作品图 |
| `editableFile` | JSON 文件 | 否 | 笔迹 JSON 或海报草稿 JSON |
| `workType` | String | 是 | `TRACING` 或 `POSTER` |
| `patternId` | Long | 否 | 临摹作品使用的纹样编号 |
| `templateId` | Long | 否 | 海报作品使用的模板编号 |
| `title` | String | 是 | 作品名称 |
| `clientId` | UUID String | 是 | Android 本地唯一编号，用于防止重复上传 |

作品响应：

```json
{
  "id": 1001,
  "clientId": "7f672ced-9286-42c4-8642-f927ac5b5608",
  "workType": "POSTER",
  "patternId": 1,
  "templateId": 2,
  "title": "今日开演",
  "imageUrl": "/files/artworks/1001.png",
  "editableFileUrl": "/files/editable/1001.json",
  "createdAt": "2026-11-09T08:00:00Z",
  "updatedAt": "2026-11-09T08:00:00Z"
}
```

## 查询当前用户作品

```http
GET /api/v1/artworks/my?page=1&size=10
```

可使用 `workType=TRACING` 或 `workType=POSTER` 过滤。

## 查询作品详情

```http
GET /api/v1/artworks/{id}
```

## 删除作品

```http
DELETE /api/v1/artworks/{id}
```

## 错误码初稿

| code | 含义 |
|---:|---|
| 0 | 成功 |
| 40001 | 请求参数错误 |
| 40101 | 未登录或令牌失效 |
| 40401 | 纹样不存在 |
| 40402 | 作品不存在 |
| 40403 | 皮影人物素材不存在 |
| 40404 | 海报模板不存在 |
| 40901 | `clientId` 重复 |
| 50001 | 文件保存失败 |
| 50000 | 服务器内部错误 |

