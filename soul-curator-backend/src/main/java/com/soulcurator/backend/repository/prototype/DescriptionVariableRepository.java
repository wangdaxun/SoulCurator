package com.soulcurator.backend.repository.prototype;

import com.soulcurator.backend.model.prototype.DescriptionVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 描述变量数据访问接口
 */
@Repository
public interface DescriptionVariableRepository extends JpaRepository<DescriptionVariable, Long> {
    
    /**
     * 根据变量键和类型查找变量
     */
    DescriptionVariable findByVariableKeyAndVariableType(String variableKey, String variableType);
    
    /**
     * 查找所有启用的描述变量
     */
    List<DescriptionVariable> findByIsActiveTrue();
    
    /**
     * 根据变量类型查找启用的描述变量
     */
    List<DescriptionVariable> findByVariableTypeAndIsActiveTrue(String variableType);
    
    /**
     * 根据维度查找启用的描述变量
     */
    List<DescriptionVariable> findByDimensionAndIsActiveTrue(String dimension);
    
    /**
     * 检查指定键和类型的变量是否存在
     */
    boolean existsByVariableKeyAndVariableType(String variableKey, String variableType);
}