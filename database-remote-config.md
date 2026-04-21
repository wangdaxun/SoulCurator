# SoulCurator 数据库远程连接配置

## 当前架构

```
┌─────────────────┐    ┌─────────────────┐
│   Windows PC    │────▶│   MacBook Pro   │
│                 │    │                 │
│ 后端开发        │    │ PostgreSQL数据库 │
│ 数据生成        │    │ (Docker容器)    │
│                 │    │ 前端开发        │
└─────────────────┘    └─────────────────┘
        │                       │
        └───────────────────────┘
              NAS (备份存储)
```

## 数据库服务器信息 (Mac)

- **IP地址**: 192.168.2.186
- **端口**: 5432
- **数据库名**: soulcurator
- **用户名**: postgres
- **密码**: [在安全位置存储]
- **容器名**: soulcurator-postgres
- **Docker镜像**: postgres:14-alpine

## 连接字符串

### Java/Spring Boot
```yaml
spring:
  datasource:
    url: jdbc:postgresql://192.168.2.186:5432/soulcurator
    username: postgres
    password: ${DB_PASSWORD}
```

### Python
```python
import psycopg2

conn = psycopg2.connect(
    host="192.168.2.186",
    port=5432,
    database="soulcurator",
    user="postgres",
    password="your_password"
)
```

### PostgreSQL客户端 (psql)
```bash
psql -h 192.168.2.186 -p 5432 -U postgres -d soulcurator
```

## 配置验证

### 在Mac上验证配置
```bash
# 检查Docker容器状态
docker ps | grep postgres

# 检查PostgreSQL配置
docker exec soulcurator-postgres psql -U postgres -c "SHOW listen_addresses;"
docker exec soulcurator-postgres psql -U postgres -c "SELECT version();"

# 检查认证配置
docker exec soulcurator-postgres cat /var/lib/postgresql/data/pg_hba.conf | tail -5
```

### 在Windows上测试连接
```powershell
# 测试端口连通性
Test-NetConnection -ComputerName 192.168.2.186 -Port 5432

# 使用telnet测试
telnet 192.168.2.186 5432

# 使用psql测试（需要先安装）
psql -h 192.168.2.186 -p 5432 -U postgres -d soulcurator -c "SELECT current_database();"
```

## 防火墙配置

### Mac防火墙
```bash
# 检查防火墙状态
sudo pfctl -s rules

# 如果防火墙阻止，添加规则
sudo pfctl -a com.apple/250.PostgreSQL -f /dev/stdin <<EOF
pass in proto tcp from any to any port 5432
EOF
```

### Windows防火墙
```powershell
# 允许入站连接（如果需要）
New-NetFirewallRule -DisplayName "PostgreSQL" -Direction Inbound -LocalPort 5432 -Protocol TCP -Action Allow

# 检查现有规则
Get-NetFirewallRule | Where-Object {$_.DisplayName -like "*PostgreSQL*"}
```

## 性能优化

### 连接池配置
```yaml
# application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### PostgreSQL配置优化
```sql
-- 在PostgreSQL中执行
ALTER SYSTEM SET max_connections = 100;
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET work_mem = '16MB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';

-- 重启PostgreSQL使配置生效
```

## 备份策略

### 自动备份脚本 (Mac)
```bash
#!/bin/bash
# backup-soulcurator.sh
BACKUP_DIR="/Volumes/NAS/backups/soulcurator"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份数据库
docker exec soulcurator-postgres pg_dump -U postgres soulcurator > $BACKUP_DIR/soulcurator_$DATE.sql

# 压缩备份
gzip $BACKUP_DIR/soulcurator_$DATE.sql

# 保留最近7天的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/soulcurator_$DATE.sql.gz"
```

### 定时任务 (cron)
```bash
# 每天凌晨2点执行备份
0 2 * * * /Users/wangdaxun/SoulCurator/scripts/backup-soulcurator.sh
```

## 监控和日志

### 查看数据库日志
```bash
# 查看Docker容器日志
docker logs soulcurator-postgres --tail 100

# 查看连接统计
docker exec soulcurator-postgres psql -U postgres -c "SELECT * FROM pg_stat_activity;"

# 查看数据库大小
docker exec soulcurator-postgres psql -U postgres -c "SELECT pg_database_size('soulcurator');"
```

### 性能监控
```sql
-- 查看慢查询
SELECT query, calls, total_time, mean_time
FROM pg_stat_statements
ORDER BY mean_time DESC
LIMIT 10;

