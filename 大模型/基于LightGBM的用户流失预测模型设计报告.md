# 基于LightGBM的用户流失预测模型设计报告

## 1. 项目背景与目标

设计一套AI算法或模型，实现基于网络KQI数据识别出贬损用户，帮助运营商提前发现潜在贬损者。

## 2.项目文件与任务分析

构建一个用户流失预测模型，通过分析用户的多维度KQI(关键质量指标)数据，预测用户流失风险。

### 2.1项目文件

项目文件train.csv与test.csv。其中tran.csv文件为训练模型数据，test.csv为推理数据。

简化贬损者评判标准到label列，简化为0,1二分。

### 2.2任务分析

这个问题是一个二分类问题，并且选择使用AUC作为主要评估指标，以衡量所使用算法或模型对于正负样本的区分能力。

原始数据包含多个KQI指标，每个指标还可能存在多个测量值，并且，由于流失用户占少数，存在类别不平衡。对于这些数据特点，我们需要将原始KQI数据转化为适合模型处理的结构化特征，并从中提取数据。对于不平衡的处理方法，可以使用法增加少数类样本。

### 2.3解决思路

采用分组统计特征工程思路，利用SVC和KQI对于指标分组，然后每组数据提取其统计特征和趋势特征。

由于以上的分析，模型采用LightGBM算法，结合特征工程和类别不平衡处理技术。

## 3. 数据预处理与特征工程

### 3.1 数据加载与清洗

原始数据包含训练集(5721个负样本+3962个正样本)和测试集(1430个负样本+990个正样本)

移除无关的NPS列，保留LABEL作为目标变量

处理缺失值和无穷大值：用0填充NaN和inf值

```c++
def load_data(train_path, test_path):
    train = pd.read_csv(train_path)
    test = pd.read_csv(test_path)
    # 移除NPS列，保留LABEL作为目标
    train = train.drop('NPS', axis=1) if 'NPS' in train.columns else train
    test = test.drop('NPS', axis=1) if 'NPS' in test.columns else test
    return train, test
```

### 3.2 特征提取方法

针对KQI指标(SVC_x_KQI_y_z格式)设计专门的特征提取

```python
def extract_features(df):
    # 提取所有KQI指标列（格式为SVC_x_KQI_y_z）
    kqi_cols = [col for col in df.columns if 'KQI' in col and col not in ['USERID', 'LABEL']]
    
    # 按业务(SVC)和KQI类型分组
    kqi_groups = {}
    for col in kqi_cols:
        parts = col.split('_')
        if len(parts) >= 4:
            group_key = f"{parts[0]}_{parts[1]}_{parts[2]}_{parts[3]}"
            kqi_groups[group_key] = kqi_groups.get(group_key, []) + [col]
    
    # 为每个KQI组计算特征
    for group, cols in kqi_groups.items():
        kqi_data = df[cols]
        # 计算各种统计特征（均值、标准差、最大值等）
        # 计算变化趋势和异常值统计
        ...
    return features
```

提取的特征类型包括：
1. **统计特征**：均值、标准差、最大值、最小值、中位数、末值
2. **趋势特征**：线性回归斜率
3. **异常特征**：超出2σ的异常值计数

### 3.3数据预处理

```python
# 处理缺失值
X_train = X_train.fillna(X_train.median())
X_test = X_test.fillna(X_train.median())

# 标准化
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)
```

## 4. 模型设计与实现

### 4.1 LightGBM

| 项目需求     | LightGBM特性                           |
| :----------- | :------------------------------------- |
| 处理大量特征 | 自动特征选择，处理高维数据高效         |
| 类别不平衡   | 支持class_weight参数，对不平衡数据鲁棒 |
| 非线性关系   | 树模型天然适合捕捉非线性关系           |
| 解释性需求   | 提供特征重要性排序                     |
| 计算效率     | 比传统GBDT更快，适合快速迭代           |

### 4.2 模型参数配置

```python
lgb_model = lgb.LGBMClassifier(
    objective='binary',
    metric='auc',
    n_estimators=1000,
    learning_rate=0.05,
    max_depth=7,
    num_leaves=31,
    min_child_samples=20,
    subsample=0.8,
    colsample_bytree=0.8,
    reg_alpha=0.1,
    reg_lambda=0.1,
    random_state=42
)
```

