#!/bin/bash
echo "=== 关闭SoulCurator前端 ==="

# 查找并杀掉Vite进程
VITE_PID=$(lsof -ti:5173 2>/dev/null)
if [ -n "$VITE_PID" ]; then
    echo "找到Vite进程 (PID: $VITE_PID)，正在关闭..."
    kill -9 $VITE_PID 2>/dev/null
    echo "✅ 前端已关闭"
else
    echo "✅ 前端未运行"
fi

# 也可以杀掉其他可能占用的端口
for port in 5173 5174 5175 5176; do
    PID=$(lsof -ti:$port 2>/dev/null)
    if [ -n "$PID" ]; then
        kill -9 $PID 2>/dev/null
        echo "关闭端口 $port 的进程"
    fi
done