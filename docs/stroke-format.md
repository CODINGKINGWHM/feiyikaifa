# 可编辑笔迹文件格式

版本：1

笔迹坐标使用相对于画布的比例值，范围为 `0.0` 到 `1.0`，避免因手机尺寸变化导致草稿错位。

```json
{
  "version": 1,
  "patternId": 1,
  "canvasRatio": 1.0,
  "strokes": [
    {
      "tool": "PEN",
      "color": "#D32F2F",
      "width": 12.0,
      "points": [
        {"x": 0.132, "y": 0.205},
        {"x": 0.138, "y": 0.211}
      ]
    }
  ]
}
```

## 字段约束

- `version`：当前固定为 `1`。
- `tool`：`PEN` 或 `ERASER`。
- `color`：`#RRGGBB` 格式；橡皮擦可忽略该字段。
- `width`：以设计画布为基准的画笔宽度。
- `points`：至少包含一个点。
- Android 与后端不得修改客户端上传的原始笔迹文件内容。