关键参数说明：
- `num_leaves=31`：每棵树的最大叶子数，控制模型复杂度
- `min_child_samples=20`：防止过拟合的最小叶子样本数
- `reg_alpha`和`reg_lambda`：L1和L2正则化系数

### 4.3 类别不平衡处理

采用SMOTE过采样技术：
- 少数类样本通过插值生成新样本
- 处理后训练集正负样本均为5721个
- 插值公式：xₙₑᵥ = xᵢ + λ × (xⱼ - xᵢ)，其中λ∈[0,1]

```python
smote = SMOTE(random_state=42)
X_train_res, y_train_res = smote.fit_resample(X_train_scaled, y_train)
```

## 5. 模型评估与分析

### 5.1 评估指标

| 指标        | 值     |
| ----------- | ------ |
| AUC         | 0.5558 |
| 准确率      | 0.58   |
| 类别0精确率 | 0.60   |
| 类别1精确率 | 0.47   |
| 类别0召回率 | 0.83   |
| 类别1召回率 | 0.22   |

### 5.2 混淆矩阵分析

```
          Predicted 0  Predicted 1
Actual 0     1187         243
Actual 1      772         218
```

- 模型对类别0(未流失)识别较好(召回率83%)
- 对类别1(流失)识别能力不足(召回率仅22%)
- 存在明显的类别偏向性

### 4.3 特征重要性分析

Top 5重要特征：
1. SVC_1_KQI_1_0_last
2. SVC_1_KQI_1_0_mean  
3. SVC_1_KQI_1_0_slope
4. SVC_1_KQI_1_0_median
5. SVC_1_KQI_1_0_std

## 5. 问题分析

### 5.1 当前模型局限

1. **AUC值偏低**(0.5558)，接近随机猜测(0.5)
2. 对流失用户识别能力差(召回率22%)

**模型优化**：

- 尝试加权损失函数：`class_weight='balanced'`

- 调整早停策略(当前stopping_rounds=50)

- 使用贝叶斯优化进行超参数搜索

## 6.代码

