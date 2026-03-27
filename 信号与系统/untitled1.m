function RI_values = monte_carlo_ri_cr_threshold(max_n, num_samples, cr_threshold)
    % max_n: 最大矩阵维度 (n = 1 到 max_n)
    % num_samples: 每个维度生成的随机矩阵数量
    % cr_threshold: CR 的阈值（例如 0.1）
    
    RI_values = zeros(max_n, 1); % 存储每个维度的RI值
    
    for n = 1:max_n
        CI_values = []; % 存储当前维度n的所有CI值
        
        while length(CI_values) < num_samples
            % 生成一个随机的n x n判断矩阵
            A = random_judgment_matrix(n);
            
            % 计算该矩阵的一致性指标CI
            CI = consistency_index(A);
            
            % 计算随机一致性指标RI（使用理论值或预计算值）
            RI = theoretical_ri(n);
            
            % 计算一致性比率CR
            CR = CI / RI;
            
            % 如果CR < cr_threshold，则记录CI值
            if CR < cr_threshold
                CI_values = [CI_values; CI];
            end
        end
        
        % 计算当前维度n的随机一致性指标RI
        RI_values(n) = mean(CI_values);
        
        % 打印当前维度的RI值
        fprintf('n = %d, RI = %.4f\n', n, RI_values(n));
    end
end

function A = random_judgment_matrix(n)
    % 生成一个随机的n x n判断矩阵
    A = ones(n); % 初始化矩阵，对角线元素为1
    
    for i = 1:n
        for j = i+1:n
            % 随机生成1到9之间的整数或其倒数
            A(i, j) = randi([1, 9]);
            A(j, i) = 1 / A(i, j);
        end
    end
end

function CI = consistency_index(A)
    % 计算判断矩阵A的一致性指标CI
    n = size(A, 1);
    
    if n == 1
        CI = 0; % 当n=1时，CI为0
        return;
    end
    
    % 计算权重向量
    [~, eig_vals] = eig(A);
    lambda_max = max(diag(eig_vals)); % 最大特征值
    
    CI = (lambda_max - n) / (n - 1); % 一致性指标
end

function RI = theoretical_ri(n)
    % 返回理论RI值（可以根据需要调整）
    % 这里使用经典的RI表值
    ri_table = [0, 0, 0.58, 0.90, 1.12, 1.24, 1.32, 1.41, 1.45, 1.49, 1.51];
    if n <= length(ri_table)
        RI = ri_table(n);
    else
        RI = 1.51; % 对于n > 11，使用近似值
    end
end

% 示例：计算n=1到11的随机一致性指标RI（CR < 0.1）
max_n = 11; % 最大矩阵维度
num_samples = 1000; % 生成的随机矩阵数量
cr_threshold = 0.1; % CR的阈值
RI_values = monte_carlo_ri_cr_threshold(max_n, num_samples, cr_threshold);

% 输出结果
disp('Random Consistency Index (RI) for n = 1 to 11 (CR < 0.1):');
disp(RI_values');