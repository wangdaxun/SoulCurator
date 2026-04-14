#!/bin/bash
# SoulCurator项目状态检查脚本

echo "=== SoulCurator Project Status Check ==="
echo ""

# 1. 项目基本信息
echo "📊 Project Overview:"
echo "  Name: SoulCurator (灵魂策展人)"
echo "  Timeline: 30 days (2026-03-29 to 2026-04-27)"
DAYS_PASSED=$(( ($(date +%s) - $(date +%s -d "2026-03-29")) / 86400 ))
DAYS_REMAINING=$((30 - DAYS_PASSED))
echo "  Current: Day ${DAYS_PASSED}/30 (${DAYS_REMAINING} days remaining)"
echo ""

# 2. 项目目录检查
echo "📁 Project Structure:"
if [ -d "/Users/wangdaxun/SoulCurator" ]; then
    echo "  ✓ SoulCurator directory exists"
    ls -la /Users/wangdaxun/SoulCurator/ | grep -E "^(d|-)" | head -10
else
    echo "  ✗ SoulCurator directory not found!"
fi
echo ""

# 3. Git状态检查
echo "🔧 Git Status:"
if [ -d "/Users/wangdaxun/SoulCurator/soul-curator-frontend" ]; then
    cd /Users/wangdaxun/SoulCurator/soul-curator-frontend
    git status --short 2>/dev/null || echo "  Not a git repository"
else
    echo "  Frontend directory not found"
fi
echo ""

# 4. 技术栈检查
echo "⚙️ Tech Stack Check:"
echo -n "  Node.js: "
node --version 2>/dev/null || echo "Not installed"
echo -n "  Java: "
java --version 2>/dev/null | head -1 || echo "Not installed"
echo -n "  Vue CLI: "
which vue >/dev/null 2>&1 && echo "Installed" || echo "Not installed"
echo ""

# 5. 记忆系统检查
echo "🧠 Memory System:"
echo -n "  Today's memory: "
if [ -f "/Users/wangdaxun/.openclaw/workspace/memory/$(date +%Y-%m-%d).md" ]; then
    echo "Exists ($(wc -l < "/Users/wangdaxun/.openclaw/workspace/memory/$(date +%Y-%m-%d).md") lines)"
else
    echo "Not created yet"
fi
echo -n "  Long-term memory: "
if [ -f "/Users/wangdaxun/.openclaw/workspace/MEMORY.md" ]; then
    echo "Exists ($(wc -l < "/Users/wangdaxun/.openclaw/workspace/MEMORY.md") lines)"
else
    echo "Not found"
fi
echo ""

# 6. 待办事项
echo "📝 Pending Tasks:"
if [ -f "/Users/wangdaxun/.openclaw/workspace/memory/$(date +%Y-%m-%d).md" ]; then
    grep -i "todo\|pending\|需要\|待办\|next" "/Users/wangdaxun/.openclaw/workspace/memory/$(date +%Y-%m-%d).md" | head -5 || echo "  No pending tasks found in today's memory"
else
    echo "  No memory file for today"
fi
echo ""

echo "=== Status Check Complete ==="