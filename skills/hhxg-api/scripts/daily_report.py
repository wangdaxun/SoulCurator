#!/usr/bin/env python3
"""
A股每日简报 - 使用hhxg.top API
"""

import requests
import json
from datetime import datetime

def get_market_snapshot():
    """获取市场快照"""
    url = "https://hhxg.top/api/snapshot"
    
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        return response.json()
    except Exception as e:
        print(f"❌ API请求失败: {e}")
        return None

def format_market_report(data):
    """格式化市场报告"""
    if not data or not data.get('success'):
        return "无法获取市场数据"
    
    snapshot = data['data']
    
    report = []
    report.append("📊 A股每日市场简报")
    report.append("=" * 50)
    report.append(f"📅 日期: {snapshot['date']}")
    report.append(f"🔄 更新: {snapshot['meta']['update_time']}")
    report.append("")
    
    # 市场情绪
    market = snapshot['market']
    report.append("🎯 市场情绪:")
    report.append(f"  赚钱效应: {market['sentiment_label']} ({market['sentiment_index']}%)")
    report.append(f"  涨停股数: {market['limit_up']} 只")
    report.append(f"  跌停股数: {market['limit_down']} 只")
    report.append(f"  炸板数: {market.get('fried', 'N/A')} 只")
    report.append("")
    
    # AI摘要
    ai_summary = snapshot['ai_summary']
    report.append("🤖 AI市场摘要:")
    report.append(f"  {ai_summary['market_state']}")
    report.append(f"  资金方向: {ai_summary['focus_direction']}")
    report.append(f"  活跃题材: {ai_summary['theme_focus']}")
    report.append(f"  游资动向: {ai_summary['hotmoney_state']}")
    report.append(f"  新闻焦点: {ai_summary['news_highlight']}")
    report.append("")
    
    # 热门题材
    hot_themes = snapshot['hot_themes'][:5]
    report.append("🔥 热门题材 (前5):")
    for theme in hot_themes:
        report.append(f"  {theme['name']}: {theme['limitup_count']}只涨停")
        if theme['top_stocks']:
            top_stock = theme['top_stocks'][0]
            report.append(f"    龙头: {top_stock['name']} (净流入: {top_stock['net_yi']}亿)")
    report.append("")
    
    # 行业表现
    sectors = snapshot['sectors']
    if sectors and len(sectors) > 0:
        report.append("🏢 行业表现:")
        
        # 强势行业
        strong_industries = sectors[0]['strong'][:3]
        report.append("  强势行业:")
        for industry in strong_industries:
            report.append(f"    {industry['name']}: 净流入{industry['net_yi']}亿")
        
        # 弱势行业
        weak_industries = sectors[0]['weak'][:3]
        report.append("  弱势行业:")
        for industry in weak_industries:
            report.append(f"    {industry['name']}: 净流出{abs(industry['net_yi'])}亿")
        report.append("")
    
    # 连板天梯
    ladder = snapshot['ladder']
    report.append("📈 连板天梯:")
    report.append(f"  最高连板: {ladder['max_streak']} 板")
    if ladder['top_streak']:
        report.append(f"  龙头股: {ladder['top_streak']['name']} ({ladder['top_streak']['boards']}连板)")
    report.append("")
    
    # 游资动向
    hotmoney = snapshot['hotmoney']
    report.append("💰 游资动向:")
    report.append(f"  游资净买入: {hotmoney['total_net_yi']} 亿")
    
    top_buy = hotmoney['top_net_buy'][:3]
    report.append("  净买入TOP3:")
    for stock in top_buy:
        report.append(f"    {stock['name']}: {stock['net_yi']}亿 ({stock['ratio_pct']}%)")
    report.append("")
    
    # 焦点新闻
    focus_news = snapshot['focus_news'][:3]
    report.append("📰 焦点新闻:")
    for news in focus_news:
        time = news['t'].split('T')[1][:5]
        report.append(f"  [{time}] {news['title']}")
    
    return "\n".join(report)

def get_margin_data():
    """获取融资融券数据"""
    url = "https://hhxg.top/api/margin"
    
    try:
        response = requests.get(url, timeout=10)
        data = response.json()
        
        if data['success']:
            margin_data = data['data']
            
            report = []
            report.append("\n💳 融资融券数据")
            report.append("-" * 40)
            report.append(f"日期: {margin_data['date']}")
            report.append(f"融资余额: {margin_data['financing_balance']} 亿")
            report.append(f"融券余额: {margin_data['securities_balance']} 亿")
            report.append(f"两融总额: {margin_data['total_balance']} 亿")
            report.append("")
            
            # 融资净买入TOP
            report.append("📈 融资净买入TOP5:")
            for i, stock in enumerate(margin_data['top_net_buy'][:5], 1):
                report.append(f"  {i}. {stock['name']}: {stock['net_buy']}万")
            
            return "\n".join(report)
    except Exception as e:
        return f"\n❌ 融资融券数据获取失败: {e}"
    
    return ""

def main():
    print("正在获取A股市场数据...")
    
    # 获取市场快照
    data = get_market_snapshot()
    
    if data:
        report = format_market_report(data)
        print(report)
        
        # 获取融资融券数据
        margin_report = get_margin_data()
        print(margin_report)
        
        # 添加链接
        print("\n🔗 更多数据:")
        print("完整日报: https://hhxg.top/2026/04/2026-04-02.html")
        print("量化选股: https://hhxg.top/xuangu.html")
        print("游资追踪: https://hhxg.top/youzi.html")
        print("ETF工具: https://hhxg.top/etf.html")
    else:
        print("无法获取市场数据，请检查网络连接")

if __name__ == "__main__":
    main()