-- 查看表大小
SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename))
FROM pg_tables
WHERE schemaname NOT IN ('pg_catalog', 'information_schema')
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

## 故障排除

### 常见问题及解决方案

1. **连接被拒绝**
   ```bash
   # 检查Docker容器是否运行
   docker ps | grep postgres
   
   # 检查端口映射
   docker port soulcurator-postgres 5432
   
   # 检查防火墙
   sudo lsof -i :5432
   ```

2. **认证失败**
   ```bash
   # 检查pg_hba.conf
   docker exec soulcurator-postgres cat /var/lib/postgresql/data/pg_hba.conf
   
   # 重置密码
   docker exec soulcurator-postgres psql -U postgres -c "ALTER USER postgres WITH PASSWORD 'new_password';"
   ```

3. **性能问题**
   ```bash
   # 检查系统资源
   docker stats soulcurator-postgres
   
   # 检查数据库连接数
   docker exec soulcurator-postgres psql -U postgres -c "SELECT count(*) FROM pg_stat_activity;"
   ```

### 网络诊断
```bash
# 在Mac上测试
nc -zv 192.168.2.186 5432

# 在Windows上测试
Test-NetConnection -ComputerName 192.168.2.186 -Port 5432

# 跟踪路由
tracert 192.168.2.186  # Windows
traceroute 192.168.2.186  # Mac
```

## 安全建议

1. **使用强密码**：定期更换数据库密码
2. **限制访问**：只允许特定IP访问数据库
3. **启用SSL**：配置PostgreSQL使用SSL连接
4. **定期备份**：确保数据安全
5. **监控日志**：检测异常访问
6. **更新软件**：定期更新PostgreSQL和Docker

## 扩展配置

### 启用SSL（可选）
```bash
# 生成SSL证书
openssl req -new -text -passout pass:abcd -subj /CN=localhost -out server.req
openssl rsa -in privkey.pem -passin pass:abcd -out server.key
openssl req -x509 -in server.req -text -key server.key -out server.crt

# 复制证书到容器
docker cp server.key soulcurator-postgres:/var/lib/postgresql/data/
docker cp server.crt soulcurator-postgres:/var/lib/postgresql/data/

# 配置PostgreSQL使用SSL
docker exec soulcurator-postgres psql -U postgres -c "ALTER SYSTEM SET ssl = on;"
docker exec soulcurator-postgres psql -U postgres -c "ALTER SYSTEM SET ssl_cert_file = '/var/lib/postgresql/data/server.crt';"
docker exec soulcurator-postgres psql -U postgres -c "ALTER SYSTEM SET ssl_key_file = '/var/lib/postgresql/data/server.key';"

# 重启容器
docker restart soulcurator-postgres
```

### 设置复制（高可用）
```bash
# 创建复制用户
docker exec soulcurator-postgres psql -U postgres -c "CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'replica_password';"

# 配置主服务器
docker exec soulcurator-postgres psql -U postgres -c "ALTER SYSTEM SET wal_level = replica;"
docker exec soulcurator-postgres psql -U postgres -c "ALTER SYSTEM SET max_wal_senders = 10;"
docker exec soulcurator-postgres psql -U postgres -c "ALTER SYSTEM SET wal_keep_size = 1GB;"

# 在pg_hba.conf中添加
# host replication replicator 192.168.2.0/24 md5
```

## 维护命令

### 日常维护
```bash
# 清理旧数据
docker exec soulcurator-postgres psql -U postgres -d soulcurator -c "VACUUM ANALYZE;"

# 重新索引
docker exec soulcurator-postgres psql -U postgres -d soulcurator -c "REINDEX DATABASE soulcurator;"

# 更新统计信息
docker exec soulcurator-postgres psql -U postgres -d soulcurator -c "ANALYZE;"
```

### 紧急恢复
```bash
# 停止服务
docker stop soulcurator-postgres

# 从备份恢复
docker exec soulcurator-postgres psql -U postgres -c "DROP DATABASE IF EXISTS soulcurator;"
docker exec soulcurator-postgres psql -U postgres -c "CREATE DATABASE soulcurator;"
docker exec -i soulcurator-postgres psql -U postgres soulcurator < backup_file.sql

# 启动服务
docker start soulcurator-postgres
```

---

**最后更新**: 2026-04-21  
**维护者**: 王达迅 (爹)  
**联系方式**: [在项目中记录]