package com.soulcurator.backend.service.exploration;

import com.soulcurator.backend.dto.exploration.*;
import com.soulcurator.backend.dto.gateway.SoulGatewayResponse;
import com.soulcurator.backend.model.gateway.SoulGateway;
import com.soulcurator.backend.model.option.Option;
import com.soulcurator.backend.model.question.Question;
import com.soulcurator.backend.model.user.User;
import com.soulcurator.backend.model.session.UserSession;
import com.soulcurator.backend.repository.gateway.SoulGatewayRepository;
import com.soulcurator.backend.repository.option.OptionRepository;
import com.soulcurator.backend.repository.question.QuestionRepository;
import com.soulcurator.backend.repository.user.UserRepository;
import com.soulcurator.backend.repository.session.UserSessionRepository;
import com.soulcurator.backend.service.gateway.SoulGatewayService;
import com.soulcurator.backend.service.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 灵魂探索服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SoulExplorationService {

  private final SoulGatewayRepository soulGatewayRepository;
  private final QuestionRepository questionRepository;
  private final OptionRepository optionRepository;
  private final UserRepository userRepository;
  private final UserSessionRepository userSessionRepository;
  private final SoulGatewayService soulGatewayService;
  private final SessionService sessionService;

  /**
   * 开始灵魂探索
   */
  @Transactional
  public StartExplorationResponse startExploration(StartExplorationRequest request) {
    log.info("开始灵魂探索: gatewayType={}", request.getGatewayType());

    // 1. 验证请求
    if (!request.isValid()) {
      throw new IllegalArgumentException("入口类型不能为空");
    }

    String gatewayType = request.getCleanGatewayType();

    // 2. 检查入口是否存在且启用
    if (!soulGatewayService.isGatewayActive(gatewayType)) {
      throw new IllegalArgumentException("入口不存在或未启用: " + gatewayType);
    }

    // 3. 获取入口信息
    SoulGateway gateway = soulGatewayRepository.findByGatewayType(gatewayType)
        .orElseThrow(() -> new IllegalArgumentException("入口不存在: " + gatewayType));

    // 4. 创建或获取用户会话
    String sessionId = resolveSessionId(gatewayType, request);

    // 5. 获取会话信息
    UserSession session = userSessionRepository.findBySessionId(sessionId)
        .orElseThrow(() -> new IllegalStateException("会话创建失败: " + sessionId));

    // 6. 获取问题列表
    List<Question> questions = getQuestionsForGateway(gatewayType);

    // 7. 获取选项列表
    Map<Long, List<Option>> optionsByQuestion = getOptionsForQuestions(questions);

    // 8. 组装响应数据
    return buildResponse(session, gateway, questions, optionsByQuestion);
  }

  /**
   * 创建或获取用户
   */
  private User createOrGetUser(String existingSessionId) {
    // 如果有现有会话ID，尝试查找用户
    if (existingSessionId != null && !existingSessionId.trim().isEmpty()) {
      return userRepository.findBySessionId(existingSessionId.trim())
          .orElseGet(() -> createAnonymousUser());
    }

    // 创建新的匿名用户
    return createAnonymousUser();
  }

  /**
   * 创建新会话
   */
  private String createNewSession(String gatewayType, StartExplorationRequest request) {
    try {
      // 使用SessionService创建会话
      // 注意：这里需要获取实际的客户端信息，这里简化处理
      String userAgent = "unknown";
      String ipAddress = "0.0.0.0";
      String screenResolution = "unknown";

      String sessionId = sessionService.createSession(gatewayType, userAgent, ipAddress, screenResolution);
      log.info("通过SessionService创建新会话: sessionId={}, gatewayType={}", sessionId, gatewayType);

      return sessionId;
    } catch (Exception e) {
      log.error("创建新会话失败", e);
      throw new IllegalStateException("创建会话失败: " + e.getMessage());
    }
  }

  /**
   * 决定使用现有会话 ID 还是创建新会话
   */
  private String resolveSessionId(String gatewayType, StartExplorationRequest request) {
    if (request.getSessionId() != null && !request.getSessionId().trim().isEmpty()) {
      String sessionId = request.getCleanSessionId();
      if (sessionService.validateSession(sessionId)) {
        return sessionId;
      }
      log.warn("现有会话无效，创建新会话: {}", sessionId);
    }
    return createNewSession(gatewayType, request);
  }

  /**
   * 创建匿名用户
   */
  private User createAnonymousUser() {
    User user = new User();
    user.setSessionId(generateSessionId());
    user.setIsAnonymous(true);
    user.setIsActive(true);
    user.setLastActiveAt(LocalDateTime.now());

    User savedUser = userRepository.save(user);
    log.info("创建匿名用户: userId={}, sessionId={}", savedUser.getId(), savedUser.getSessionId());

    return savedUser;
  }

  /**
   * 生成会话ID
   */
  private String generateSessionId() {
    return "session_" + System.currentTimeMillis() + "_" +
        UUID.randomUUID().toString().substring(0, 8);
  }

  /**
   * 创建用户会话
   */
  private UserSession createUserSession(User user, String gatewayType) {
    UserSession session = new UserSession();
    session.setSessionId(user.getSessionId());
    session.setUserId(user.getId());
    session.setGatewayType(gatewayType);
    session.setStartedAt(LocalDateTime.now());
    session.setLastActiveAt(LocalDateTime.now());
    session.setIsCompleted(false);
    session.setTotalQuestions(5); // 默认5个问题

    // 记录设备信息（可以从请求中获取）
    session.setFirstUserAgent("unknown");
    session.setFirstIpAddress("0.0.0.0");
    session.setFirstScreenResolution("unknown");

    UserSession savedSession = userSessionRepository.save(session);
    log.info("创建用户会话: sessionId={}, gatewayType={}", savedSession.getSessionId(), gatewayType);

    return savedSession;
  }

  /**
   * 获取指定入口类型的问题列表
   */
  private List<Question> getQuestionsForGateway(String gatewayType) {
    // 获取启用的问题，按步骤编号排序
    List<Question> questions = questionRepository
        .findByGatewayTypeAndIsActiveTrueOrderByStepNumberAsc(gatewayType);

    if (questions.isEmpty()) {
      throw new IllegalStateException("该入口类型没有可用的问题: " + gatewayType);
    }

    // 限制最多返回5个问题（MVP版本）
    if (questions.size() > 5) {
      questions = questions.subList(0, 5);
    }

    log.debug("获取到{}个问题 for gatewayType={}", questions.size(), gatewayType);
    return questions;
  }

  /**
   * 获取问题的选项列表
   */
  private Map<Long, List<Option>> getOptionsForQuestions(List<Question> questions) {
    List<Long> questionIds = questions.stream()
        .map(Question::getId)
        .collect(Collectors.toList());

    // 获取所有选项
    List<Option> allOptions = optionRepository.findByQuestionIdIn(questionIds);

    // 按问题ID分组
    Map<Long, List<Option>> optionsByQuestion = allOptions.stream()
        .collect(Collectors.groupingBy(Option::getQuestionId));

    // 确保每个问题都有选项
    for (Question question : questions) {
      if (!optionsByQuestion.containsKey(question.getId())) {
        optionsByQuestion.put(question.getId(), Collections.emptyList());
        log.warn("问题没有选项: questionId={}, title={}", question.getId(), question.getTitle());
      }
    }

    return optionsByQuestion;
  }

  /**
   * 组装响应数据
   */
  private StartExplorationResponse buildResponse(UserSession session,
      SoulGateway gateway,
      List<Question> questions,
      Map<Long, List<Option>> optionsByQuestion) {
    // 转换入口信息
    SoulGatewayResponse gatewayResponse = SoulGatewayResponse.fromEntity(gateway);

    // 转换问题列表
    List<QuestionResponse> questionResponses = questions.stream()
        .map(question -> {
          QuestionResponse questionResponse = QuestionResponse.fromEntity(question);

          // 添加选项
          List<Option> options = optionsByQuestion.get(question.getId());
          if (options != null && !options.isEmpty()) {
            List<OptionResponse> optionResponses = options.stream()
                .map(OptionResponse::fromEntity)
                .collect(Collectors.toList());
            questionResponse.setOptions(optionResponses);
          }

          return questionResponse;
        })
        .collect(Collectors.toList());

    // 构建响应
    StartExplorationResponse response = StartExplorationResponse.success(
        session.getSessionId(),
        gateway.getGatewayType(),
        gatewayResponse,
        questionResponses);

    // 增加入口热度
    soulGatewayService.incrementPopularity(gateway.getGatewayType());

    log.info("灵魂探索开始成功: sessionId={}, gatewayType={}, questions={}",
        session.getSessionId(), gateway.getGatewayType(), questionResponses.size());

    return response;
  }

  /**
   * 获取探索进度
   */
  public ExplorationProgress getExplorationProgress(String sessionId) {
    log.debug("获取探索进度: sessionId={}", sessionId);

    UserSession session = userSessionRepository.findBySessionId(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("会话不存在: " + sessionId));

    ExplorationProgress progress = new ExplorationProgress();
    progress.setSessionId(sessionId);
    progress.setGatewayType(session.getGatewayType());
    progress.setStartedAt(session.getStartedAt());
    progress.setLastActiveAt(session.getLastActiveAt());
    progress.setTotalQuestions(session.getTotalQuestions());
    progress.setCompletedQuestions(session.getCompletedQuestions());
    progress.setIsCompleted(session.getIsCompleted());

    // 计算进度百分比
    if (session.getTotalQuestions() != null && session.getTotalQuestions() > 0) {
      double percentage = (double) session.getCompletedQuestions() / session.getTotalQuestions() * 100;
      progress.setCompletionPercentage(Math.min(100, Math.round(percentage)));
    } else {
      progress.setCompletionPercentage(0L);
    }

    return progress;
  }

  /**
   * 探索进度信息
   */
  @lombok.Data
  public static class ExplorationProgress {
    private String sessionId;
    private String gatewayType;
    private LocalDateTime startedAt;
    private LocalDateTime lastActiveAt;
    private Integer totalQuestions;
    private Integer completedQuestions;
    private Boolean isCompleted;
    private Long completionPercentage; // 完成百分比
  }
}