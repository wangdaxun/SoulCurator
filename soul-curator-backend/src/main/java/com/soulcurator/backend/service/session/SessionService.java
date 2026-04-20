package com.soulcurator.backend.service.session;

import com.soulcurator.backend.model.session.UserSession;
import com.soulcurator.backend.model.user.User;
import com.soulcurator.backend.repository.session.UserSessionRepository;
import com.soulcurator.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 统一的Session管理服务
 * 负责创建、验证和管理用户会话
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    
    /**
     * 创建新的会话
     * @param gatewayType 灵魂之门入口类型
     * @param userAgent 用户代理
     * @param ipAddress IP地址
     * @param screenResolution 屏幕分辨率
     * @return 创建的会话ID
     */
    @Transactional
    public String createSession(String gatewayType, String userAgent, 
                                String ipAddress, String screenResolution) {
        log.info("创建新会话: gatewayType={}", gatewayType);
        
        // 1. 生成唯一的会话ID
        String sessionId = generateSessionId();
        
        // 2. 创建匿名用户（使用相同的sessionId）
        User user = createAnonymousUser(sessionId);
        
        // 3. 创建用户会话
        UserSession session = new UserSession();
        session.setSessionId(sessionId);
        session.setUserId(user.getId());
        session.setGatewayType(gatewayType);
        session.setStartedAt(LocalDateTime.now());
        session.setLastActiveAt(LocalDateTime.now());
        session.setFirstUserAgent(userAgent);
        session.setFirstIpAddress(ipAddress);
        session.setFirstScreenResolution(screenResolution);
        session.setIsCompleted(false);
        
        // 4. 保存会话
        UserSession savedSession = userSessionRepository.save(session);
        
        log.info("会话创建成功: sessionId={}, userId={}", 
                savedSession.getSessionId(), savedSession.getUserId());
        
        return savedSession.getSessionId();
    }
    
    /**
     * 验证会话是否存在且有效
     * @param sessionId 会话ID
     * @return 会话是否有效
     */
    public boolean validateSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return false;
        }
        
        Optional<UserSession> sessionOpt = userSessionRepository.findBySessionId(sessionId.trim());
        if (sessionOpt.isEmpty()) {
            log.warn("会话不存在: {}", sessionId);
            return false;
        }
        
        UserSession session = sessionOpt.get();
        
        // 检查会话是否已过期（例如超过24小时）
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        if (session.getStartedAt().isBefore(twentyFourHoursAgo)) {
            log.warn("会话已过期: {}, startedAt={}", sessionId, session.getStartedAt());
            return false;
        }
        
        // 更新最后活跃时间
        session.setLastActiveAt(LocalDateTime.now());
        userSessionRepository.save(session);
        
        return true;
    }
    
    /**
     * 获取会话信息
     * @param sessionId 会话ID
     * @return 会话信息
     */
    public Optional<UserSession> getSession(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId);
    }
    
    /**
     * 完成会话
     * @param sessionId 会话ID
     */
    @Transactional
    public void completeSession(String sessionId) {
        Optional<UserSession> sessionOpt = userSessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            session.setIsCompleted(true);
            session.setEndedAt(LocalDateTime.now());
            userSessionRepository.save(session);
            log.info("会话完成: {}", sessionId);
        }
    }
    
    /**
     * 创建匿名用户
     */
    private User createAnonymousUser(String sessionId) {
        User user = new User();
        user.setSessionId(sessionId);
        user.setIsAnonymous(true);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * 生成唯一的会话ID
     * 格式: soul_session_{timestamp}_{uuid}
     */
    private String generateSessionId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return "soul_session_" + timestamp + "_" + uuid;
    }
}