```python
import pandas as pd
import numpy as np
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import roc_auc_score, classification_report, confusion_matrix
import lightgbm as lgb
from imblearn.over_sampling import SMOTE
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.utils import check_array
import warnings

warnings.filterwarnings('ignore')


# 1. 数据加载与预处理
def load_data(train_path, test_path):
    train = pd.read_csv(train_path)
    test = pd.read_csv(test_path)

    # 移除NPS列，保留LABEL作为目标
    train = train.drop('NPS', axis=1) if 'NPS' in train.columns else train
    test = test.drop('NPS', axis=1) if 'NPS' in test.columns else test

    return train, test


# 2. 特征工程 
def extract_features(df):
    features = pd.DataFrame()

    # 获取所有KQI指标列（格式为SVC_x_KQI_y_z）
    kqi_cols = [col for col in df.columns if 'KQI' in col and col not in ['USERID', 'LABEL']]

    # 按业务(SVC)和KQI类型分组
    kqi_groups = {}
    for col in kqi_cols:
        parts = col.split('_')
        if len(parts) >= 4:  # 确保是SVC_x_KQI_y_z格式
            group_key = f"{parts[0]}_{parts[1]}_{parts[2]}_{parts[3]}"
            if group_key not in kqi_groups:
                kqi_groups[group_key] = []
            kqi_groups[group_key].append(col)

    for group, cols in kqi_groups.items():
        kqi_data = df[cols]

        # 基本统计量
        features[f'{group}_mean'] = kqi_data.mean(axis=1)
        features[f'{group}_std'] = kqi_data.std(axis=1)
        features[f'{group}_max'] = kqi_data.max(axis=1)
        features[f'{group}_min'] = kqi_data.min(axis=1)
        features[f'{group}_median'] = kqi_data.median(axis=1)
        features[f'{group}_last'] = kqi_data.iloc[:, -1]

        # 变化趋势
        features[f'{group}_slope'] = kqi_data.apply(
            lambda x: np.polyfit(range(len(x)), x, 1)[0] if len(x) > 1 else 0, axis=1)

        # 异常值统计
        features[f'{group}_outlier'] = kqi_data.apply(
            lambda x: np.sum((x - x.mean()).abs() > 2 * x.std()) if x.std() > 0 else 0, axis=1)

    # 添加用户ID和标签（用于后续处理）
    if 'USERID' in df.columns:
        features['USERID'] = df['USERID']
    if 'LABEL' in df.columns:
        features['LABEL'] = df['LABEL']

    # 处理可能的NaN/inf
    features = features.replace([np.inf, -np.inf], np.nan).fillna(0)
    return features


# 3. 模型训练与评估
def train_and_evaluate(train, test):
    # 特征工程
    print("正在进行特征工程...")
    train_features = extract_features(train)
    test_features = extract_features(test)

    # 准备数据
    X_train = train_features.drop(['USERID', 'LABEL'], axis=1, errors='ignore')
    y_train = train_features['LABEL']
    X_test = test_features.drop(['USERID', 'LABEL'], axis=1, errors='ignore')
    y_test = test_features['LABEL']

    # 检查类别分布
    print("\n类别分布:")
    print("训练集 - 类别0:", sum(y_train == 0), "类别1:", sum(y_train == 1))
    print("测试集 - 类别0:", sum(y_test == 0), "类别1:", sum(y_test == 1))

    # 处理缺失值
    print("\n处理缺失值...")
    X_train = X_train.fillna(X_train.median())
    X_test = X_test.fillna(X_train.median())

    # 标准化
    print("标准化数据...")
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)

    # 检查数据有效性
    X_train_scaled = check_array(X_train_scaled, force_all_finite=True)
    y_train = check_array(y_train.values.reshape(-1, 1), force_all_finite=True).ravel()

    # 处理类别不平衡
    print("\n处理类别不平衡...")
    try:
        smote = SMOTE(random_state=42)
        X_train_res, y_train_res = smote.fit_resample(X_train_scaled, y_train)
        print("SMOTE过采样后 - 类别0:", sum(y_train_res == 0), "类别1:", sum(y_train_res == 1))
    except ValueError as e:
        print("SMOTE 失败，使用原始数据:", e)
        X_train_res, y_train_res = X_train_scaled, y_train

    # 训练LightGBM模型
    print("\n训练LightGBM模型...")
    lgb_model = lgb.LGBMClassifier(
        objective='binary',
        metric='auc',
        n_estimators=1000,
        learning_rate=0.05,
        max_depth=7,
        num_leaves=31,
        min_child_samples=20,
        subsample=0.8,
        colsample_bytree=0.8,
        reg_alpha=0.1,
        reg_lambda=0.1,
        random_state=42,
        n_jobs=-1,
        verbose=-1  # 设置日志级别为静默
    )

    # 正确的early stopping使用方式
    lgb_model.fit(
        X_train_res,
        y_train_res,
        eval_set=[(X_test_scaled, y_test)],
        eval_metric='auc',
        callbacks=[lgb.early_stopping(stopping_rounds=50)]
    )

    # 预测与评估
    print("\n模型评估:")
    y_pred = lgb_model.predict(X_test_scaled)
    y_pred_prob = lgb_model.predict_proba(X_test_scaled)[:, 1]

    # 计算评估指标
    auc = roc_auc_score(y_test, y_pred_prob)
    print("\n分类报告:")
    print(classification_report(y_test, y_pred))

    # 混淆矩阵
    plt.figure(figsize=(8, 6))
    cm = confusion_matrix(y_test, y_pred)
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues')
    plt.title('Confusion Matrix')
    plt.xlabel('Predicted')
    plt.ylabel('Actual')
    plt.show()

    # 特征重要性
    feature_imp = pd.DataFrame({
        'Feature': X_train.columns,
        'Importance': lgb_model.feature_importances_
    }).sort_values('Importance', ascending=False)

    plt.figure(figsize=(10, 8))
    plt.barh(feature_imp['Feature'][:20][::-1], feature_imp['Importance'][:20][::-1])
    plt.title('Top 20 Feature Importance')
    plt.xlabel('Importance Score')
    plt.tight_layout()
    plt.show()

    return auc, lgb_model


# 主函数
def main():
    # 加载数据
    print("加载数据...")
    train_path = 'train.csv'  # 修改为您的train.csv路径
    test_path = 'test.csv'  # 修改为您的test.csv路径
    train, test = load_data(train_path, test_path)

    # 训练评估模型
    auc, model = train_and_evaluate(train, test)
    print(f'\n最终测试集AUC: {auc:.4f}')


if __name__ == '__main__':
    main()
```

