---
name: soulcurator-manager
description: "Manage and monitor the SoulCurator project (灵魂策展人). Use when: checking project status, generating progress reports, reminding pending tasks, validating tech stack compatibility, or tracking 30-day project timeline. NOT for: general coding tasks (use coding-agent), unrelated project management."
metadata:
  {
    "openclaw":
      {
        "emoji": "🎨",
        "requires": { "bins": ["git", "node", "java"] },
      },
  }
---

# SoulCurator Project Manager

A specialized skill for managing the SoulCurator project (30-day development cycle).

## Project Overview

**SoulCurator (灵魂策展人)**
- **Goal**: "不是推荐你看什么，而是帮你发现**你想成为谁**"
- **Timeline**: 30 days (2026-03-29 to 2026-04-27)
- **Current Phase**: Core module development (Day 5/30)

**Tech Stack**:
- Backend: Spring Boot 3.x + Java 17 + PostgreSQL
- Frontend: Vue3 + TypeScript + Vite
- AI Integration: MCP servers + n8n workflows

## When to Use

✅ **USE this skill when:**

- "Check SoulCurator project status"
- "Generate daily/weekly progress report"
- "What's next for SoulCurator?"
- "Remind me of pending tasks"
- "Validate tech stack compatibility"
- "Track project timeline"

❌ **DON'T use this skill when:**

- General coding tasks → use coding-agent
- Unrelated project management
- Non-SoulCurator projects

## Commands

### 1. Project Status Check

```bash
# Check project directory structure
ls -la /Users/wangdaxun/SoulCurator/

# Check git status
cd /Users/wangdaxun/SoulCurator/soul-curator-frontend && git status

# Check memory archive status
ls -la /Users/wangdaxun/.openclaw/workspace/memory/ | tail -5
```

### 2. Progress Report Generation

```bash
# Generate daily report template
echo "## SoulCurator Daily Report - $(date +%Y-%m-%d)" > /tmp/soulcurator-report.md
echo "### Day $(($(date +%s -d "2026-03-29") - $(date +%s)) / 86400 + 1)/30" >> /tmp/soulcurator-report.md
echo "" >> /tmp/soulcurator-report.md
echo "### Completed Today:" >> /tmp/soulcurator-report.md
echo "- [ ] " >> /tmp/soulcurator-report.md
echo "" >> /tmp/soulcurator-report.md
echo "### Tomorrow's Focus:" >> /tmp/soulcurator-report.md
echo "- [ ] " >> /tmp/soulcurator-report.md
```

### 3. Pending Tasks Reminder

```bash
# Check MEMORY.md for pending tasks
grep -i "todo\|pending\|需要\|待办" /Users/wangdaxun/.openclaw/workspace/MEMORY.md | head -10

# Check today's memory file
if [ -f "/Users/wangdaxun/.openclaw/workspace/memory/$(date +%Y-%m-%d).md" ]; then
  grep -i "todo\|pending\|需要\|待办" "/Users/wangdaxun/.openclaw/workspace/memory/$(date +%Y-%m-%d).md"
fi
```

### 4. Tech Stack Validation

```bash
# Check Node.js version
node --version

# Check Java version
java --version

# Check Vue CLI availability
which vue

# Check Spring Boot CLI
which spring
```

### 5. Timeline Tracking

```bash
# Calculate days remaining
DAYS_PASSED=$(( ($(date +%s) - $(date +%s -d "2026-03-29")) / 86400 ))
DAYS_REMAINING=$((30 - DAYS_PASSED))
echo "Project Day: ${DAYS_PASSED}/30"
echo "Days Remaining: ${DAYS_REMAINING}"
```

## Workflow Examples

### Daily Standup Check

```bash
# 1. Check project status
cd /Users/wangdaxun/SoulCurator/soul-curator-frontend && git status

# 2. Generate progress report
echo "## Daily Update - $(date +%Y-%m-%d)" > /tmp/daily-update.md
echo "- Yesterday: " >> /tmp/daily-update.md
echo "- Today: " >> /tmp/daily-update.md
echo "- Blockers: " >> /tmp/daily-update.md

# 3. Check pending tasks
grep -i "todo" /Users/wangdaxun/.openclaw/workspace/memory/$(date +%Y-%m-%d).md 2>/dev/null || echo "No pending tasks found"
```

### Weekly Review

```bash
# Generate weekly summary
echo "## SoulCurator Weekly Summary - Week $(($(date +%V) - $(date +%V -d "2026-03-29")))" > /tmp/weekly-summary.md
echo "" >> /tmp/weekly-summary.md
echo "### Progress This Week:" >> /tmp/weekly-summary.md
find /Users/wangdaxun/.openclaw/workspace/memory -name "*.md" -mtime -7 -exec grep -l "SoulCurator" {} \; | xargs cat | grep -i "completed\|done\|finished" | head -20
```

## Integration with Memory System

This skill integrates with the daily memory archive system (cron job: dad3d83e-de02-43c5-a2e7-4c3c150b3474).

**Key Memory Files**:
- `MEMORY.md` - Long-term project memory
- `memory/YYYY-MM-DD.md` - Daily logs
- `soul-curator-progress-tracker.md` - Detailed progress tracking

## Notes

- Project started: 2026-03-29
- Current focus: Core module development
- Next milestone: MVP by Day 15 (2026-04-12)
- Final delivery: Day 30 (2026-04-27)