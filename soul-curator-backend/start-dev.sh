#!/bin/bash
echo "=== SoulCurator 开发环境启动脚本 ==="
echo "当前目录: $(pwd)"
echo ""

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行，请先启动Docker Desktop"
    exit 1
fi

# 启动数据库
echo "1. 启动PostgreSQL和Redis..."
docker-compose up -d postgres redis

# 等待数据库就绪
echo "2. 等待数据库就绪..."
for i in {1..10}; do
    if docker-compose exec postgres pg_isready -U postgres > /dev/null 2>&1; then
        echo "   ✅ PostgreSQL已就绪"
        break
    fi
    echo "   ⏳ 等待中... ($i/10)"
    sleep 2
done

# 检查数据库状态
if ! docker-compose exec postgres pg_isready -U postgres > /dev/null 2>&1; then
    echo "❌ PostgreSQL启动失败，请检查日志：docker-compose logs postgres"
    exit 1
fi

echo ""
echo "3. 数据库信息："
echo "   PostgreSQL: localhost:5432 (soulcurator/postgres/postgres)"
echo "   Redis: localhost:6379"
echo ""
echo "4. 现在可以启动Spring Boot后端："
echo "   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev"
echo ""
echo "5. 或者用Navicat连接数据库："
echo "   主机: localhost"
echo "   端口: 5432"
echo "   数据库: soulcurator"
echo "   用户名: postgres"
echo "   密码: postgres"
echo ""
echo "✅ 开发环境准备就绪！"