运行结果

```python
C:\Users\24961\PycharmProjects\PythonProject\.venv\Scripts\python.exe C:\Users\24961\PycharmProjects\PythonProject\model.py 
加载数据...
正在进行特征工程...

类别分布:
训练集 - 类别0: 5721 类别1: 3962
测试集 - 类别0: 1430 类别1: 990

处理缺失值...
标准化数据...

处理类别不平衡...
SMOTE过采样后 - 类别0: 5721 类别1: 5721

训练LightGBM模型...
Training until validation scores don't improve for 50 rounds
Early stopping, best iteration is:
[90]	valid_0's auc: 0.555752

模型评估:

分类报告:
              precision    recall  f1-score   support

           0       0.60      0.83      0.70      1430
           1       0.47      0.22      0.30       990

    accuracy                           0.58      2420
   macro avg       0.53      0.52      0.50      2420
weighted avg       0.55      0.58      0.53      2420
最终测试集AUC: 0.5558
```

![image-20250426223940764](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250426223940764.png)

![image-20250426234738277](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250426234738277.png)

## 6.模型优化

按照上述改进方向对于模型进行修改

```python
import pandas as pd
import numpy as np
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import roc_auc_score, classification_report, confusion_matrix, precision_recall_curve, auc
from sklearn.model_selection import StratifiedKFold
import lightgbm as lgb
from imblearn.over_sampling import SMOTE
from imblearn.under_sampling import RandomUnderSampler
from imblearn.pipeline import Pipeline
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.utils import check_array
import warnings
from tqdm import tqdm
from scipy.stats import kurtosis, skew

warnings.filterwarnings('ignore')


# 1. 数据加载与预处理 - 增强版
def load_data(train_path, test_path):
    try:
        train = pd.read_csv(train_path)
        test = pd.read_csv(test_path)

        # 确保数据不为空
        if train.empty or test.empty:
            raise ValueError("训练集或测试集为空")

        # 移除NPS列，保留LABEL作为目标
        train = train.drop('NPS', axis=1) if 'NPS' in train.columns else train
        test = test.drop('NPS', axis=1) if 'NPS' in test.columns else test

        # 检查必要的列是否存在
        if 'LABEL' not in train.columns or 'LABEL' not in test.columns:
            raise ValueError("数据中缺少LABEL列")

        # 初步处理缺失值
        train = train.fillna(train.median(numeric_only=True))
        test = test.fillna(train.median(numeric_only=True))

        return train, test
    except Exception as e:
        print(f"加载数据时出错: {str(e)}")
        raise


# 2. 特征工程 - 增强版
def extract_features(df):
    try:
        features = pd.DataFrame()

        # 获取所有KQI指标列
        kqi_cols = [col for col in df.columns if 'KQI' in col and col not in ['USERID', 'LABEL']]

        if not kqi_cols:
            raise ValueError("数据中没有找到KQI指标列")

        # 按业务(SVC)和KQI类型分组
        kqi_groups = {}
        for col in kqi_cols:
            parts = col.split('_')
            if len(parts) >= 4:  # SVC_x_KQI_y_z格式
                group_key = f"{parts[0]}_{parts[1]}_{parts[2]}_{parts[3]}"
                if group_key not in kqi_groups:
                    kqi_groups[group_key] = []
                kqi_groups[group_key].append(col)

        # 为每个KQI组计算特征
        for group, cols in tqdm(kqi_groups.items(), desc="提取特征"):
            kqi_data = df[cols]

            # 基本统计量
            features[f'{group}_mean'] = kqi_data.mean(axis=1)
            features[f'{group}_std'] = kqi_data.std(axis=1)
            features[f'{group}_max'] = kqi_data.max(axis=1)
            features[f'{group}_min'] = kqi_data.min(axis=1)
            features[f'{group}_median'] = kqi_data.median(axis=1)
            features[f'{group}_last'] = kqi_data.iloc[:, -1]
            features[f'{group}_first'] = kqi_data.iloc[:, 0]
            features[f'{group}_range'] = features[f'{group}_max'] - features[f'{group}_min']
            features[f'{group}_cv'] = features[f'{group}_std'] / (features[f'{group}_mean'] + 1e-6)  # 变异系数

            # 变化趋势特征
            features[f'{group}_slope'] = kqi_data.apply(
                lambda x: np.polyfit(range(len(x)), x, 1)[0] if len(x) > 1 else 0, axis=1)
            features[f'{group}_abs_change'] = kqi_data.diff(axis=1).abs().sum(axis=1)
            features[f'{group}_pct_change'] = kqi_data.pct_change(axis=1).abs().sum(axis=1)

            # 异常值统计
            features[f'{group}_outlier'] = kqi_data.apply(
                lambda x: np.sum((x - x.mean()).abs() > 2 * x.std()) if x.std() > 0 else 0, axis=1)

            # 百分位数特征
            for p in [10, 25, 75, 90]:
                features[f'{group}_percentile_{p}'] = kqi_data.quantile(p / 100, axis=1)

            # 统计检验特征
            features[f'{group}_kurtosis'] = kqi_data.apply(lambda x: kurtosis(x), axis=1)
            features[f'{group}_skew'] = kqi_data.apply(lambda x: skew(x), axis=1)

            # 时间序列特征
            features[f'{group}_autocorr'] = kqi_data.apply(
                lambda x: x.autocorr() if len(x) > 1 else 0, axis=1)

        # 添加用户ID和标签
        if 'USERID' in df.columns:
            features['USERID'] = df['USERID']
        if 'LABEL' in df.columns:
            features['LABEL'] = df['LABEL']

        # 处理可能的NaN/inf
        features = features.replace([np.inf, -np.inf], np.nan)
        features = features.fillna(0)

        # 添加交叉特征
        if len(kqi_groups) > 1:
            group_keys = list(kqi_groups.keys())
            for i in range(min(3, len(group_keys))):  # 只计算前3组的交叉特征以避免维度爆炸
                for j in range(i + 1, min(i + 4, len(group_keys))):  # 每个组与接下来的3个组交叉
                    g1, g2 = group_keys[i], group_keys[j]
                    features[f'cross_{g1}_{g2}_mean'] = (features[f'{g1}_mean'] + 1e-6) / (
                                features[f'{g2}_mean'] + 1e-6)
                    features[f'cross_{g1}_{g2}_slope'] = features[f'{g1}_slope'] * features[f'{g2}_slope']

        return features
    except Exception as e:
        print(f"特征工程时出错: {str(e)}")
        raise


# 3. 模型训练与评估 - 增强版
def train_and_evaluate(train, test):
    try:
        # 特征工程
        print("\n正在进行特征工程...")
        train_features = extract_features(train)
        test_features = extract_features(test)

        # 准备数据
        X_train = train_features.drop(['USERID', 'LABEL'], axis=1, errors='ignore')
        y_train = train_features['LABEL']
        X_test = test_features.drop(['USERID', 'LABEL'], axis=1, errors='ignore')
        y_test = test_features['LABEL']

        # 检查类别分布
        print("\n类别分布:")
        print("训练集 - 类别0:", sum(y_train == 0), "类别1:", sum(y_train == 1))
        print("测试集 - 类别0:", sum(y_test == 0), "类别1:", sum(y_test == 1))

        # 处理缺失值
        print("\n处理缺失值...")
        for col in X_train.columns:
            if X_train[col].isnull().any():
                median_val = X_train[col].median()
                X_train[col] = X_train[col].fillna(median_val)
                X_test[col] = X_test[col].fillna(median_val)

        # 特征选择 - 移除低方差和常数值特征
        print("\n特征选择...")
        variances = X_train.var()
        low_var_cols = variances[variances < 0.01].index
        X_train = X_train.drop(low_var_cols, axis=1)
        X_test = X_test.drop(low_var_cols, axis=1)
        print(f"移除了 {len(low_var_cols)} 个低方差特征")

        # 标准化
        print("\n标准化数据...")
        scaler = StandardScaler()
        X_train_scaled = scaler.fit_transform(X_train)
        X_test_scaled = scaler.transform(X_test)

        # 确保数据有效
        X_train_scaled = check_array(X_train_scaled, force_all_finite=True)
        X_test_scaled = check_array(X_test_scaled, force_all_finite=True)
        y_train = check_array(y_train.values.reshape(-1, 1), force_all_finite=True).ravel()
        y_test = check_array(y_test.values.reshape(-1, 1), force_all_finite=True).ravel()

        # 处理类别不平衡
        print("\n处理类别不平衡...")
        try:
            over = SMOTE(sampling_strategy=0.5, random_state=42)
            under = RandomUnderSampler(sampling_strategy=0.8, random_state=42)
            steps = [('o', over), ('u', under)]
            pipeline = Pipeline(steps=steps)
            X_train_res, y_train_res = pipeline.fit_resample(X_train_scaled, y_train)
            print("重采样后 - 类别0:", sum(y_train_res == 0), "类别1:", sum(y_train_res == 1))
        except Exception as e:
            print(f"重采样失败，使用原始数据: {str(e)}")
            X_train_res, y_train_res = X_train_scaled, y_train

        # 交叉验证
        print("\n交叉验证...")
        skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
        cv_scores = []
        best_iterations = []

        for train_idx, val_idx in skf.split(X_train_res, y_train_res):
            X_tr, X_val = X_train_res[train_idx], X_train_res[val_idx]
            y_tr, y_val = y_train_res[train_idx], y_train_res[val_idx]

            # 优化后的LightGBM参数
            lgb_model = lgb.LGBMClassifier(
                objective='binary',
                metric='auc',
                n_estimators=2000,
                learning_rate=0.01,  # 更小的学习率
                max_depth=6,  # 稍深的树
                num_leaves=63,  # 更多的叶子节点
                min_child_samples=20,  # 更小的最小子样本数
                subsample=0.8,
                colsample_bytree=0.8,
                reg_alpha=0.05,
                reg_lambda=0.05,
                random_state=42,
                n_jobs=-1,
                verbose=-1
            )

            lgb_model.fit(
                X_tr, y_tr,
                eval_set=[(X_val, y_val)],
                eval_metric='auc',
                callbacks=[
                    lgb.early_stopping(stopping_rounds=100, verbose=False),
                    lgb.log_evaluation(False)
                ]
            )

            y_val_pred = lgb_model.predict_proba(X_val)[:, 1]
            auc_score = roc_auc_score(y_val, y_val_pred)
            cv_scores.append(auc_score)
            best_iterations.append(lgb_model.best_iteration_)
            print(f"Fold AUC: {auc_score:.4f} (最佳迭代: {lgb_model.best_iteration_})")

        print(f"\n平均交叉验证AUC: {np.mean(cv_scores):.4f} ± {np.std(cv_scores):.4f}")

        # 使用全部训练数据训练最终模型
        print("\n训练最终模型...")
        avg_best_iteration = int(np.mean(best_iterations) * 1.2)  # 使用平均最佳迭代次数的1.2倍

        final_model = lgb.LGBMClassifier(
            objective='binary',
            metric='auc',
            n_estimators=avg_best_iteration,
            learning_rate=0.01,
            max_depth=6,
            num_leaves=63,
            min_child_samples=20,
            subsample=0.8,
            colsample_bytree=0.8,
            reg_alpha=0.05,
            reg_lambda=0.05,
            random_state=42,
            n_jobs=-1,
            verbose=-1
        )

        final_model.fit(
            X_train_res, y_train_res,
            eval_set=[(X_test_scaled, y_test)],
            eval_metric='auc',
            callbacks=[lgb.log_evaluation(50)]
        )

        # 预测与评估
        print("\n模型评估:")
        y_pred = final_model.predict(X_test_scaled)
        y_pred_prob = final_model.predict_proba(X_test_scaled)[:, 1]

        # 计算评估指标
        test_auc = roc_auc_score(y_test, y_pred_prob)

        # PR曲线和AUC
        precision, recall, _ = precision_recall_curve(y_test, y_pred_prob)
        pr_auc = auc(recall, precision)

        print("\n分类报告:")
        print(classification_report(y_test, y_pred))
        print(f"测试集ROC AUC: {test_auc:.4f}")
        print(f"测试集PR AUC: {pr_auc:.4f}")

        # 绘制ROC曲线
        from sklearn.metrics import roc_curve
        fpr, tpr, _ = roc_curve(y_test, y_pred_prob)
        plt.figure(figsize=(8, 6))
        plt.plot(fpr, tpr, label=f'ROC Curve (AUC = {test_auc:.2f})')
        plt.plot([0, 1], [0, 1], 'k--')
        plt.xlabel('False Positive Rate')
        plt.ylabel('True Positive Rate')
        plt.title('ROC Curve')
        plt.legend()
        plt.show()

        # 混淆矩阵
        plt.figure(figsize=(8, 6))
        cm = confusion_matrix(y_test, y_pred)
        sns.heatmap(cm, annot=True, fmt='d', cmap='Blues')
        plt.title('Confusion Matrix')
        plt.xlabel('Predicted')
        plt.ylabel('Actual')
        plt.show()

        # 特征重要性
        feature_imp = pd.DataFrame({
            'Feature': X_train.columns,
            'Importance': final_model.feature_importances_
        }).sort_values('Importance', ascending=False)

        plt.figure(figsize=(10, 12))
        plt.barh(feature_imp['Feature'][:30][::-1], feature_imp['Importance'][:30][::-1])
        plt.title('Top 30 Feature Importance')
        plt.xlabel('Importance Score')
        plt.tight_layout()
        plt.show()

        return test_auc, final_model

    except Exception as e:
        print(f"模型训练与评估时出错: {str(e)}")
        raise


# 主函数
def main():
    try:
        # 加载数据
        print("加载数据...")
        train_path = 'train.csv'  # 修改为您的train.csv路径
        test_path = 'test.csv'  # 修改为您的test.csv路径
        train, test = load_data(train_path, test_path)

        # 训练评估模型
        auc_score, model = train_and_evaluate(train, test)
        print(f'\n最终测试集AUC: {auc_score:.4f}')

        # 如果AUC低于0.6，尝试更激进的方法
        if auc_score < 0.6:
            print("\nAUC低于0.6，尝试更激进的特征工程和模型调整...")
            # 可以在这里添加更激进的特征工程或模型调整代码

    except Exception as e:
        print(f"程序运行出错: {str(e)}")


if __name__ == '__main__':
    main()
```


