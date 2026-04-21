#!/usr/bin/env python3
"""
测试PostgreSQL远程连接
"""

import subprocess
import sys

def test_connection():
    """测试数据库连接"""
    
    # 方法1：使用nc测试端口
    print("1. 测试端口5432是否开放...")
    result = subprocess.run(["nc", "-z", "192.168.2.186", "5432"], 
                          capture_output=True, text=True)
    if result.returncode == 0:
        print("✅ 端口5432开放")
    else:
        print("❌ 端口5432未开放")
        print(f"错误: {result.stderr}")
    
    # 方法2：使用telnet测试
    print("\n2. 使用telnet测试连接...")
    try:
        import telnetlib
        tn = telnetlib.Telnet("192.168.2.186", 5432, timeout=5)
        print("✅ Telnet连接成功")
        tn.close()
    except Exception as e:
        print(f"❌ Telnet连接失败: {e}")
    
    # 方法3：检查防火墙
    print("\n3. 检查防火墙设置...")
    result = subprocess.run(["sudo", "pfctl", "-s", "rules"], 
                          capture_output=True, text=True)
    if "5432" in result.stdout:
        print("⚠️  防火墙规则中包含5432端口")
    else:
        print("✅ 防火墙未阻止5432端口")
    
    # 方法4：从Docker容器内部测试
    print("\n4. 从Docker容器内部测试...")
    result = subprocess.run([
        "docker", "exec", "soulcurator-postgres",
        "psql", "-U", "postgres", "-d", "soulcurator",
        "-c", "SELECT '数据库连接正常' as status, current_database() as db;"
    ], capture_output=True, text=True)
    
    if result.returncode == 0:
        print("✅ Docker容器内部连接正常")
        print(result.stdout)
    else:
        print("❌ Docker容器内部连接失败")
        print(f"错误: {result.stderr}")

if __name__ == "__main__":
    print("=== PostgreSQL远程连接测试 ===\n")
    print(f"目标主机: 192.168.2.186:5432")
    print(f"数据库: soulcurator")
    print(f"用户: postgres\n")
    
    test_connection()
    
    print("\n=== 连接信息总结 ===")
    print("Mac IP地址: 192.168.2.186")
    print("PostgreSQL端口: 5432")
    print("数据库名: soulcurator")
    print("用户名: postgres")
    print("\nPC连接字符串:")
    print("jdbc:postgresql://192.168.2.186:5432/soulcurator")