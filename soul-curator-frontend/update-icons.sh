#!/bin/bash

# 更新所有组件中的图标使用
echo "更新组件中的图标使用..."

# 1. 更新QuestionCard.vue
sed -i '' 's/<i data-lucide="sparkles" class="h-4 w-4"><\/i>/<Icon name="sparkles" :size="16" \/>/g' src/components/entry/QuestionCard.vue
sed -i '' 's/<i data-lucide="check" class="h-4 w-4"><\/i>/<Icon name="check" :size="16" \/>/g' src/components/entry/QuestionCard.vue
sed -i '' 's/<i data-lucide="x" class="h-3 w-3"><\/i>/<Icon name="x" :size="12" \/>/g' src/components/entry/QuestionCard.vue

# 2. 更新OptionCard.vue
sed -i '' 's/<i data-lucide="check-circle-2" class="w-5 h-5"><\/i>/<Icon name="check-circle-2" :size="20" \/>/g' src/components/entry/OptionCard.vue

# 3. 添加Icon导入到QuestionCard.vue
if ! grep -q "import Icon from" src/components/entry/QuestionCard.vue; then
    sed -i '' '/<script setup>/a\
import Icon from "@/components/shared/Icon.vue"' src/components/entry/QuestionCard.vue
fi

# 4. 添加Icon导入到OptionCard.vue
if ! grep -q "import Icon from" src/components/entry/OptionCard.vue; then
    sed -i '' '/<script setup>/a\
import Icon from "@/components/shared/Icon.vue"' src/components/entry/OptionCard.vue
fi

echo "图标更新完成！"