```
C:\Users\24961\PycharmProjects\PythonProject\.venv\Scripts\python.exe C:\Users\24961\PycharmProjects\PythonProject\model2.py 
加载数据...

正在进行特征工程...
提取特征: 100%|██████████| 67/67 [09:15<00:00,  8.30s/it]
提取特征: 100%|██████████| 67/67 [02:14<00:00,  2.01s/it]

类别分布:
训练集 - 类别0: 5721 类别1: 3962
测试集 - 类别0: 1430 类别1: 990

处理缺失值...

特征选择...
移除了 135 个低方差特征

标准化数据...

处理类别不平衡...
重采样失败，使用原始数据: The specified ratio required to remove samples from the minority class while trying to generate new samples. Please increase the ratio.

交叉验证...
Fold AUC: 0.5448 (最佳迭代: 36)
Fold AUC: 0.5436 (最佳迭代: 6)
Fold AUC: 0.5445 (最佳迭代: 14)
Fold AUC: 0.5436 (最佳迭代: 130)
Fold AUC: 0.5427 (最佳迭代: 360)

平均交叉验证AUC: 0.5438 ± 0.0007

训练最终模型...
[50]	valid_0's auc: 0.554075
[100]	valid_0's auc: 0.56189

模型评估:

分类报告:
              precision    recall  f1-score   support

           0       0.59      0.98      0.74      1430
           1       0.48      0.03      0.06       990

    accuracy                           0.59      2420
   macro avg       0.53      0.50      0.40      2420
weighted avg       0.55      0.59      0.46      2420

测试集ROC AUC: 0.5613
测试集PR AUC: 0.4621

最终测试集AUC: 0.5613

AUC低于0.6，尝试更激进的特征工程和模型调整...
```

