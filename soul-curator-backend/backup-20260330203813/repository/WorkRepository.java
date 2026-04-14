package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.entity.WorkEntity;
import com.soulcurator.backend.model.enums.WorkType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<WorkEntity, Long> {
    
    Optional<WorkEntity> findByExternalSourceAndExternalId(String externalSource, String externalId);
    
    List<WorkEntity> findByType(WorkType type);
    
    Page<WorkEntity> findByType(WorkType type, Pageable pageable);
    
    List<WorkEntity> findByTitleContainingIgnoreCase(String title);
    
    Page<WorkEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    List<WorkEntity> findByReleaseYear(Integer releaseYear);
    
    @Query("SELECT w FROM WorkEntity w WHERE w.ratingScore >= :minScore ORDER BY w.ratingScore DESC")
    List<WorkEntity> findTopRatedWorks(@Param("minScore") Double minScore, Pageable pageable);
    
    @Query("SELECT w FROM WorkEntity w WHERE w.depthScore >= :minDepth ORDER BY w.depthScore DESC")
    List<WorkEntity> findDeepestWorks(@Param("minDepth") Double minDepth, Pageable pageable);
    
    @Query("SELECT w FROM WorkEntity w JOIN w.emotions e WHERE e.id = :emotionId")
    List<WorkEntity> findByEmotionId(@Param("emotionId") Long emotionId, Pageable pageable);
    
    @Query("SELECT w FROM WorkEntity w JOIN w.themes t WHERE t.id = :themeId")
    List<WorkEntity> findByThemeId(@Param("themeId") Long themeId, Pageable pageable);
    
    @Query("SELECT w FROM WorkEntity w WHERE w.id IN (SELECT fw.id FROM UserEntity u JOIN u.favoriteWorks fw WHERE u.id = :userId)")
    List<WorkEntity> findUserFavorites(@Param("userId") Long userId);
    
    @Query("SELECT w FROM WorkEntity w WHERE w.id IN (SELECT r.work.id FROM UserRatingEntity r WHERE r.user.id = :userId AND r.score >= :minScore)")
    List<WorkEntity> findUserHighRatedWorks(@Param("userId") Long userId, @Param("minScore") Double minScore);
    
    @Query(value = "SELECT w.* FROM works w " +
                   "WHERE to_tsvector('simple', w.title || ' ' || COALESCE(w.description, '')) @@ plainto_tsquery('simple', :query) " +
                   "ORDER BY ts_rank(to_tsvector('simple', w.title || ' ' || COALESCE(w.description, '')), plainto_tsquery('simple', :query)) DESC",
           nativeQuery = true)
    List<WorkEntity> fullTextSearch(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT COUNT(w) FROM WorkEntity w WHERE w.type = :type")
    Long countByType(@Param("type") WorkType type);
    
    @Query("SELECT AVG(w.ratingScore) FROM WorkEntity w WHERE w.ratingScore IS NOT NULL")
    Double getAverageRating();
    
    @Query("SELECT w.type, COUNT(w) FROM WorkEntity w GROUP BY w.type")
    List<Object[]> countByTypeGroup();
}