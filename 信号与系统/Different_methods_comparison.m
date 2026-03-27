format long
x1 = fsolve(@(x) x.^3+4*x.^2-20,1.5); % Matlab内置的高端算法：信赖域算法
x2 = Fixed_point(1.5);                % 直接迭代法
x3 = Newton_method(1.5);        % 标准的牛顿法
x4 = Bisection(1, 2);                 % 二分法
fun = @(x) x^3 + 4 * x^2 - 20;
y1 = fun(x1);
y2 = fun(x2);
y3 = fun(x3);
y4 = fun(x4);
disp(['二分法： x=',num2str(x4), ';  f(x)= ', num2str(y4)]);
disp(['直接迭代法： x=',num2str(x2), ';  f(x)= ', num2str(y2)]);
disp(['牛顿法： x=',num2str(x3), ';  f(x)= ', num2str(y3)]);
disp(['信赖域法： x=',num2str(x1), ';  f(x)= ', num2str(y1)]);