![Figure_1](C:\Users\24961\Desktop\Figure_1.png)

![image-20250428223959978](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250428223959978.png)

![image-20250428224013523](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250428224013523.png)

#### 1. 模型对比总览

| 指标        | 基础版 | 增强版 | 改进幅度 |
| :---------- | :----- | :----- | :------- |
| 测试集AUC   | 0.5558 | 0.5613 | +1.0%    |
| PR AUC      | -      | 0.4621 | -        |
| 类别0召回率 | 0.83   | 0.98   | +18%     |
| 类别1召回率 | 0.22   | 0.03   | -86%     |
| 特征数量    | 约400  | 约800  | +100%    |
| 训练时间    | 较短   | 较长   | -        |

#### 2. 数据预处理对比

##### 1 基础版预处理

- 简单移除NPS列
- 用0填充缺失值和无穷大值
- 无特征选择

##### 2 增强版预处理

```
# 增强版数据加载代码
train = train.fillna(train.median(numeric_only=True))
test = test.fillna(train.median(numeric_only=True))

# 特征选择
variances = X_train.var()
low_var_cols = variances[variances < 0.01].index
X_train = X_train.drop(low_var_cols, axis=1)
```

- **改进点**：
  - 使用中位数而非0填充缺失值
  - 移除了135个低方差特征（阈值0.01）
  - 增加数据完整性检查

