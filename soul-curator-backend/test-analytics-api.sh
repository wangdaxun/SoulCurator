#!/bin/bash

echo "测试SoulCurator用户事件API（基于user_events表）"
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
echo "4. 测试记录用户事件接口..."
curl -X POST http://localhost:8080/api/v1/analytics/events \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "session_123456",
    "eventType": "page_view",
    "eventName": "home_page_view",
    "pageUrl": "/home",
    "eventData": {
      "category": "navigation",
      "duration": 15
    }
  }' | python3 -m json.tool

echo ""
echo "5. 测试带用户ID的事件记录..."
curl -X POST http://localhost:8080/api/v1/analytics/events \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "sessionId": "session_123456",
    "eventType": "selection",
    "eventName": "question_option_selected",
    "pageUrl": "/question/1",
    "eventData": {
      "questionId": 1,
      "optionId": "deep-reflection",
      "dimensionScores": {
        "visual": 3,
        "rational": 2
      }
    }
  }' | python3 -m json.tool

echo ""
echo "6. 测试批量记录事件接口..."
curl -X POST http://localhost:8080/api/v1/analytics/events/batch \
  -H "Content-Type: application/json" \
  -d '[
    {
      "sessionId": "session_123456",
      "eventType": "button_click",
      "eventName": "start_exploration_click",
      "pageUrl": "/home",
      "eventData": {
        "buttonId": "start_button",
        "timestamp": "2026-04-15T17:00:00"
      }
    },
    {
      "sessionId": "session_123456",
      "eventType": "portrait_generated",
      "eventName": "soul_portrait_created",
      "pageUrl": "/result",
      "eventData": {
        "portraitId": 42,
        "dimensions": {
          "visual": 8,
          "rational": 6,
          "emotional": 7
        }
      }
    }
  ]' | python3 -m json.tool

echo ""
echo "测试完成！"