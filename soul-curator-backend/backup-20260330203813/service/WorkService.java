package com.soulcurator.backend.service;

import com.soulcurator.backend.model.entity.WorkEntity;
import com.soulcurator.backend.model.enums.WorkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WorkService {
    
    WorkEntity createWork(WorkEntity work);
    
    WorkEntity updateWork(Long id, WorkEntity work);
    
    void deleteWork(Long id);
    
    WorkEntity getWorkById(Long id);
    
    WorkEntity getWorkByExternalId(String source, String externalId);
    
    List<WorkEntity> getAllWorks();
    
    Page<WorkEntity> getWorksByPage(Pageable pageable);
    
    List<WorkEntity> getWorksByType(WorkType type);
    
    Page<WorkEntity> getWorksByType(WorkType type, Pageable pageable);
    
    List<WorkEntity> searchWorks(String keyword);
    
    Page<WorkEntity> searchWorks(String keyword, Pageable pageable);
    
    List<WorkEntity> getTopRatedWorks(Double minScore, int limit);
    
    List<WorkEntity> getDeepestWorks(Double minDepth, int limit);
    
    // List<WorkEntity> getWorksByEmotion(Long emotionId, int limit);
    
    // List<WorkEntity> getWorksByTheme(Long themeId, int limit);
    
    List<WorkEntity> getUserFavoriteWorks(Long userId);
    
    List<WorkEntity> getUserHighRatedWorks(Long userId, Double minScore);
    
    WorkEntity addEmotionTag(Long workId, Long tagId);
    
    WorkEntity removeEmotionTag(Long workId, Long tagId);
    
    WorkEntity addThemeTag(Long workId, Long tagId);
    
    WorkEntity removeThemeTag(Long workId, Long tagId);
    
    WorkEntity addStyleTag(Long workId, Long tagId);
    
    WorkEntity removeStyleTag(Long workId, Long tagId);
    
    WorkEntity updateAiAnalysis(Long workId, String aiAnalysisJson);
    
    Map<String, Object> getWorkStats();
    
    List<Map<String, Object>> getTypeDistribution();
    
    Double getAverageRating();
    
    void syncFromExternalSource(String source, String externalId);
    
    void batchSyncFromExternalSource(String source, List<String> externalIds);
}