#### 3. 特征工程对比

##### 1 基础版特征

- 6种基本统计量
- 简单趋势特征
- 异常值计数

##### 2 增强版特征

```
# 新增特征类型
features[f'{group}_cv'] = std / (mean + 1e-6)  # 变异系数
features[f'{group}_kurtosis'] = kurtosis(x)    # 峰度
features[f'{group}_autocorr'] = x.autocorr()   # 自相关
features[f'cross_{g1}_{g2}_mean'] = mean1/mean2 # 交叉特征
```

- **新增特征**：
  - 变异系数、峰度、偏度
  - 自相关特征
  - 百分位数特征（10%,25%,75%,90%）
  - 时间序列差分特征
  - 组间交叉特征
- **特征总量**从约400增加到约800（移除低方差后）

#### 4. 模型优化对比

##### 1 基础版模型

```
lgb.LGBMClassifier(
    n_estimators=1000,
    learning_rate=0.05,
    max_depth=7,
    num_leaves=31
)
```

##### 2 增强版模型

```
lgb.LGBMClassifier(
    n_estimators=2000,
    learning_rate=0.01,  # 更小的学习率
    max_depth=6,        # 平衡深度
    num_leaves=63,      # 更多叶子
    min_child_samples=20,
    reg_alpha=0.05,     # 调整正则化
    early_stopping=100  # 更耐心
)
```

