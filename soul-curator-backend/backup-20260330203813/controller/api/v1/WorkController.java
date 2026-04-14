package com.soulcurator.backend.controller.api.v1;

import com.soulcurator.backend.model.entity.WorkEntity;
import com.soulcurator.backend.model.enums.WorkType;
import com.soulcurator.backend.service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/works")
@Tag(name = "作品管理", description = "文艺作品管理接口")
public class WorkController {
    
    @Autowired
    private WorkService workService;
    
    @PostMapping
    @Operation(summary = "创建作品", description = "创建新的文艺作品")
    public ResponseEntity<WorkEntity> createWork(@RequestBody WorkEntity work) {
        WorkEntity created = workService.createWork(work);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新作品", description = "更新指定ID的作品信息")
    public ResponseEntity<WorkEntity> updateWork(@PathVariable Long id, @RequestBody WorkEntity work) {
        WorkEntity updated = workService.updateWork(id, work);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除作品", description = "删除指定ID的作品")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取作品详情", description = "根据ID获取作品详细信息")
    public ResponseEntity<WorkEntity> getWorkById(@PathVariable Long id) {
        WorkEntity work = workService.getWorkById(id);
        return ResponseEntity.ok(work);
    }
    
    @GetMapping("/external/{source}/{externalId}")
    @Operation(summary = "根据外部ID获取作品", description = "根据外部来源和ID获取作品")
    public ResponseEntity<WorkEntity> getWorkByExternalId(
            @PathVariable String source,
            @PathVariable String externalId) {
        WorkEntity work = workService.getWorkByExternalId(source, externalId);
        return ResponseEntity.ok(work);
    }
    
    @GetMapping
    @Operation(summary = "获取作品列表", description = "分页获取所有作品")
    public ResponseEntity<Page<WorkEntity>> getAllWorks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort sort = direction.equalsIgnoreCase("ASC") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<WorkEntity> works = workService.getWorksByPage(pageable);
        return ResponseEntity.ok(works);
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "按类型获取作品", description = "根据作品类型获取作品列表")
    public ResponseEntity<List<WorkEntity>> getWorksByType(@PathVariable WorkType type) {
        List<WorkEntity> works = workService.getWorksByType(type);
        return ResponseEntity.ok(works);
    }
    
