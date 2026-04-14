#!/bin/bash
echo "=== 启动SoulCurator前端 ==="
echo "项目目录: $(pwd)"

# 检查端口是否被占用
if lsof -ti:5173 > /dev/null 2>&1; then
    echo "⚠️  端口5173已被占用，正在关闭..."
    ./stop-frontend.sh
    sleep 1
fi

# 安装依赖（如果需要）
if [ ! -d "node_modules" ]; then
    echo "安装依赖..."
    npm install
fi

# 启动开发服务器
echo "启动开发服务器..."
npm run dev