#!/usr/bin/env python3
"""
HTML代码分析脚本 - SoulCurator项目专用
分析HTML代码并提供改进建议
"""

import re
import sys
from typing import Dict, List, Tuple, Optional
from dataclasses import dataclass

@dataclass
class HTMLIssue:
    """HTML问题记录"""
    line: int
    type: str  # 'error', 'warning', 'suggestion'
    message: str
    code: str
    fix: Optional[str] = None

@dataclass
class AnalysisResult:
    """分析结果"""
    issues: List[HTMLIssue]
    suggestions: List[str]
    score: float  # 0-100分

class HTMLAnalyzer:
    """HTML代码分析器"""
    
    def __init__(self):
        self.rules = {
            'missing_alt': re.compile(r'<img[^>]*?(?<!alt=)[^>]*?>'),
            'missing_button_type': re.compile(r'<button(?![^>]*type=)[^>]*>'),
            'div_soup': re.compile(r'<div[^>]*>.*?<div[^>]*>.*?<div[^>]*>'),
            'inline_style': re.compile(r'style="[^"]*"'),
            'non_semantic': re.compile(r'<(div|span)[^>]*>\s*<(h[1-6]|p|ul|ol|li|header|nav|main|section|article|footer)'),
            'table_layout': re.compile(r'<table[^>]*>(?!.*<th>).*?</table>', re.DOTALL),
        }
        
    def analyze(self, html: str) -> AnalysisResult:
        """分析HTML代码"""
        issues = []
        lines = html.split('\n')
        
        for i, line in enumerate(lines, 1):
            # 检查每行的问题
            line_issues = self._analyze_line(line.strip(), i)
            issues.extend(line_issues)
        
        # 整体分析
        suggestions = self._generate_suggestions(html, issues)
        score = self._calculate_score(html, issues)
        
        return AnalysisResult(
            issues=issues,
            suggestions=suggestions,
            score=score
        )
    
    def _analyze_line(self, line: str, line_num: int) -> List[HTMLIssue]:
        """分析单行HTML"""
        issues = []
        
        # 检查缺少alt属性的img标签
        if '<img' in line and 'alt=' not in line and 'alt =' not in line:
            issues.append(HTMLIssue(
                line=line_num,
                type='error',
                message='图片缺少alt属性（可访问性问题）',
                code=line,
                fix='添加alt="描述文本"'
            ))
        
        # 检查缺少type属性的button标签
        if '<button' in line and 'type=' not in line:
            issues.append(HTMLIssue(
                line=line_num,
                type='warning',
                message='按钮缺少type属性',
                code=line,
                fix='添加type="button"（或type="submit"）'
            ))
        
        # 检查内联样式
        if 'style="' in line:
            issues.append(HTMLIssue(
                line=line_num,
                type='suggestion',
                message='使用内联样式，建议使用CSS类',
                code=line,
                fix='将样式提取到CSS类中'
            ))
        
        # 检查非语义化标签包裹语义化内容
        if re.search(r'^<div[^>]*>\s*<(h[1-6]|p)', line):
            issues.append(HTMLIssue(
                line=line_num,
                type='suggestion',
                message='使用div包裹标题/段落，建议直接使用语义化标签',
                code=line,
                fix='移除不必要的div包装'
            ))
        
        return issues
    
    def _generate_suggestions(self, html: str, issues: List[HTMLIssue]) -> List[str]:
        """生成改进建议"""
        suggestions = []
        
        # 统计问题类型
        error_count = sum(1 for issue in issues if issue.type == 'error')
        warning_count = sum(1 for issue in issues if issue.type == 'warning')
        
        # 基础建议
        if error_count > 0:
            suggestions.append(f"修复{error_count}个关键错误（如缺少alt属性）")
        
        if warning_count > 0:
            suggestions.append(f"处理{warning_count}个警告（如按钮类型）")
        
        # 结构建议
        if '<div' in html and html.count('<div') > 5:
            suggestions.append("考虑减少div嵌套，使用更语义化的HTML5标签")
        
        if 'class=' in html:
            # 检查是否使用Tailwind
            if 'bg-' in html or 'text-' in html or 'p-' in html or 'm-' in html:
                suggestions.append("检测到Tailwind CSS，确保类名组织合理")
            else:
                suggestions.append("考虑使用Tailwind CSS统一样式系统")
        
        # Vue3相关建议
        if '{{' in html or 'v-' in html:
            suggestions.append("Vue3代码检测，确保使用Composition API最佳实践")
        
        # 性能建议
        if '<img' in html:
            suggestions.append("为图片添加loading='lazy'属性优化性能")
        
        return suggestions
    
    def _calculate_score(self, html: str, issues: List[HTMLIssue]) -> float:
        """计算代码质量分数"""
        base_score = 100.0
        
        # 扣分规则
        deductions = 0
        
        # 关键错误扣分
        error_count = sum(1 for issue in issues if issue.type == 'error')
        deductions += error_count * 10
        
        # 警告扣分
        warning_count = sum(1 for issue in issues if issue.type == 'warning')
        deductions += warning_count * 5
        
        # 建议扣分
        suggestion_count = sum(1 for issue in issues if issue.type == 'suggestion')
        deductions += suggestion_count * 2
        
        # 结构复杂度扣分
        div_count = html.count('<div')
        if div_count > 10:
            deductions += (div_count - 10) * 1
        
        # 内联样式扣分
        style_count = len(re.findall(r'style="[^"]*"', html))
        deductions += style_count * 3
        
        # 计算最终分数
        final_score = max(0, base_score - deductions)
        return final_score
    
    def generate_report(self, result: AnalysisResult, html: str) -> str:
        """生成分析报告"""
        report = []
        
        # 标题
        report.append("# 🔍 HTML代码分析报告")
        report.append("")
        
        # 总体评分
        report.append(f"## 📊 总体评分: {result.score:.1f}/100")
        report.append("")
        
        # 问题统计
        error_count = sum(1 for issue in result.issues if issue.type == 'error')
        warning_count = sum(1 for issue in result.issues if issue.type == 'warning')
        suggestion_count = sum(1 for issue in result.issues if issue.type == 'suggestion')
        
        report.append("## 📈 问题统计")
        report.append(f"- ❌ 关键错误: {error_count}个")
        report.append(f"- ⚠️ 警告: {warning_count}个")
        report.append(f"- 💡 建议: {suggestion_count}个")
        report.append("")
        
        # 详细问题
        if result.issues:
            report.append("## 🔎 详细问题")
            report.append("")
            
            for issue in result.issues:
                emoji = "❌" if issue.type == 'error' else "⚠️" if issue.type == 'warning' else "💡"
                report.append(f"### {emoji} 第{issue.line}行: {issue.message}")
                report.append(f"```html\n{issue.code}\n```")
                if issue.fix:
                    report.append(f"**修复建议**: {issue.fix}")
                report.append("")
        
        # 改进建议
        if result.suggestions:
            report.append("## 🚀 改进建议")
            report.append("")
            for i, suggestion in enumerate(result.suggestions, 1):
                report.append(f"{i}. {suggestion}")
            report.append("")
        
        # 方案建议
        report.append("## 🎯 修改方案建议")
        report.append("")
        
        report.append("### 方案A（保守 - 快速修复）")
        report.append("- 只修复关键错误（alt属性、按钮类型）")
        report.append("- 保持现有结构不变")
        report.append("- 预计耗时: 5-10分钟")
        report.append("")
        
        report.append("### 方案B（优化 - 推荐）")
        report.append("- 修复所有错误和警告")
        report.append("- 优化HTML结构（减少div嵌套）")
        report.append("- 提取内联样式到CSS类")
        report.append("- 预计耗时: 15-30分钟")
        report.append("")
        
        report.append("### 方案C（激进 - 完全重构）")
        report.append("- 完全重写为Vue3组件")
        report.append("- 使用Tailwind CSS最佳实践")
        report.append("- 实现响应式设计")
        report.append("- 添加TypeScript类型定义")
        report.append("- 预计耗时: 1-2小时")
        report.append("")
        
        report.append("---")
        report.append("*报告生成时间: 2026-04-03*")
        report.append("*适用于: SoulCurator项目 (Vue3 + TypeScript + Tailwind CSS)*")
        
        return '\n'.join(report)

def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("用法: python analyze-html.py <HTML文件路径>")
        sys.exit(1)
    
    file_path = sys.argv[1]
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            html = f.read()
    except FileNotFoundError:
        print(f"错误: 文件不存在 {file_path}")
        sys.exit(1)
    
    analyzer = HTMLAnalyzer()
    result = analyzer.analyze(html)
    report = analyzer.generate_report(result, html)
    
    print(report)
    
    # 保存报告到文件
    output_path = file_path.replace('.html', '_analysis.md')
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(report)
    
    print(f"\n✅ 分析报告已保存到: {output_path}")

if __name__ == '__main__':
    main()