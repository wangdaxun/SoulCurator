package com.soulcurator.backend.dto;

/**
 * 使用Java 17 Record的DTO示例
 * Record会自动生成：构造函数、getter、equals、hashCode、toString
 */
// @ToString
// @Data
// @Setter
// @EqualsAndHashCode
// @Getter
// @AllArgsConstructor
// @NoArgsConstructor
public record UserDTO(
    String id,
    String username,
    String email,
    Integer age) {
  // 可以添加自定义方法
  public String getDisplayName() {
    return username != null ? username : email;
  }

}