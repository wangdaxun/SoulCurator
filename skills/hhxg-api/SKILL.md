---
name: hhxg-api
description: "Access A-share market data via hhxg.top REST API. Use when: querying Chinese stock market data, daily reports, hot themes, margin trading data, market calendar, or financial news. Zero configuration, no token required. NOT for: international markets, real-time trading, or historical data analysis."
metadata:
  {
    "openclaw":
      {
        "emoji": "📊",
        "requires": { "bins": ["curl", "python3"] },
      },
  }
---

# hhxg.top API Skill

Access comprehensive A-share market data through hhxg.top REST API. Zero configuration, no token required.

## API Endpoints

**Base URL**: `https://hhxg.top`

| Endpoint | Description | Example |
|----------|-------------|---------|
| `GET /api/snapshot` | Daily market snapshot | `curl -s "https://hhxg.top/api/snapshot"` |
| `GET /api/margin` | Margin trading data (7 days) | `curl -s "https://hhxg.top/api/margin"` |
| `GET /api/news?limit=20` | Financial news (max 50) | `curl -s "https://hhxg.top/api/news?limit=10"` |
| `GET /api/calendar?type=trading` | Market calendar | `curl -s "https://hhxg.top/api/calendar?type=trading"` |

## Data Categories

### 📈 Market Snapshot (`/api/snapshot`)
- Market sentiment (赚钱效应)
- Hot themes (热门题材)
- Limit-up ladder (连板天梯)
- Sector performance (行业资金)
- Hot money tracking (游资龙虎榜)
- Focus news (焦点新闻)

### 💰 Margin Trading (`/api/margin`)
- 7-day margin balance changes
- Top net buying/selling stocks
- Margin trading trends

### 📰 Financial News (`/api/news`)
- Real-time financial news
- Market updates
- Macroeconomic news
- Company announcements

### 📅 Market Calendar (`/api/calendar`)
- Trading days
- Share unlock dates
- Earnings reports
- Futures delivery dates

## Usage Examples

### Command Line (curl)

```bash
# Get daily market snapshot
curl -s "https://hhxg.top/api/snapshot" | python3 -m json.tool

# Get margin trading data
curl -s "https://hhxg.top/api/margin" | jq '.data.top_net_buy[]'

# Get latest 10 news
curl -s "https://hhxg.top/api/news?limit=10" | jq '.data[].title'

# Check if tomorrow is trading day
curl -s "https://hhxg.top/api/calendar?type=trading" | jq '.data[] | select(.is_trading==true)'
```

### Python Script

```python
import requests
import json

def get_market_snapshot():
    url = "https://hhxg.top/api/snapshot"
    response = requests.get(url)
    data = response.json()
    
    if data['success']:
        snapshot = data['data']
        print(f"Market Date: {snapshot['date']}")
        print(f"Sentiment: {snapshot['market']['sentiment_label']} ({snapshot['market']['sentiment_index']}%)")
        print(f"Limit-up stocks: {snapshot['market']['limit_up']}")
        
        # Hot themes
        print("\nHot Themes:")
        for theme in snapshot['hot_themes'][:5]:
            print(f"  {theme['name']}: {theme['limitup_count']} limit-ups")
    
    return data
```

## Common Queries

### Market Overview
```bash
# "今天A股怎么样？"
curl -s "https://hhxg.top/api/snapshot" | jq '.data.ai_summary'

# "赚钱效应如何？"
curl -s "https://hhxg.top/api/snapshot" | jq '.data.market.sentiment_index'

# "热门题材有哪些？"
curl -s "https://hhxg.top/api/snapshot" | jq '.data.hot_themes[].name'
```

### Margin Trading
```bash
# "融资融券数据"
curl -s "https://hhxg.top/api/margin" | jq '.data'

# "融资净买入TOP"
curl -s "https://hhxg.top/api/margin" | jq '.data.top_net_buy[]'
```

### News & Calendar
```bash
# "最新财经快讯"
curl -s "https://hhxg.top/api/news?limit=5" | jq '.data[].title'

# "明天是交易日吗？"
curl -s "https://hhxg.top/api/calendar?type=trading" | jq '.data[] | select(.date=="2026-04-03")'
```

## Quick Response Scripts

### Daily Market Report
```bash
#!/bin/bash
# daily_report.sh
echo "📊 A股每日简报"
echo "================"

SNAPSHOT=$(curl -s "https://hhxg.top/api/snapshot")
DATE=$(echo $SNAPSHOT | jq -r '.data.date')
SENTIMENT=$(echo $SNAPSHOT | jq -r '.data.market.sentiment_label')
SENTIMENT_INDEX=$(echo $SNAPSHOT | jq -r '.data.market.sentiment_index')
LIMIT_UP=$(echo $SNAPSHOT | jq -r '.data.market.limit_up')

echo "日期: $DATE"
echo "赚钱效应: $SENTIMENT ($SENTIMENT_INDEX%)"
echo "涨停股数: $LIMIT_UP"
echo ""
echo "热门题材:"
echo $SNAPSHOT | jq -r '.data.hot_themes[0:3][] | "  \(.name): \(.limitup_count)只涨停"'
```

### Stock Screener (Based on Hot Themes)
```python
#!/usr/bin/env python3
# theme_stocks.py
import requests

def get_hot_theme_stocks():
    """Get stocks from hot themes"""
    url = "https://hhxg.top/api/snapshot"
    response = requests.get(url)
    data = response.json()
    
    if data['success']:
        themes = data['data']['hot_themes']
        
        print("🔥 热门题材龙头股:")
        for theme in themes[:5]:  # Top 5 themes
            print(f"\n{theme['name']} ({theme['limitup_count']}只涨停):")
            for stock in theme['top_stocks'][:3]:  # Top 3 stocks
                print(f"  {stock['name']} (净流入: {stock['net_yi']}亿)")
    
    return data
```

## Integration with OpenClaw

This skill can be triggered by phrases like:
- "今天A股怎么样？"
- "热门题材有哪些？"
- "融资融券数据"
- "最新财经新闻"
- "明天开盘吗？"

## Notes

- Data updates daily after market close (20:00)
- No authentication required
- Rate limiting: Be respectful of API usage
- Data is for reference only, not investment advice
- Source: 恢恢量化 (hhxg.top)

## Resources

- Official Website: https://hhxg.top
- Full Market Report: https://hhxg.top/2026/04/2026-04-02.html
- Stock Picker: https://hhxg.top/xuangu.html
- ETF Tools: https://hhxg.top/etf.html
- Hot Money Tracking: https://hhxg.top/youzi.html