% 参数设置
v0 = 200; % 初速度 (m/s)
g = 9.81; % 重力加速度 (m/s^2)
x = 360; % 水平距离 (m)
y = 160; % 垂直距离 (m)

% 发射角
theta1 = 84.8 * pi / 180; % 发射角1 (rad)
theta2 = 28.8 * pi / 180; % 发射角2 (rad)

% 时间计算
t1 = x / (v0 * cos(theta1)); % 飞行时间1 (s)
t2 = x / (v0 * cos(theta2)); % 飞行时间2 (s)

% 轨迹计算
t = linspace(0, max(t1, t2), 1000);
x1 = v0 * cos(theta1) * t;
y1 = v0 * sin(theta1) * t - 0.5 * g * t.^2;
x2 = v0 * cos(theta2) * t;
y2 = v0 * sin(theta2) * t - 0.5 * g * t.^2;

% 绘图
figure;
plot(x1, y1, 'r', 'LineWidth', 2); hold on;
plot(x2, y2, 'b', 'LineWidth', 2);
plot(x, y, 'go', 'MarkerSize', 10, 'LineWidth', 2);
xlabel('水平距离 (m)');
ylabel('垂直距离 (m)');
title('炮弹飞行轨迹');
legend('发射角 84.8^\circ', '发射角 28.8^\circ', '目标点');
grid on;
hold off;