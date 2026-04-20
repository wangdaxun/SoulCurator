package com.soulcurator.backend.service.selection;

import com.soulcurator.backend.dto.selection.UserSelectionRequest;
import com.soulcurator.backend.dto.selection.UserSelectionResponse;
import com.soulcurator.backend.model.option.Option;
import com.soulcurator.backend.model.question.Question;
import com.soulcurator.backend.model.selection.UserSelection;
import com.soulcurator.backend.model.session.UserSession;
import com.soulcurator.backend.model.user.User;
import com.soulcurator.backend.repository.option.OptionRepository;
import com.soulcurator.backend.repository.question.QuestionRepository;
import com.soulcurator.backend.repository.selection.UserSelectionRepository;
import com.soulcurator.backend.repository.session.UserSessionRepository;
import com.soulcurator.backend.repository.user.UserRepository;
import com.soulcurator.backend.service.exploration.SoulExplorationService;
import com.soulcurator.backend.service.session.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户选择记录服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSelectionService {
    
    private final UserSelectionRepository userSelectionRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;
    private final SoulExplorationService soulExplorationService;
    private final SessionService sessionService;
    private final ObjectMapper objectMapper;
    
    /**
     * 记录用户选择
     */
    @Transactional
    public UserSelectionResponse recordSelections(UserSelectionRequest request) {
        log.info("记录用户选择: sessionId={}, gatewayType={}, selections={}", 
                request.getSessionId(), request.getGatewayType(), 
                request.getSelections().size());
        
        // 验证请求
        validateRequest(request);
        
        // 验证会话是否存在且有效
        if (!sessionService.validateSession(request.getSessionId())) {
            throw new IllegalArgumentException("会话无效或已过期，请重新开始探索: " + request.getSessionId());
        }
        
        // 获取会话的探索信息
        SoulExplorationService.ExplorationProgress progress = 
                soulExplorationService.getExplorationProgress(request.getSessionId());
        
        // 处理每个选择项
        List<UserSelection> savedSelections = new ArrayList<>();
        int stepNumber = 1;
        
        for (UserSelectionRequest.SelectionItem item : request.getSelections()) {
            // 验证问题和选项是否存在
            validateQuestionAndOption(item.getQuestionId(), item.getOptionId());
            
            // 检查是否已经选择过该问题
            Optional<UserSelection> existingSelection = 
                    userSelectionRepository.findBySessionIdAndQuestionId(
                            request.getSessionId(), item.getQuestionId());
            
            if (existingSelection.isPresent()) {
                log.debug("用户已选择过该问题，跳过: sessionId={}, questionId={}", 
                        request.getSessionId(), item.getQuestionId());
                continue;
            }
            
            // 创建选择记录
            UserSelection selection = createUserSelection(request, item, stepNumber);
            UserSelection saved = userSelectionRepository.save(selection);
            savedSelections.add(saved);
            
            stepNumber++;
        }
        
        log.info("用户选择记录成功: sessionId={}, savedCount={}", 
                request.getSessionId(), savedSelections.size());
        
        // 构建响应
        return buildResponse(request, savedSelections, progress);
    }
    
    /**
     * 获取用户选择记录
     */
    public UserSelectionResponse getSelections(String sessionId, String gatewayType) {
        log.debug("获取用户选择记录: sessionId={}, gatewayType={}", sessionId, gatewayType);
        
        List<UserSelection> selections;
        if (gatewayType != null && !gatewayType.trim().isEmpty()) {
            selections = userSelectionRepository.findBySessionIdAndGatewayType(sessionId, gatewayType);
        } else {
            selections = userSelectionRepository.findBySessionId(sessionId);
        }
        
        // 获取探索进度
        SoulExplorationService.ExplorationProgress progress = null;
        try {
            progress = soulExplorationService.getExplorationProgress(sessionId);
        } catch (Exception e) {
            log.warn("获取探索进度失败: {}", e.getMessage());
        }
        
        return buildResponse(sessionId, gatewayType, selections, progress);
    }
    
    /**
     * 获取用户选择摘要
     */
    public Map<String, Object> getSelectionSummary(String sessionId) {
        log.debug("获取用户选择摘要: sessionId={}", sessionId);
        
        List<UserSelection> selections = userSelectionRepository.findBySessionId(sessionId);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("sessionId", sessionId);
        summary.put("totalSelections", selections.size());
        
        // 按网关类型分组
        Map<String, Long> selectionsByGateway = selections.stream()
                .collect(Collectors.groupingBy(
                        UserSelection::getGatewayType,
                        Collectors.counting()
                ));
        summary.put("selectionsByGateway", selectionsByGateway);
        
        // 时间统计
        Optional<LocalDateTime> earliest = userSelectionRepository.findEarliestSelectionTimeBySessionId(sessionId);
        Optional<LocalDateTime> latest = userSelectionRepository.findLatestSelectionTimeBySessionId(sessionId);
        
        earliest.ifPresent(time -> summary.put("firstSelectionAt", time));
        latest.ifPresent(time -> summary.put("lastSelectionAt", time));
        
        if (earliest.isPresent() && latest.isPresent()) {
            long durationMs = latest.get().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                             earliest.get().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            summary.put("explorationDurationMs", durationMs);
        }
        
        // 获取已选择的问题ID
        List<Long> selectedQuestionIds = userSelectionRepository.findSelectedQuestionIdsBySessionId(sessionId);
        summary.put("selectedQuestionIds", selectedQuestionIds);
        summary.put("uniqueQuestionsSelected", selectedQuestionIds.size());
        
        return summary;
    }
    
    /**
     * 删除用户选择记录
     */
    @Transactional
    public void deleteSelections(String sessionId, String gatewayType) {
        log.info("删除用户选择记录: sessionId={}, gatewayType={}", sessionId, gatewayType);
        
        if (gatewayType != null && !gatewayType.trim().isEmpty()) {
            userSelectionRepository.deleteBySessionIdAndGatewayType(sessionId, gatewayType);
            log.info("删除特定网关类型的选择记录成功");
        } else {
            userSelectionRepository.deleteBySessionId(sessionId);
            log.info("删除所有选择记录成功");
        }
    }
    
    /**
     * 验证请求
     */
    private void validateRequest(UserSelectionRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("请求参数无效");
        }
        
        // 验证网关类型
        String cleanGatewayType = request.getCleanGatewayType();
        if (!isValidGatewayType(cleanGatewayType)) {
            throw new IllegalArgumentException("无效的灵魂之门入口类型: " + cleanGatewayType);
        }
    }
    
    /**
     * 验证问题和选项
     */
    private void validateQuestionAndOption(Long questionId, String optionId) {
        // 验证问题是否存在
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            throw new IllegalArgumentException("问题不存在: " + questionId);
        }
        
        // 验证选项是否存在
        Optional<Option> optionOpt = optionRepository.findById(optionId);
        if (optionOpt.isEmpty()) {
            throw new IllegalArgumentException("选项不存在: " + optionId);
        }
        
        // 验证选项是否属于该问题
        Option option = optionOpt.get();
        if (!questionId.equals(option.getQuestionId())) {
            throw new IllegalArgumentException("选项不属于该问题: optionId=" + optionId + ", questionId=" + questionId);
        }
    }
    
    /**
     * 创建用户选择记录
     */
    private UserSelection createUserSelection(UserSelectionRequest request, 
                                            UserSelectionRequest.SelectionItem item,
                                            int stepNumber) {
        UserSelection selection = new UserSelection();
        
        // 基础信息
        String sessionId = request.getCleanSessionId();
        selection.setSessionId(sessionId);
        selection.setGatewayType(request.getCleanGatewayType());
        selection.setQuestionId(item.getQuestionId());
        selection.setOptionId(item.getOptionId());
        selection.setStepNumber(item.getStepNumber() != null ? item.getStepNumber() : stepNumber);
        selection.setTimeSpentSeconds(item.getTimeSpentSeconds());
        
        // 根据sessionId获取用户ID
        Long userId = getUserIdBySessionId(sessionId);
        if (userId == null || userId <= 0) {
            log.warn("未找到会话对应的用户，创建新用户: sessionId={}", sessionId);
            userId = getOrCreateDefaultUser(sessionId);
        }
        selection.setUserId(userId);
        
        // 元数据
        if (item.getItemMetadata() != null && !item.getItemMetadata().isEmpty()) {
            try {
                selection.setMetadata(objectMapper.writeValueAsString(item.getItemMetadata()));
            } catch (JsonProcessingException e) {
                log.warn("序列化选项元数据失败: {}", e.getMessage());
            }
        }
        
        // 设置选择时间
        if (item.getSelectedAt() != null) {
            selection.setCreatedAt(LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(item.getSelectedAt()),
                    ZoneId.systemDefault()
            ));
        }
        
        return selection;
    }
    
    /**
     * 根据sessionId获取用户ID
     */
    private Long getUserIdBySessionId(String sessionId) {
        try {
            // 1. 先尝试从UserSession获取
            Optional<UserSession> sessionOpt = userSessionRepository.findBySessionId(sessionId);
            if (sessionOpt.isPresent()) {
                Long userId = sessionOpt.get().getUserId();
                if (userId != null && userId > 0) {
                    log.debug("从UserSession获取用户ID: sessionId={}, userId={}", sessionId, userId);
                    return userId;
                }
            }
            
            // 2. 尝试从User表获取
            Optional<User> userOpt = userRepository.findBySessionId(sessionId);
            if (userOpt.isPresent()) {
                Long userId = userOpt.get().getId();
                log.debug("从User表获取用户ID: sessionId={}, userId={}", sessionId, userId);
                return userId;
            }
            
            log.warn("未找到sessionId对应的用户: {}", sessionId);
            return null;
            
        } catch (Exception e) {
            log.error("获取用户ID失败: sessionId={}", sessionId, e);
            return null;
        }
    }
    
    /**
     * 获取或创建默认用户
     */
    private Long getOrCreateDefaultUser(String sessionId) {
        try {
            // 先检查是否已存在该sessionId的用户
            Optional<User> existingUser = userRepository.findBySessionId(sessionId);
            if (existingUser.isPresent()) {
                return existingUser.get().getId();
            }
            
            // 尝试创建新用户
            User user = new User();
            user.setSessionId(sessionId);
            user.setIsAnonymous(true);
            user.setIsActive(true);
            user.setCreatedAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(user);
            log.info("创建默认用户成功: userId={}, sessionId={}", savedUser.getId(), sessionId);
            
            return savedUser.getId();
            
        } catch (Exception e) {
            log.error("创建默认用户失败: sessionId={}", sessionId, e);
            
            // 尝试获取第一个可用的用户ID
            try {
                List<User> users = userRepository.findAll();
                if (!users.isEmpty()) {
                    return users.get(0).getId();
                }
            } catch (Exception ex) {
                log.error("获取用户列表失败", ex);
            }
            
            // 最后的手段：返回一个可能存在的用户ID
            // 注意：这需要确保数据库中有这个用户
            return 1L;
        }
    }
    
    /**
     * 构建响应
     */
    private UserSelectionResponse buildResponse(UserSelectionRequest request, 
                                               List<UserSelection> selections,
                                               SoulExplorationService.ExplorationProgress progress) {
        return buildResponse(request.getSessionId(), request.getGatewayType(), selections, progress);
    }
    
    /**
     * 构建响应
     */
    private UserSelectionResponse buildResponse(String sessionId, String gatewayType,
                                               List<UserSelection> selections,
                                               SoulExplorationService.ExplorationProgress progress) {
        UserSelectionResponse response = new UserSelectionResponse();
        response.setSessionId(sessionId);
        response.setGatewayType(gatewayType);
        response.setTotalSelections(selections.size());
        
        // 设置探索进度信息
        if (progress != null) {
            response.setCompletedQuestions(progress.getCompletedQuestions());
            response.setTotalQuestions(progress.getTotalQuestions());
            // 类型转换：Long -> Double
            if (progress.getCompletionPercentage() != null) {
                response.setCompletionPercentage(progress.getCompletionPercentage().doubleValue());
            }
        }
        
        // 时间信息
        Optional<LocalDateTime> earliest = userSelectionRepository.findEarliestSelectionTimeBySessionId(sessionId);
        Optional<LocalDateTime> latest = userSelectionRepository.findLatestSelectionTimeBySessionId(sessionId);
        
        earliest.ifPresent(response::setFirstSelectionAt);
        latest.ifPresent(response::setLastSelectionAt);
        
        if (earliest.isPresent() && latest.isPresent()) {
            long durationMs = latest.get().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                             earliest.get().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            response.setExplorationDurationMs(durationMs);
        }
        
        // 选择项详情
        List<UserSelectionResponse.SelectionSummary> summaries = selections.stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
        response.setSelections(summaries);
        
        return response;
    }
    
    /**
     * 转换为选择摘要
     */
    private UserSelectionResponse.SelectionSummary convertToSummary(UserSelection selection) {
        UserSelectionResponse.SelectionSummary summary = new UserSelectionResponse.SelectionSummary();
        summary.setQuestionId(selection.getQuestionId());
        summary.setOptionId(selection.getOptionId());
        summary.setStepNumber(selection.getStepNumber());
        summary.setSelectedAt(selection.getCreatedAt());
        summary.setTimeSpentSeconds(selection.getTimeSpentSeconds());
        
        // 尝试获取问题和选项的详细信息
        try {
            Optional<Question> questionOpt = questionRepository.findById(selection.getQuestionId());
            questionOpt.ifPresent(question -> summary.setQuestionTitle(question.getTitle()));
            
            Optional<Option> optionOpt = optionRepository.findById(selection.getOptionId());
            optionOpt.ifPresent(option -> summary.setOptionTitle(option.getTitle()));
        } catch (Exception e) {
            log.warn("获取问题或选项详情失败: {}", e.getMessage());
        }
        
        return summary;
    }
    
    /**
     * 验证网关类型是否有效
     */
    private boolean isValidGatewayType(String gatewayType) {
        // 这里应该从数据库或配置中获取有效的网关类型
        // 暂时使用硬编码的列表
        Set<String> validGatewayTypes = Set.of("movie", "literature", "music", "game");
        return validGatewayTypes.contains(gatewayType);
    }
}