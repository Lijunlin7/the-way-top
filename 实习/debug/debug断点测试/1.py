# debug_test.py
"""
VS Code 调试环境测试文件
包含以下测试场景：
1. 基础断点调试
2. 变量监控
3. 条件断点
4. 函数调用栈
5. 循环调试
6. 异常捕获
"""

def calculate_discount(price, discount_rate):
    """计算折扣后的价格（测试函数调用）"""
    discounted_price = price * (1 - discount_rate)
    return discounted_price  # 在此行设置断点查看变量变化

def main():
    # 1. 基础变量测试
    username = "debug_user"
    user_id = 42
    is_active = True

    print(f"Starting debug test for {username} (ID: {user_id})")  # 在此行设置断点

    # 2. 列表和字典测试
    items = ["apple", "banana", "cherry"]
    prices = {"apple": 1.2, "banana": 0.8, "cherry": 2.5}

    # 3. 循环调试测试
    total = 0.0
    for item in items:
        price = prices[item]
        total += price
        print(f"Added {item}: ${price:.2f}")  # 在此行设置条件断点（如 item == "banana"）

    # 4. 函数调用测试
    final_price = calculate_discount(total, 0.1)
    print(f"Total before discount: ${total:.2f}")
    print(f"Final price after 10% discount: ${final_price:.2f}")

    # 5. 异常测试（取消注释测试）
    # risky_operation = 10 / 0  # 测试异常断点

if __name__ == "__main__":
    main()
    print("Debug test completed!")