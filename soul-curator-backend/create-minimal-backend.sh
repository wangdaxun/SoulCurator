#!/bin/bash

echo "=== 创建SoulCurator最小化后端版本 ==="
echo "当前目录: $(pwd)"

# 备份当前文件
BACKUP_DIR="backup-$(date +%Y%m%d%H%M%S)"
echo "备份当前文件到: $BACKUP_DIR"
mkdir -p "$BACKUP_DIR"
cp -r src/main/java/com/soulcurator/backend/* "$BACKUP_DIR/" 2>/dev/null || true

# 清理并创建新结构
echo "清理旧文件..."
rm -rf src/main/java/com/soulcurator/backend/*
mkdir -p src/main/java/com/soulcurator/backend/{model,repository,controller}

echo "创建简单实体..."
cat > src/main/java/com/soulcurator/backend/model/SimpleWork.java << 'EOF'
package com.soulcurator.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "simple_works")
public class SimpleWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String type;
    private String description;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
EOF

echo "创建简单Repository..."
cat > src/main/java/com/soulcurator/backend/repository/SimpleWorkRepository.java << 'EOF'
package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.SimpleWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleWorkRepository extends JpaRepository<SimpleWork, Long> {
}
EOF

echo "创建简单Controller..."
cat > src/main/java/com/soulcurator/backend/controller/SimpleController.java << 'EOF'
package com.soulcurator.backend.controller;

import com.soulcurator.backend.model.SimpleWork;
import com.soulcurator.backend.repository.SimpleWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SimpleController {
    @Autowired 
    private SimpleWorkRepository repo;
    
    @GetMapping("/works")
    public List<SimpleWork> getAll() { 
        return repo.findAll(); 
    }
    
    @PostMapping("/works")
    public SimpleWork create(@RequestBody SimpleWork work) { 
        return repo.save(work); 
    }
    
    @GetMapping("/health")
    public String health() { 
        return "SoulCurator Backend is running!"; 
    }
    
    @GetMapping("/test-data")
    public String addTestData() {
        SimpleWork work1 = new SimpleWork();
        work1.setTitle("最后的生还者");
        work1.setType("GAME");
        work1.setDescription("末日生存游戏，讲述乔尔和艾莉的故事");
        repo.save(work1);
        
        SimpleWork work2 = new SimpleWork();
        work2.setTitle("星际穿越");
        work2.setType("MOVIE");
        work2.setDescription("关于时间、爱和星际旅行的科幻电影");
        repo.save(work2);
        
        SimpleWork work3 = new SimpleWork();
        work3.setTitle("三体");
        work3.setType("BOOK");
        work3.setDescription("刘慈欣的科幻小说，讲述地球与三体文明的接触");
        repo.save(work3);
        
        return "添加了3个测试作品！";
    }
}
EOF

echo "创建主启动类..."
cat > src/main/java/com/soulcurator/backend/SoulCuratorApplication.java << 'EOF'
package com.soulcurator.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoulCuratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoulCuratorApplication.class, args);
    }
}
EOF

echo "创建简化配置文件..."
cat > src/main/resources/application.yml << 'EOF'
spring:
  application:
    name: soul-curator-simple
  
  datasource:
    url: jdbc:postgresql://localhost:5432/soulcurator
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    com.soulcurator.backend: DEBUG
    org.springframework.web: INFO
EOF

echo "创建开发环境配置..."
cat > src/main/resources/application-dev.yml << 'EOF'
# 开发环境配置
spring:
  jpa:
    show-sql: true
  
  web:
    cors:
      allowed-origins: "http://localhost:3000,http://localhost:5173"
      allowed-methods: "*"
      allowed-headers: "*"
      allow-credentials: true

logging:
  level:
    com.soulcurator.backend: TRACE
EOF

echo "✅ 最小化项目创建完成！"
echo ""
echo "接下来运行:"
echo "1. 确保数据库运行: docker-compose up -d postgres redis"
echo "2. 启动后端: ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev"
echo "3. 测试API: curl http://localhost:8080/api/health"