    @GetMapping("/type/{type}/page")
    @Operation(summary = "按类型分页获取作品", description = "根据作品类型分页获取作品")
    public ResponseEntity<Page<WorkEntity>> getWorksByTypePage(
            @PathVariable WorkType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<WorkEntity> works = workService.getWorksByType(type, pageable);
        return ResponseEntity.ok(works);
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索作品", description = "根据关键词搜索作品")
    public ResponseEntity<List<WorkEntity>> searchWorks(@RequestParam String keyword) {
        List<WorkEntity> works = workService.searchWorks(keyword);
        return ResponseEntity.ok(works);
    }
    
    @GetMapping("/top-rated")
    @Operation(summary = "获取高评分作品", description = "获取评分最高的作品")
    public ResponseEntity<List<WorkEntity>> getTopRatedWorks(
            @RequestParam(defaultValue = "8.0") Double minScore,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<WorkEntity> works = workService.getTopRatedWorks(minScore, limit);
        return ResponseEntity.ok(works);
    }
    
    @GetMapping("/deepest")
    @Operation(summary = "获取深度作品", description = "获取深度分数最高的作品")
    public ResponseEntity<List<WorkEntity>> getDeepestWorks(
            @RequestParam(defaultValue = "7.0") Double minDepth,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<WorkEntity> works = workService.getDeepestWorks(minDepth, limit);
        return ResponseEntity.ok(works);
    }
    
    // @GetMapping("/emotion/{emotionId}")
    // @Operation(summary = "按情感获取作品", description = "根据情感标签获取作品")
    // public ResponseEntity<List<WorkEntity>> getWorksByEmotion(
    //         @PathVariable Long emotionId,
    //         @RequestParam(defaultValue = "10") int limit) {
        
    //     List<WorkEntity> works = workService.getWorksByEmotion(emotionId, limit);
    //     return ResponseEntity.ok(works);
    // }
    
    // @GetMapping("/theme/{themeId}")
    // @Operation(summary = "按主题获取作品", description = "根据主题标签获取作品")
    // public ResponseEntity<List<WorkEntity>> getWorksByTheme(
    //         @PathVariable Long themeId,
    //         @RequestParam(defaultValue = "10") int limit) {
        
    //     List<WorkEntity> works = workService.getWorksByTheme(themeId, limit);
    //     return ResponseEntity.ok(works);
    // }
    
    @GetMapping("/user/{userId}/favorites")
    @Operation(summary = "获取用户收藏", description = "获取用户收藏的作品")
    public ResponseEntity<List<WorkEntity>> getUserFavoriteWorks(@PathVariable Long userId) {
        List<WorkEntity> works = workService.getUserFavoriteWorks(userId);
        return ResponseEntity.ok(works);
    }
    
    @GetMapping("/user/{userId}/high-rated")
    @Operation(summary = "获取用户高评分作品", description = "获取用户评分高的作品")
    public ResponseEntity<List<WorkEntity>> getUserHighRatedWorks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "8.0") Double minScore) {
        
        List<WorkEntity> works = workService.getUserHighRatedWorks(userId, minScore);
        return ResponseEntity.ok(works);
    }
    
    @PostMapping("/{workId}/emotions/{tagId}")
    @Operation(summary = "添加情感标签", description = "为作品添加情感标签")
    public ResponseEntity<WorkEntity> addEmotionTag(
            @PathVariable Long workId,
            @PathVariable Long tagId) {
        
        WorkEntity work = workService.addEmotionTag(workId, tagId);
        return ResponseEntity.ok(work);
    }
    
    @DeleteMapping("/{workId}/emotions/{tagId}")
    @Operation(summary = "移除情感标签", description = "从作品移除情感标签")
    public ResponseEntity<WorkEntity> removeEmotionTag(
            @PathVariable Long workId,
            @PathVariable Long tagId) {
        
        WorkEntity work = workService.removeEmotionTag(workId, tagId);
        return ResponseEntity.ok(work);
    }
    
    @PostMapping("/{workId}/themes/{tagId}")
    @Operation(summary = "添加主题标签", description = "为作品添加主题标签")
    public ResponseEntity<WorkEntity> addThemeTag(
            @PathVariable Long workId,
            @PathVariable Long tagId) {
        
        WorkEntity work = workService.addThemeTag(workId, tagId);
        return ResponseEntity.ok(work);
    }
    
    @DeleteMapping("/{workId}/themes/{tagId}")
    @Operation(summary = "移除主题标签", description = "从作品移除主题标签")
    public ResponseEntity<WorkEntity> removeThemeTag(
            @PathVariable Long workId,
            @PathVariable Long tagId) {
        
        WorkEntity work = workService.removeThemeTag(workId, tagId);
        return ResponseEntity.ok(work);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "获取作品统计", description = "获取作品相关的统计数据")
    public ResponseEntity<Map<String, Object>> getWorkStats() {
        Map<String, Object> stats = workService.getWorkStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/type-distribution")
    @Operation(summary = "获取类型分布", description = "获取作品类型分布统计")
    public ResponseEntity<List<Map<String, Object>>> getTypeDistribution() {
        List<Map<String, Object>> distribution = workService.getTypeDistribution();
        return ResponseEntity.ok(distribution);
    }
    
    @GetMapping("/average-rating")
    @Operation(summary = "获取平均评分", description = "获取所有作品的平均评分")
    public ResponseEntity<Map<String, Double>> getAverageRating() {
        Double average = workService.getAverageRating();
        Map<String, Double> response = new HashMap<>();
        response.put("averageRating", average);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/sync/{source}/{externalId}")
    @Operation(summary = "同步外部作品", description = "从外部源同步作品数据")
    public ResponseEntity<Void> syncFromExternalSource(
            @PathVariable String source,
            @PathVariable String externalId) {
        
        workService.syncFromExternalSource(source, externalId);
        return ResponseEntity.accepted().build();
    }
}