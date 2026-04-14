#!/bin/bash

echo "🚀 SoulCurator 前端开发环境设置脚本"
echo "======================================"

# 检查是否在项目目录
if [ ! -f "package.json" ]; then
  echo "❌ 错误：请在项目根目录运行此脚本"
  exit 1
fi

echo "📦 步骤1: 安装开发依赖..."
echo "--------------------------"

# 代码质量和格式化
echo "安装 ESLint 和 Prettier..."
npm install -D eslint eslint-plugin-vue @babel/eslint-parser prettier

# 类型检查和文档
echo "安装 TypeScript 和 JSDoc..."
npm install -D typescript jsdoc

# 测试框架
echo "安装测试框架..."
npm install -D vitest @vue/test-utils jsdom @vitest/ui

# Git hooks
echo "安装 Git hooks 工具..."
npm install -D husky lint-staged

echo ""
echo "✅ 依赖安装完成"
echo ""

echo "⚙️ 步骤2: 创建配置文件..."
echo "--------------------------"

# ESLint 配置
cat > .eslintrc.js << 'EOF'
module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended'
  ],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    parser: '@babel/eslint-parser'
  },
  rules: {
    // Vue 相关规则
    'vue/multi-word-component-names': 'off',
    'vue/no-v-html': 'off',
    
    // JavaScript 规则
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-unused-vars': 'warn',
    
    // 代码风格
    'indent': ['error', 2],
    'quotes': ['error', 'single'],
    'semi': ['error', 'never']
  }
}
EOF
echo "✅ 创建 .eslintrc.js"

# Prettier 配置
cat > .prettierrc << 'EOF'
{
  "semi": false,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "none",
  "printWidth": 100,
  "htmlWhitespaceSensitivity": "ignore"
}
EOF
echo "✅ 创建 .prettierrc"

cat > .prettierignore << 'EOF'
node_modules
dist
*.log
.DS_Store
EOF
echo "✅ 创建 .prettierignore"

# TypeScript 配置（仅用于检查）
cat > tsconfig.json << 'EOF'
{
  "compilerOptions": {
    "target": "ES2020",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    
    /* 类型检查配置 */
    "strict": false,
    "noImplicitAny": false,
    "strictNullChecks": false,
    "strictFunctionTypes": false,
    "strictBindCallApply": false,
    "strictPropertyInitialization": false,
    "noImplicitThis": false,
    "useUnknownInCatchVariables": false,
    "alwaysStrict": false,
    
    /* 模块解析 */
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    
    /* 互操作性 */
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    
    /* JavaScript 支持 */
    "allowJs": true,
    "checkJs": true,
    
    /* 路径别名 */
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": [
    "src/**/*.js",
    "src/**/*.vue"
  ],
  "exclude": [
    "node_modules",
    "dist"
  ]
}
EOF
echo "✅ 创建 tsconfig.json"

# JSDoc 配置
cat > jsdoc.config.json << 'EOF'
{
  "plugins": [],
  "recurseDepth": 10,
  "source": {
    "include": ["src"],
    "includePattern": ".+\\.(js|vue)$",
    "excludePattern": "(^|\\/|\\\\)_"
  },
  "sourceType": "module",
  "tags": {
    "allowUnknownTags": true,
    "dictionaries": ["jsdoc", "closure"]
  },
  "templates": {
    "cleverLinks": false,
    "monospaceLinks": false
  },
  "opts": {
    "destination": "./docs/",
    "recurse": true,
    "readme": "./README.md"
  }
}
EOF
echo "✅ 创建 jsdoc.config.json"

# Vitest 配置
cat > vitest.config.js << 'EOF'
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  test: {
    environment: 'jsdom',
    globals: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'src/main.js'
      ]
    }
  }
})
EOF
echo "✅ 创建 vitest.config.js"

echo ""
echo "🔧 步骤3: 更新 package.json 脚本..."
echo "-----------------------------------"

# 备份原始 package.json
cp package.json package.json.backup

# 使用 Node.js 更新 scripts
node << 'EOF'
const fs = require('fs')
const packageJson = JSON.parse(fs.readFileSync('package.json', 'utf8'))

// 更新 scripts
packageJson.scripts = {
  ...packageJson.scripts,
  "lint": "eslint . --ext .js,.vue",
  "lint:fix": "eslint . --ext .js,.vue --fix",
  "format": "prettier --write .",
  "type-check": "tsc --noEmit",
  "test": "vitest",
  "test:ui": "vitest --ui",
  "docs": "jsdoc -c jsdoc.config.json"
}