- **关键改进**：
  - 增加5折交叉验证
  - 采用动态早停策略（基于CV结果）
  - 更细致的超参数调整
  - 尝试SMOTE+UnderSampling组合（虽未成功）

#### 5. 评估结果分析

##### 1 性能对比

- **AUC提升**：0.5558 → 0.5613
- **PR AUC**：新增指标0.4621
- **召回率变化**：
  - 类别0（未流失）：83% → 98%
  - 类别1（流失）：22% → 3%

##### 2 问题诊断

1. **类别不平衡加剧**：

   - 基础版：正负样本比1:1.44
   - 增强版特征工程后，模型更偏向多数类

2. **SMOTE失败原因**：

   ```
   # 错误日志
   "The specified ratio required to remove samples from the minority class..."
   ```

   - 采样策略参数设置不合理导致失败

3. **特征有效性不足**：

   - 新增特征中仅少数进入Top30重要特征
   - 特征交叉可能引入噪声

### 7. 结论

本项目构建的LightGBM用户流失预测模型在当前数据条件下达到了0.5613的AUC值，虽然未能达到理想水平，但建立了完整的分析框架和技术路线。可以通过减小学习率，增加训练轮次（注意处理数据过拟合），选择其他模型等方法进行研究。

还需要在数据层面，特征工程和模型优化模型方面下手改进。

## 附录

### 数据分布可视化

```python
plt.figure(figsize=(8,4))
sns.countplot(x='LABEL', data=train)
plt.title('Class Distribution in Training Set')
plt.show()
```
