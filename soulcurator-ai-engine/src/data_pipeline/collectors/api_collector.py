"""
API数据收集器 - 从各种API收集数据
"""
import requests
import json
from typing import Dict, List, Any
from loguru import logger


class APICollector:
    """从API收集数据的收集器"""
    
    def __init__(self):
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'SoulCurator-AI-Engine/1.0'
        })
    
    def collect_from_hhxg(self) -> List[Dict[str, Any]]:
        """从hhxg.top收集市场数据"""
        try:
            logger.info("开始从hhxg.top收集市场数据")
            
            # 市场快照API
            market_url = "https://api.hhxg.top/market/snapshot"
            response = self.session.get(market_url)
            
            if response.status_code == 200:
                data = response.json()
                logger.success(f"成功收集市场数据，记录数: {len(data.get('data', []))}")
                return data.get('data', [])
            else:
                logger.error(f"API请求失败: {response.status_code}")
                return []
                
        except Exception as e:
            logger.error(f"收集hhxg数据时出错: {e}")
            return []
    
    def collect_from_tushare(self, api_name: str, params: Dict = None) -> List[Dict[str, Any]]:
        """从Tushare收集金融数据"""
        try:
            logger.info(f"开始从Tushare收集数据: {api_name}")
            
            # 这里需要配置Tushare Token
            # 实际使用时需要从环境变量获取
            tushare_token = "your_tushare_token"
            
            if not tushare_token:
                logger.warning("Tushare Token未配置")
                return []
            
            tushare_url = "http://api.tushare.pro"
            payload = {
                "api_name": api_name,
                "token": tushare_token,
                "params": params or {},
                "fields": ""  # 默认所有字段
            }
            
            response = self.session.post(tushare_url, json=payload)
            
            if response.status_code == 200:
                data = response.json()
                if data.get("code") == 0:
                    records = data.get("data", {}).get("items", [])
                    fields = data.get("data", {}).get("fields", [])
                    
                    # 转换为字典列表
                    result = []
                    for item in records:
                        record = dict(zip(fields, item))
                        result.append(record)
                    
                    logger.success(f"成功收集Tushare数据，记录数: {len(result)}")
                    return result
                else:
                    logger.error(f"Tushare API错误: {data.get('msg')}")
                    return []
            else:
                logger.error(f"Tushare API请求失败: {response.status_code}")
                return []
                
        except Exception as e:
            logger.error(f"收集Tushare数据时出错: {e}")
            return []
    
    def collect_stock_daily(self, ts_code: str = None, trade_date: str = None) -> List[Dict[str, Any]]:
        """收集股票日线数据"""
        params = {}
        if ts_code:
            params["ts_code"] = ts_code
        if trade_date:
            params["trade_date"] = trade_date
            
        return self.collect_from_tushare("daily", params)
    
    def collect_financial_news(self, limit: int = 50) -> List[Dict[str, Any]]:
        """收集财经新闻"""
        try:
            logger.info("开始收集财经新闻")
            
            # 这里可以使用hhxg的新闻API或其他新闻源
            news_url = "https://api.hhxg.top/news/latest"
            params = {"limit": limit}
            
            response = self.session.get(news_url, params=params)
            
            if response.status_code == 200:
                data = response.json()
                news_list = data.get('data', [])
                logger.success(f"成功收集财经新闻，记录数: {len(news_list)}")
                return news_list
            else:
                logger.error(f"新闻API请求失败: {response.status_code}")
                return []
                
        except Exception as e:
            logger.error(f"收集财经新闻时出错: {e}")
            return []


if __name__ == "__main__":
    # 测试代码
    collector = APICollector()
    
    # 测试hhxg数据收集
    hhxg_data = collector.collect_from_hhxg()
    print(f"hhxg数据: {len(hhxg_data)} 条")
    
    # 测试新闻收集
    news_data = collector.collect_financial_news(limit=10)
    print(f"新闻数据: {len(news_data)} 条")