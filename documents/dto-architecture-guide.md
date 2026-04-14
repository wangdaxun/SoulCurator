# SoulCurator DTO架构指南

## 目录结构
```
src/main/java/com/soulcurator/backend/
├── model/          # 实体层（对应数据库表）
├── dto/            # 数据传输对象
│   ├── request/    # 请求DTO（接收前端参数）
│   ├── response/   # 响应DTO（返回给前端）
│   └── analytics/  # 分析相关DTO
├── vo/             # 值对象（Service层内部使用）
├── service/        # 服务层（业务逻辑）
├── repository/     # 数据访问层
└── controller/     # 控制层（API接口）
```

## DTO分类详解

### 1. Request DTO (请求DTO)
**作用**：接收前端发送的请求参数
**特点**：
- 包含验证注解（`@NotNull`, `@Size`, `@Email`等）
- 字段名与前端JSON字段对应
- 只包含必要的字段，不暴露数据库结构
- 位于 `dto/request/` 目录

**示例**：
```java
// UserRegisterRequest.java
@Data
public class UserRegisterRequest {
    @NotBlank @Size(min=3, max=50)
    private String username;
    
    @NotBlank @Email
    private String email;
    
    @NotBlank @Size(min=6)
    private String password;
}
```

### 2. Response DTO (响应DTO)
**作用**：返回给前端的响应数据
**特点**：
- 包含前端需要的所有字段
- 可以组合多个Model的数据
- 包含业务状态信息（success, message, code等）
- 位于 `dto/response/` 目录

**示例**：
```java
// UserResponse.java
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private Boolean isAnonymous;
    private LocalDateTime createdAt;
}
```

### 3. Query DTO (查询DTO)
**作用**：接收查询参数（分页、排序、过滤）
**特点**：
- 包含分页参数（page, size）
- 包含排序参数（sortBy, sortOrder）
- 包含过滤条件
- 位于 `dto/query/` 目录

**示例**：
```java
// UserQueryRequest.java
@Data
public class UserQueryRequest {
    private Integer page = 1;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortOrder = "desc";
    private String username;
    private String email;
    private Boolean isActive;
}
```

## VO (Value Object) 详解

### 作用
- Service层内部的数据传递
- 复杂的业务数据组装
- 不直接对应数据库表

### 示例
```java
// UserProfileVO.java
@Data
public class UserProfileVO {
    private User user;
    private SoulPortrait latestPortrait;
    private List<PersonalizedRecommendation> recommendations;
    private UserStats stats;
    
    // 业务逻辑方法
    public int getTotalRecommendations() {
        return recommendations != null ? recommendations.size() : 0;
    }
}
```

## 完整的数据流转示例

### 场景：用户注册
```
前端 → Controller → Request DTO → Service → Model → Repository → 数据库
前端 ← Controller ← Response DTO ← Service ← Model ← Repository ← 数据库
```

### 代码示例

**1. Request DTO** (`UserRegisterRequest.java`)
```java
@Data
public class UserRegisterRequest {
    @NotBlank @Size(min=3, max=50)
    private String username;
    
    @NotBlank @Email
    private String email;
    
    @NotBlank @Size(min=6)
    private String password;
}
```

**2. Controller**
```java
@PostMapping("/register")
public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request) {
    UserResponse response = userService.register(request);
    return ResponseEntity.ok(response);
}
```

**3. Service**
```java
public UserResponse register(UserRegisterRequest request) {
    // 1. 验证业务逻辑
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new BusinessException("用户名已存在");
    }
    
    // 2. 创建Model对象
    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(encodePassword(request.getPassword()));
    
    // 3. 保存到数据库
    user = userRepository.save(user);
    
    // 4. 转换为Response DTO
    return convertToResponse(user);
}
```

**4. Response DTO** (`UserResponse.java`)
```java
@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private LocalDateTime createdAt;
    
    // 转换方法
    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
```

## DTO设计原则

### 1. 单一职责原则
- 每个DTO只负责一种类型的请求或响应
- 避免一个DTO包含太多不相关的字段

### 2. 最小暴露原则
- 只暴露前端需要的字段
- 不暴露数据库ID、内部状态等敏感信息

### 3. 验证前置原则
- 在Request DTO中进行数据验证
- 使用JSR-303验证注解
- 自定义验证逻辑放在Service层

### 4. 转换分离原则
- DTO与Model的转换使用专门的转换器
- 避免在Controller或Service中直接操作Model

### 5. 版本兼容原则
- 新增字段不影响旧版本API
- 废弃字段标记为`@Deprecated`
- 提供字段变更说明

## 常用DTO模式

### 1. 分页响应
```java
@Data
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
```

### 2. 统一响应
```java
@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer code;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setCode(200);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
```

### 3. 错误响应
```java
@Data
public class ErrorResponse {
    private boolean success = false;
    private String message;
    private Integer code;
    private String errorType;
    private LocalDateTime timestamp;
    private List<FieldError> fieldErrors;
    
    @Data
    public static class FieldError {
        private String field;
        private String message;
    }
}
```

## 工具类推荐

### 1. 转换工具
```java
@Component
public class UserConverter {
    public UserResponse toResponse(User user) {
        return UserResponse.from(user);
    }
    
    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
```

### 2. 验证工具
```java
@Component
public class RequestValidator {
    public void validate(UserRegisterRequest request) {
        // 自定义验证逻辑
        if (request.getUsername().contains("admin")) {
            throw new ValidationException("用户名不能包含admin");
        }
    }
}
```

## 最佳实践

### 1. 使用Lombok简化代码
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    // 字段定义
}
```

### 2. 使用Builder模式
```java
UserResponse response = UserResponse.builder()
    .id(user.getId())
    .username(user.getUsername())
    .email(user.getEmail())
    .build();
```

### 3. 使用MapStruct进行对象映射
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
</dependency>
```

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    User toEntity(UserRegisterRequest request);
}
```

## 下一步工作

### 1. 创建基础DTO
- `ApiResponse` - 统一响应格式
- `PageRequest` - 分页请求
- `PageResponse` - 分页响应
- `ErrorResponse` - 错误响应

### 2. 创建业务DTO
- 用户相关：注册、登录、更新、查询
- 问题相关：获取问题、提交答案
- 画像相关：生成画像、获取画像
- 推荐相关：获取推荐、提交反馈

### 3. 创建转换器
- 为每个业务领域创建转换器
- 使用MapStruct或手动转换

### 4. 创建验证器
- 自定义验证注解
- 业务逻辑验证器

---

**创建时间**: 2026-04-13  
**更新说明**: 初始版本，包含DTO架构指南和示例