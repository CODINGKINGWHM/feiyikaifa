# 可编辑皮影海报草稿格式

版本：1

海报草稿只保存资源引用、文字和变换参数；最终展示图另存为 PNG。位置使用 `0.0` 到 `1.0` 的相对坐标，避免不同手机尺寸导致元素错位。

```json
{
  "version": 1,
  "canvasRatio": 0.75,
  "background": {
    "type": "LOCAL_URI",
    "value": "content://media/..."
  },
  "elements": [
    {
      "id": "element-1",
      "type": "PUPPET",
      "assetId": 12,
      "localAsset": "puppet_female_01",
      "centerX": 0.32,
      "centerY": 0.61,
      "scale": 0.65,
      "rotation": -6.0,
      "zIndex": 1
    },
    {
      "id": "element-2",
      "type": "TEXT",
      "text": "今日开演",
      "centerX": 0.5,
      "centerY": 0.12,
      "scale": 1.0,
      "rotation": 0.0,
      "zIndex": 2
    }
  ],
  "templateId": 2,
  "createdAt": "2026-11-09T08:00:00Z",
  "updatedAt": "2026-11-09T08:10:00Z"
}
```

## 元素类型

- `PUPPET`：透明皮影人物。
- `DRAWING`：用户临摹或手绘图层。
- `TEXT`：题字。
- `FRAME`：海报边框。

## 背景类型

- `LOCAL_URI`：用户拍摄或从相册选择的照片。
- `REMOTE_URL`：后端提供的背景模板。
- `BUNDLED_ASSET`：APP 内置背景。