// 添加 lint-staged 配置
packageJson["lint-staged"] = {
  "*.{js,vue}": [
    "eslint --fix",
    "prettier --write"
  ]
}

fs.writeFileSync('package.json', JSON.stringify(packageJson, null, 2))
EOF

echo "✅ 更新 package.json"

echo ""
echo "🔧 步骤4: 设置 Git hooks..."
echo "---------------------------"

# 初始化 husky
npx husky init 2>/dev/null || true

# 创建 pre-commit hook
cat > .husky/pre-commit << 'EOF'
#!/bin/sh
. "$(dirname "$0")/_/husky.sh"

echo "🔍 运行代码检查..."
npx lint-staged

echo "✅ 代码检查完成"
EOF

chmod +x .husky/pre-commit
echo "✅ 设置 Git pre-commit hook"

echo ""
echo "📁 步骤5: 创建基础目录结构..."
echo "-----------------------------"

# 创建基础目录
mkdir -p src/{core,services,utils,constants,types}
mkdir -p src/components/{ui,domain,layout,shared}
mkdir -p __tests__/{components,utils,stores}

echo "✅ 创建目录结构"

echo ""
echo "📝 步骤6: 创建示例文件..."
echo "-------------------------"

# 创建类型定义示例
cat > src/types/quiz.js << 'EOF'
/**
 * 问题类型
 * @typedef {'movie'|'literature'|'music'|'game'} QuestionType
 */

/**
 * 问题选项
 * @typedef {Object} Option
 * @property {string} id - 选项ID
 * @property {string} text - 选项文本
 * @property {number} value - 选项值
 * @property {string} [emotion] - 关联情感（可选）
 */

/**
 * 问题
 * @typedef {Object} Question
 * @property {string} id - 问题ID
 * @property {QuestionType} type - 问题类型
 * @property {string} text - 问题文本
 * @property {Option[]} options - 选项列表
 */

/**
 * 用户答案
 * @typedef {Object} Answer
 * @property {string} questionId - 问题ID
 * @property {string} optionId - 选项ID
 * @property {number} timestamp - 时间戳
 */

export {}
EOF
echo "✅ 创建 src/types/quiz.js"

# 创建工具函数示例
cat > src/utils/format.js << 'EOF'
/**
 * 格式化百分比
 * @param {number} value - 小数形式的百分比 (0-1)
 * @returns {string} 格式化后的百分比字符串
 */
export function formatPercentage(value) {
  return `${(value * 100).toFixed(2)}%`
}

/**
 * 格式化日期
 * @param {Date|string|number} date - 日期
 * @param {string} [format='YYYY-MM-DD'] - 格式
 * @returns {string} 格式化后的日期
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
}
EOF
echo "✅ 创建 src/utils/format.js"

# 创建测试示例
cat > __tests__/utils/format.test.js << 'EOF'
import { describe, it, expect } from 'vitest'
import { formatPercentage } from '@/utils/format.js'

describe('formatPercentage', () => {
  it('应该格式化百分比', () => {
    expect(formatPercentage(0.5)).toBe('50.00%')
    expect(formatPercentage(0.1234)).toBe('12.34%')
    expect(formatPercentage(1)).toBe('100.00%')
  })
  
  it('应该处理边界值', () => {
    expect(formatPercentage(0)).toBe('0.00%')
    expect(formatPercentage(-0.1)).toBe('-10.00%')
    expect(formatPercentage(1.5)).toBe('150.00%')
  })
})
EOF
echo "✅ 创建 __tests__/utils/format.test.js"

echo ""
echo "🎉 开发环境设置完成！"
echo "======================"
echo ""
echo "📋 可用命令:"
echo "  npm run dev        # 启动开发服务器"
echo "  npm run lint       # 代码检查"
echo "  npm run lint:fix   # 自动修复代码问题"
echo "  npm run format     # 代码格式化"
echo "  npm run type-check # 类型检查"
echo "  npm run test       # 运行测试"
echo "  npm run test:ui    # 打开测试UI"
echo "  npm run docs       # 生成文档"
echo ""
echo "🔧 Git hooks 已启用:"
echo "  - 提交前自动运行代码检查和格式化"
echo ""
echo "📁 创建的基础结构:"
echo "  src/types/         # 类型定义 (JSDoc)"
echo "  src/utils/         # 工具函数"
echo "  __tests__/         # 测试文件"
echo ""
echo "⚠️  注意:"
echo "  - 原始 package.json 已备份为 package.json.backup"
echo "  - 建议运行一次完整检查: npm run lint && npm run type-check"
echo ""
echo "🚀 开始开发吧！"