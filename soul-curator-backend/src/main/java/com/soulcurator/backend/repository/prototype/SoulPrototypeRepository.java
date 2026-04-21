package com.soulcurator.backend.repository.prototype;

import com.soulcurator.backend.model.prototype.SoulPrototype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 灵魂原型数据访问接口
 */
@Repository
public interface SoulPrototypeRepository extends JpaRepository<SoulPrototype, Long> {
    
    /**
     * 查找所有启用的灵魂原型，按显示顺序排序
     */
    List<SoulPrototype> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    /**
     * 根据名称查找灵魂原型
     */
    SoulPrototype findByName(String name);
    
    /**
     * 检查指定名称的原型是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据名称查找启用的灵魂原型
     */
    SoulPrototype findByNameAndIsActiveTrue(String name);
    
    /**
     * 统计启用的原型数量
     */
    long countByIsActiveTrue();
    
    /**
     * 获取所有原型（包括未启用的），按显示顺序排序
     */
    List<SoulPrototype> findAllByOrderByDisplayOrderAsc();
}