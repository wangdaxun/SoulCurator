package com.soulcurator.backend.repository.user;

import com.soulcurator.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据仓库
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据会话ID查找用户
     */
    Optional<User> findBySessionId(String sessionId);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 查找所有匿名用户
     */
    List<User> findByIsAnonymousTrue();
    
    /**
     * 查找所有注册用户
     */
    List<User> findByIsAnonymousFalse();
    
    /**
     * 查找所有活跃用户
     */
    List<User> findByIsActiveTrue();
    
    /**
     * 根据最后活跃时间查找用户
     */
    List<User> findByLastActiveAtAfter(LocalDateTime lastActiveAt);
    
    /**
     * 统计用户数量
     */
    long count();
    
    /**
     * 统计匿名用户数量
     */
    long countByIsAnonymousTrue();
    
    /**
     * 统计注册用户数量
     */
    long countByIsAnonymousFalse();
    
    /**
     * 统计活跃用户数量
     */
    long countByIsActiveTrue();
    
    /**
     * 更新用户最后活跃时间
     */
    @Query("UPDATE User u SET u.lastActiveAt = :lastActiveAt WHERE u.id = :userId")
    void updateLastActiveAt(@Param("userId") Long userId, @Param("lastActiveAt") LocalDateTime lastActiveAt);
    
    /**
     * 检查会话ID是否存在
     */
    boolean existsBySessionId(String sessionId);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
}