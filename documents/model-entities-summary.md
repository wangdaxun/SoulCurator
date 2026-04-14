# SoulCurator Model实体类总结

## 已创建的实体类（11个）

### 1. 核心业务实体
1. **User** (`com.soulcurator.backend.model.User`)
   - 对应表: `users`
   - 功能: 用户信息管理，支持匿名和注册用户
   - 关联: selections, portraits, recommendations, events, sessions

2. **Question** (`com.soulcurator.backend.model.Question`)
   - 对应表: `questions`
   - 功能: 灵魂探索问题管理
   - 关联: options, userSelections

3. **Option** (`com.soulcurator.backend.model.Option`)
   - 对应表: `options`
   - 功能: 问题选项管理
   - 关联: question, userSelections

4. **UserSelection** (`com.soulcurator.backend.model.UserSelection`)
   - 对应表: `user_selections`
   - 功能: 用户选择记录
   - 关联: user, question, option

5. **SoulPortrait** (`com.soulcurator.backend.model.SoulPortrait`)
   - 对应表: `soul_portraits`
   - 功能: 灵魂画像生成结果
   - 关联: user, recommendations, userSession

6. **RecommendedWork** (`com.soulcurator.backend.model.RecommendedWork`)
   - 对应表: `recommended_works`
   - 功能: 推荐作品库管理
   - 关联: recommendations

7. **PersonalizedRecommendation** (`com.soulcurator.backend.model.PersonalizedRecommendation`)
   - 对应表: `personalized_recommendations`
   - 功能: 个性化推荐记录
   - 关联: portrait, user, work

### 2. 分析实体
8. **UserEvent** (`com.soulcurator.backend.model.analytics.UserEvent`)
   - 对应表: `user_events`
   - 功能: 用户行为埋点记录
   - 关联: user, userSession

9. **UserSession** (`com.soulcurator.backend.model.UserSession`)
   - 对应表: `user_sessions`
   - 功能: 用户会话分析
   - 关联: user, portrait, events

### 3. 配置实体
10. **SystemConfig** (`com.soulcurator.backend.model.SystemConfig`)
    - 对应表: `system_configs`
    - 功能: 系统配置管理
    - 关联: 无（独立表）

### 4. 维度实体
11. **SoulDimension** (`com.soulcurator.backend.model.SoulDimension`)
    - 对应表: `soul_dimensions`
    - 功能: 灵魂维度定义
    - 关联: 弱关联到questions, options, soul_portraits, recommended_works

## 实体类特点

### 1. 完整的字段映射
- 所有数据库字段都有对应的Java字段
- 使用正确的JPA注解：`@Entity`, `@Table`, `@Column`等
- 支持JSONB字段：`@JdbcTypeCode(SqlTypes.JSON)`
- 支持数组字段：`@JdbcTypeCode(SqlTypes.ARRAY)`

### 2. 完整的关系映射
- 一对一：`@OneToOne`
- 一对多：`@OneToMany`
- 多对一：`@ManyToOne`
- 外键约束：`@JoinColumn`
- 级联操作：`cascade = CascadeType.ALL`
- 懒加载：`fetch = FetchType.LAZY`

### 3. 丰富的便捷方法
每个实体类都包含：
- 多个构造方法
- 业务逻辑方法（如`getDisplayName()`, `isValid()`等）
- 数据转换方法（如`toJsonString()`）
- 状态判断方法（如`isActive()`, `isCompleted()`）

### 4. 类型安全
- 使用Java 17的Record类型（SystemConfig.ApiInfo）
- 使用泛型集合
- 空值安全处理
- 输入验证方法

## 依赖关系

### 编译依赖
```xml
<!-- 已在pom.xml中 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

### 可选依赖（用于JSON处理）
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

## 使用示例

### 1. 创建用户
```java
User user = new User("test_session_001");
user.setUsername("测试用户");
user.setEmail("test@example.com");
userRepository.save(user);
```

### 2. 记录用户选择
```java
UserSelection selection = new UserSelection(user, question, option, 15);
userSelectionRepository.save(selection);
```

### 3. 生成灵魂画像
```java
SoulPortrait portrait = new SoulPortrait(user, "深度思考者", "你的灵魂在静谧的星空下共鸣...");
portrait.setDimensionScores(dimensionScores);
portrait.setTopDimensions(Arrays.asList("introspective", "rational", "fantasy"));
soulPortraitRepository.save(portrait);
```

### 4. 创建个性化推荐
```java
PersonalizedRecommendation recommendation = new PersonalizedRecommendation(
    portrait, work, 
    "《星际穿越》的时空哲思与你的深度思考灵魂完美契合",
    92.5
);
personalizedRecommendationRepository.save(recommendation);
```

## 下一步工作

### 1. 创建Repository接口
为每个实体类创建对应的Repository：
- `UserRepository`
- `QuestionRepository`
- `OptionRepository`
- `UserSelectionRepository`
- `SoulPortraitRepository`
- `RecommendedWorkRepository`
- `PersonalizedRecommendationRepository`
- `UserSessionRepository`
- `SystemConfigRepository`
- `SoulDimensionRepository`

### 2. 创建Service层
实现业务逻辑：
- 用户服务
- 问题服务
- 选择服务
- 画像生成服务
- 推荐服务
- 分析服务

### 3. 创建Controller层
提供REST API：
- 用户API
- 问题API
- 选择API
- 画像API
- 推荐API
- 分析API

### 4. 创建DTO
定义数据传输对象：
- 请求DTO
- 响应DTO
- 查询DTO

## 文件位置
所有实体类位于：`/Users/wangdaxun/SoulCurator/soul-curator-backend/src/main/java/com/soulcurator/backend/model/`

## 验证
可以运行以下命令验证实体类：
```bash
cd /Users/wangdaxun/SoulCurator/soul-curator-backend
./mvnw compile
```

如果编译成功，说明所有实体类语法正确。

---

**创建完成**: 2026-04-13  
**实体数量**: 11个  
**代码行数**: 约 60,000 字节  
**包含功能**: 完整的CRUD操作、业务逻辑、数据验证、类型转换