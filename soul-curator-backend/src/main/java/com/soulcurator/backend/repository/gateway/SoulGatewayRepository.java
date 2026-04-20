package com.soulcurator.backend.repository.gateway;

import com.soulcurator.backend.model.gateway.SoulGateway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 灵魂之门入口数据仓库
 */
@Repository
public interface SoulGatewayRepository extends JpaRepository<SoulGateway, String> {
    
    /**
     * 根据入口类型查找
     */
    Optional<SoulGateway> findByGatewayType(String gatewayType);
    
    /**
     * 查找所有启用的入口
     */
    List<SoulGateway> findByIsActiveTrue();
    
    /**
     * 根据是否启用查找入口
     */
    List<SoulGateway> findByIsActive(Boolean isActive);
    
    /**
     * 根据分类查找入口
     */
    List<SoulGateway> findByCategory(String category);
    
    /**
     * 根据显示顺序排序查找所有入口
     */
    List<SoulGateway> findAllByOrderByDisplayOrderAsc();
    
    /**
     * 查找所有启用的入口并按显示顺序排序
     */
    List<SoulGateway> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * 根据热度评分排序查找入口
     */
    List<SoulGateway> findAllByOrderByPopularityScoreDesc();
    
    /**
     * 检查入口类型是否存在
     */
    boolean existsByGatewayType(String gatewayType);
    
    /**
     * 检查入口是否启用
     */
    @Query("SELECT CASE WHEN COUNT(sg) > 0 THEN true ELSE false END " +
           "FROM SoulGateway sg WHERE sg.gatewayType = :gatewayType AND sg.isActive = true")
    boolean isGatewayActive(@Param("gatewayType") String gatewayType);
    
    /**
     * 更新入口的热度评分
     */
    @Query("UPDATE SoulGateway sg SET sg.popularityScore = sg.popularityScore + :increment " +
           "WHERE sg.gatewayType = :gatewayType")
    void incrementPopularityScore(@Param("gatewayType") String gatewayType, @Param("increment") Integer increment);
    
    /**
     * 根据多个入口类型查找
     */
    List<SoulGateway> findByGatewayTypeIn(List<String> gatewayTypes);
}