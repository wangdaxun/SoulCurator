#!/bin/bash

echo "验证Repository查询语句..."
echo "=========================="

# 检查所有Repository文件
for repo_file in src/main/java/com/soulcurator/backend/repository/*.java; do
    echo "检查: $(basename $repo_file)"
    
    # 提取所有@Query注解
    grep -n "@Query" "$repo_file" | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        query=$(echo "$line" | sed 's/.*@Query("\(.*\)").*/\1/')
        
        echo "  行 $line_num: $query"
        
        # 检查查询中的属性是否存在（简单检查）
        # 这里可以添加更复杂的验证逻辑
    done
    
    echo ""
done

echo "检查实体类属性..."
echo "=================="

# 检查实体类
for model_file in src/main/java/com/soulcurator/backend/model/*.java; do
    model_name=$(basename "$model_file" .java)
    echo "实体类: $model_name"
    
    # 提取所有属性
    grep -E "private.*;" "$model_file" | sed 's/.*private[[:space:]]*[^[:space:]]*[[:space:]]*\([^[:space:];]*\).*/\1/' | while read -r field; do
        echo "  - $field"
    done
    
    echo ""
done