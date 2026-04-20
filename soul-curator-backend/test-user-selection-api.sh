#!/bin/bash

# 用户选择记录API测试脚本
# 使用方法：./test-user-selection-api.sh

set -e

API_BASE="http://localhost:8080/api/v1"
SESSION_ID="test_session_$(date +%s)_$(openssl rand -hex 4)"
GATEWAY_TYPE="movie"

echo "=== 用户选择记录API测试 ==="
echo "会话ID: $SESSION_ID"
echo "网关类型: $GATEWAY_TYPE"
echo "API基础地址: $API_BASE"
echo ""

# 1. 健康检查
echo "1. 测试健康检查..."
curl -s -X GET "$API_BASE/selections/health" | jq .
echo ""

# 2. 开始灵魂探索（需要先有会话）
echo "2. 开始灵魂探索..."
START_RESPONSE=$(curl -s -X POST "$API_BASE/soul-exploration/start" \
  -H "Content-Type: application/json" \
  -d "{
    \"gatewayType\": \"$GATEWAY_TYPE\",
    \"sessionId\": \"$SESSION_ID\"
  }")
echo "$START_RESPONSE" | jq .
echo ""

# 3. 记录用户选择
echo "3. 记录用户选择..."
SELECTION_RESPONSE=$(curl -s -X POST "$API_BASE/selections/record" \
  -H "Content-Type: application/json" \
  -d "{
    \"sessionId\": \"$SESSION_ID\",
    \"gatewayType\": \"$GATEWAY_TYPE\",
    \"selections\": [
      {
        \"questionId\": 1,
        \"optionId\": \"deep-reflection\",
        \"selectedAt\": $(date +%s)000,
        \"stepNumber\": 1,
        \"timeSpentSeconds\": 15,
        \"itemMetadata\": {
          \"confidence\": 0.8,
          \"hesitation\": false
        }
      },
      {
        \"questionId\": 2,
        \"optionId\": \"humanity-moral-boundary\",
        \"selectedAt\": $(($(date +%s) + 5))000,
        \"stepNumber\": 2,
        \"timeSpentSeconds\": 20,
        \"itemMetadata\": {
          \"confidence\": 0.9,
          \"hesitation\": true
        }
      },
      {
        \"questionId\": 3,
        \"optionId\": \"tech-ethics-clash\",
        \"selectedAt\": $(($(date +%s) + 10))000,
        \"stepNumber\": 3,
        \"timeSpentSeconds\": 12,
        \"itemMetadata\": {
          \"confidence\": 0.7,
          \"hesitation\": false
        }
      }
    ],
    \"metadata\": {
      \"deviceInfo\": \"web\",
      \"explorationDuration\": 47000,
      \"userAgent\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)\"
    }
  }")
echo "$SELECTION_RESPONSE" | jq .
echo ""

# 4. 获取用户选择记录
echo "4. 获取用户选择记录..."
GET_RESPONSE=$(curl -s -X GET "$API_BASE/selections?sessionId=$SESSION_ID&gatewayType=$GATEWAY_TYPE")
echo "$GET_RESPONSE" | jq .
echo ""

# 5. 获取用户选择摘要
echo "5. 获取用户选择摘要..."
SUMMARY_RESPONSE=$(curl -s -X GET "$API_BASE/selections/summary?sessionId=$SESSION_ID")
echo "$SUMMARY_RESPONSE" | jq .
echo ""

# 6. 测试错误情况 - 无效的会话ID
echo "6. 测试错误情况 - 无效的会话ID..."
ERROR_RESPONSE=$(curl -s -X GET "$API_BASE/selections?sessionId=invalid_session")
echo "$ERROR_RESPONSE" | jq .
echo ""

# 7. 测试错误情况 - 无效的选项ID
echo "7. 测试错误情况 - 无效的选项ID..."
ERROR_RESPONSE2=$(curl -s -X POST "$API_BASE/selections/record" \
  -H "Content-Type: application/json" \
  -d "{
    \"sessionId\": \"$SESSION_ID\",
    \"gatewayType\": \"$GATEWAY_TYPE\",
    \"selections\": [
      {
        \"questionId\": 999,
        \"optionId\": \"invalid-option\",
        \"selectedAt\": $(date +%s)000
      }
    ]
  }")
echo "$ERROR_RESPONSE2" | jq .
echo ""

echo "=== 测试完成 ==="
echo "会话ID: $SESSION_ID"
echo "可以用于后续测试或清理"