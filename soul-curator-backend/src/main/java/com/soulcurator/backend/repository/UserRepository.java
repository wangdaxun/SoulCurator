package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
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
     * 查找匿名用户
     */
    List<User> findByIsAnonymousTrue();
    
    /**
     * 查找注册用户
     */
    List<User> findByIsAnonymousFalse();
    
    /**
     * 查找活跃用户
     */
    List<User> findByIsActiveTrue();
    
    /**
     * 根据最后活跃时间查找用户
     */
    List<User> findByLastActiveAtAfter(LocalDateTime dateTime);
    
    /**
     * 统计用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isAnonymous = :isAnonymous")
    long countByIsAnonymous(@Param("isAnonymous") boolean isAnonymous);
    
    /**
     * 根据用户名或邮箱查找用户（用于登录）
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查会话ID是否存在
     */
    boolean existsBySessionId(String sessionId);
}