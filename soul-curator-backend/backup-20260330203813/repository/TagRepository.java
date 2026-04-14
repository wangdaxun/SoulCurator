package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 简化的TagRepository，只包含基本CRUD操作
 * 避免复杂的JPQL查询导致启动失败
 */
@Repository
public interface SimpleTagRepository extends JpaRepository<TagEntity, Long> {
    
    Optional<TagEntity> findByName(String name);
    
    List<TagEntity> findByCategory(TagEntity.TagCategory category);
    
    List<TagEntity> findByNameContainingIgnoreCase(String name);
    
    List<TagEntity> findByParentId(Long parentId);
    
    List<TagEntity> findByAiGenerated(Boolean aiGenerated);
}