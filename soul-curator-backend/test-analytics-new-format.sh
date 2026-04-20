#!/bin/bash

echo "测试SoulCurator用户事件API（新参数格式）"
echo "================================================"

# 等待应用启动
echo "1. 等待应用启动..."
sleep 5

echo "2. 测试健康检查接口..."
curl -s http://localhost:8080/api/v1/analytics/health | python3 -m json.tool

echo ""
echo "3. 测试服务信息接口..."
curl -s http://localhost:8080/api/v1/analytics/info | python3 -m json.tool

echo ""
echo "4. 测试新格式的页面浏览事件..."
curl -X POST http://localhost:8080/api/v1/analytics/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "page_view",
    "eventName": "unknown_entered",
    "eventData": {
      "sessionId": "session_1776159136766_u11g2y8fq",
      "from": "direct",
      "to": "/",
      "params": {},
      "query": {}
    },
    "pagePath": "/entry/emotion",
    "pageTitle": "soul-curator-frontend",
    "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36",
    "screenResolution": "1280x800",
    "language": "zh-CN",
    "timezone": "Asia/Shanghai",
    "timestamp": 1776248100248
  }' | python3 -m json.tool

echo ""
echo "5. 测试带query参数的页面浏览事件..."
curl -X POST http://localhost:8080/api/v1/analytics/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "page_view",
    "eventName": "question_page_view",
    "eventData": {
      "sessionId": "session_1776159136766_u11g2y8fq",
      "from": "/entry/emotion",
      "to": "/question/1",
      "params": {
        "questionId": "1"
      },
      "query": {
        "source": "direct",
        "ref": "home"
      }
    },
    "pagePath": "/question/1",
    "pageTitle": "问题1 - SoulCurator",
    "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36",
    "screenResolution": "1280x800",
    "language": "zh-CN",
    "timezone": "Asia/Shanghai",
    "timestamp": 1776248101000
  }' | python3 -m json.tool

echo ""
echo "6. 测试用户选择事件..."
curl -X POST http://localhost:8080/api/v1/analytics/events \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "selection",
    "eventName": "question_option_selected",
    "eventData": {
      "sessionId": "session_1776159136766_u11g2y8fq",
      "userId": 123,
      "questionId": 1,
      "optionId": "deep-reflection",
      "dimensionScores": {
        "visual": 3,
        "rational": 2,
        "emotional": 1
      }
    },
    "pagePath": "/question/1",
    "pageTitle": "问题1 - SoulCurator",
    "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36",
    "screenResolution": "1280x800",
    "language": "zh-CN",
    "timezone": "Asia/Shanghai",
    "timestamp": 1776248102000
  }' | python3 -m json.tool

echo ""
echo "7. 测试批量记录事件..."
curl -X POST http://localhost:8080/api/v1/analytics/events/batch \
  -H "Content-Type: application/json" \
  -d '[
    {
      "eventType": "button_click",
      "eventName": "start_button_click",
      "eventData": {
        "sessionId": "session_1776159136766_u11g2y8fq",
        "buttonId": "start_exploration",
        "position": "center"
      },
      "pagePath": "/home",
      "pageTitle": "SoulCurator首页",
      "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36",
      "screenResolution": "1280x800",
      "language": "zh-CN",
      "timezone": "Asia/Shanghai",
      "timestamp": 1776248103000
    },
    {
      "eventType": "portrait_generated",
      "eventName": "soul_portrait_created",
      "eventData": {
        "sessionId": "session_1776159136766_u11g2y8fq",
        "userId": 123,
        "portraitId": 42,
        "dimensions": {
          "visual": 8,
          "rational": 6,
          "emotional": 7
        }
      },
      "pagePath": "/result",
      "pageTitle": "灵魂画像结果 - SoulCurator",
      "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36",
      "screenResolution": "1280x800",
      "language": "zh-CN",
      "timezone": "Asia/Shanghai",
      "timestamp": 1776248104000
    }
  ]' | python3 -m json.tool

echo ""
echo "测试完成！"