# ollama

来自文章  [Ollama 教程：从 0 到 1 全面指南 教程【全文两万字保姆级详细讲解】 _ollama教程 从0到1-CSDN博客](https://blog.csdn.net/2402_87298751/article/details/146020076)

## 工具简介

Ollama 是一个开源的大型语言模型（LLM）工具，专注于在本地计算机上运行和部署各种 AI 模型。它简化了下载、管理和运行 LLM（如 LLaMA、Mistral、Gemma 等）的流程，支持 macOS、Linux 和 Windows（预览版）。

## 下载的ollama文件

在 Ollama 的模型存储目录中（如 `~/.ollama/models`），`blobs` 和 `manifests` 是两个核心文件夹，分别用于存储模型的不同组成部分。以下是它们的详细区别和作用：

------

### **1. `blobs` 文件夹**

- **内容**：存储模型的**实际数据文件**（二进制块），包括：

  - 模型权重（如量化后的 `.bin` 或 `.safetensors` 文件）。
  - 配置文件（如 `config.json`、`tokenizer.json`）。
  - 其他依赖文件（如词汇表、元数据等）。

- **特点**：

  - 文件以 **SHA256 哈希值**命名（如 `sha256:1234abc...`），确保唯一性。
  - 多个模型可能共享相同的 blob（如基础配置相同的变体）。

- **示例**：

  bash

  复制

  ```
  ~/.ollama/models/blobs/
  ├── sha256:1a2b3c...  # 模型权重文件
  ├── sha256:4d5e6f...  # 配置文件
  └── ...
  ```

------

### **2. `manifests` 文件夹**

- **内容**：存储模型的**描述文件（Manifest）**，本质是 JSON 文件，记录：

  - 模型名称、版本、作者等元信息。
  - 该模型依赖的 **blob 文件列表**（通过哈希值引用）。
  - 运行时配置（如上下文窗口大小、GPU 层数等）。

- **特点**：

  - 文件以命名格式 `<registry>/<model>:<tag>` 存储（如 `registry.ollama.ai/library/llama3:latest`）。
  - 通过 Manifest 关联多个 Blob，构建完整的模型。

- **示例**：

  json

  复制

  ```
  // ~/.ollama/models/manifests/registry.ollama.ai/library/llama3:latest
  {
    "schemaVersion": 2,
    "mediaType": "application/vnd.ollama.image.model",
    "config": {
      "digest": "sha256:4d5e6f...",  // 指向 blobs 中的配置文件
      "size": 1234
    },
    "layers": [
      {
        "digest": "sha256:1a2b3c...",  // 指向 blobs 中的权重文件
        "size": 4567890
      }
    ]
  }
  ```

------

### **3. 协作流程**

1. **下载模型**：
   - 当你运行 `ollama pull llama3` 时：
     - 先下载 Manifest（描述模型结构）。
     - 再根据 Manifest 中的哈希值下载对应的 Blobs。
2. **运行模型**：
   - Ollama 读取 Manifest，按需加载 Blobs 到内存/显存。

------

### **4. 用户操作建议**

- **手动清理**：
  - 删除 `blobs` 中的文件可能导致模型损坏（需通过 `ollama rm <model>` 安全删除）。
- **共享模型**：
  - 复制整个 `~/.ollama/models` 文件夹可迁移模型到另一台机器。
- **调试问题**：
  - 检查 Manifest 的 JSON 文件可确认模型配置是否正确。

------

### **总结**

- **Blobs** = 模型的“血肉”（实际数据文件）。
- **Manifests** = 模型的“蓝图”（描述如何组装数据）。

这种设计提高了存储效率（避免重复下载相同 Blob）并支持灵活的模型版本管理。

## **主要功能**

1. **本地运行模型**
   - 无需云端依赖，直接在个人电脑上运行 LLM，保护隐私。
   - 支持多平台（包括 Apple Silicon 芯片的 GPU 加速）。
2. **丰富的模型库**
   - 提供多种开源模型，例如：
     - `llama3`（Meta）、`mistral`、`phi3`（微软）、`gemma`（Google）
     - 代码专用模型（`deepseek-coder`）、多模态模型（`bakllava`）等。
   - 可通过 `ollama pull` 下载模型，例如：`ollama pull llama3:70b`。
3. **命令行与 API 支持**
   - 通过命令行交互（类似聊天界面）或集成到其他应用。
   - 提供 REST API，方便开发者调用（默认端口 `11434`）。
4. **自定义模型**
   - 用 Modelfile 修改模型参数（如温度、系统提示词），创建自己的微调版本。

## Ollama 相关命令

1、使用方法

ollama [flags]：使用标志（flags）运行 ollama。

ollama [command]：运行 ollama 的某个具体命令。

2、可用命令

serve：启动 ollama 服务。
create：根据一个 Modelfile 创建一个模型。
show：显示某个模型的详细信息。
run：运行一个模型。
stop：停止一个正在运行的模型。
pull：从一个模型仓库（registry）拉取一个模型。
push：将一个模型推送到一个模型仓库。
list：列出所有模型。
ps：列出所有正在运行的模型。
cp：复制一个模型。
rm：删除一个模型。
help：获取关于任何命令的帮助信息。

3、标志（Flags）

-h, --help：显示 ollama 的帮助信息。
-v, --version：显示版本信息。

## 具体操作

#### 拉取模型

从模型库中下载模型：

```cobol
ollama pull <model-name>
```

#### 运行模型

运行已下载的模型：

```cobol
ollama run <model-name>
```

退出正在运行的模型

| 场景         | 操作                     |
| :----------- | :----------------------- |
| 退出交互对话 | 输入 `/exit` 或 `Ctrl+C` |
| 停止后台服务 | `Ctrl+C` 或 `kill` 进程  |

#### 删除模型

删除本地模型：

```cobol
ollama rm <model-name>

      //成功
deleted 'llama3.1:8b' 
```

#### 创建自定义模型

基于现有模型创建自定义模型：

```cobol
ollama create <custom-model-name> -f <Modelfile>
```

复制一个已存在的模型：

```cobol
ollama cp <source-model-name> <new-model-name>
```

#### 推送自定义模型

将自定义模型推送到模型库：

```cobol
ollama push <model-name>
```

#### 启动 Ollama 服务

启动 Ollama 服务以在后台运行：

```undefined
ollama serve
```

#### 停止 Ollama 服务

停止正在运行的 Ollama 服务：

```vbscript
ollama stop
```

#### 重启 Ollama 服务

重启 Ollama 服务：

```undefined
ollama restart
```

## 基本概念理解

### 模型（Model）

在 Ollama 中，模型是核心组成部分。它们是经过预训练的机器学习模型，能够执行不同的任务，例如文本生成、文本摘要、情感分析、对话生成等。

模型的主要功能：

推理（Inference）：根据用户输入生成输出结果。
微调（Fine-tuning）：用户可以在已有模型的基础上使用自己的数据进行训练，从而定制化模型以适应特定的任务或领域。

模型通常是由大量参数构成的神经网络，通过对大量文本数据进行训练，能够学习语言规律并进行高效的推理。

### 交互

Ollama 提供了多种方式与模型进行交互，其中最常见的就是通过命令行进行推理操作。

#### 1. 命令行交互

通过命令行直接与模型进行交互是最简单的方式。

#### 2. 单次命令交互

如果您只需要模型生成一次响应，可以直接在命令行中传递输入。

##### 使用管道输入

通过管道将输入传递给模型：

```vbscript
echo "who are you?" | ollama run deepseek-r1:1.5b

<think>
</think>
Greetings! I'm DeepSeek-R1, an artificial intelligence assistant created by DeepSeek. I'm at your service and
would be delighted to assist you with any inquiries or tasks you may have.
```

##### 使用命令行参数

直接在命令行中传递输入：

````python
ollama run deepseek-r1:1.5b "the code of"hello world" in python？"
<think>
嗯，用户问的是“hello world in python？”，看起来是想了解Python中如何编写“Hello, World!”。首先，我应该确认用户的需
求是否只是简单输入这个句子，还是有没有更深入的用途。

然后，我会回忆一下常见的编程练习，比如使用print函数输出结果。这样用户能理解基本的步骤和逻辑结构。接下来，考虑用户可
能没有说出来的深层需求，比如希望了解完整的代码或如何在不同环境中运行，但这可能超出了基本问题。

最后，我会给出一个简短且明确的回答，直接写出Python代码，并解释每个部分的作用，让用户能够轻松理解和复制。
</think>

要输出“Hello, World!”，可以在Python中使用`print`函数。

以下是简单的代码示例：

```python
print("Hello, World!")
```

当执行这段代码时，Python会打印出“Hello, World!”。
````

#### 3.多轮对话

Ollama 支持多轮对话，模型可以记住上下文。

```
>>> 你好，你能帮我写一段 Python 代码吗？
当然可以！请告诉我你需要实现什么功能。
 
>>> 我想写一个计算斐波那契数列的函数。
好的，以下是一个简单的 Python 函数：
 
def fibonacci(n):
    if n <= 1:
        return n
    else:
        return fibonacci(n-1) + fibonacci(n-2)
```



#### 4.文件输入

可以将文件内容作为输入传递给模型。

假设 input.txt 文件内容为：

``` tex
将 input.txt 文件内容作为输入：
Python 的 hello world 代码？
```



```cobol
ollama run deepseek-coder < input.txt
```



------------------------------------------------

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。

原文链接：https://blog.csdn.net/2402_87298751/article/details/146020076

#### 5.自定义提示词

通过 Modelfile 定义自定义提示词或系统指令，使模型在交互中遵循特定规则。

##### **创建Modelfile**

```powershell
# 在PowerShell中执行：
New-Item -Path ".\Modelfile" -ItemType File -Force

notepad .\Modelfile
```

创建的这个文件会在

- **`.\Modelfile`** 中的 `.` 表示 **当前工作目录**：
  - 如果直接在PowerShell窗口运行命令，文件会保存在 **当前打开的目录**。
  - 如果通过脚本运行，文件会保存在 **脚本所在的目录**。

可以通过以下代码查看

```powershell
# 查看当前工作目录
Get-Location

# 列出当前目录文件（确认Modelfile是否存在）
Get-ChildItem
```

输入命令后会出现记事本，输入以下内容

```dockerfile
FROM yourModelName
SYSTEM """
你是一个专业的编程助手，规则：
1. 使用中文回答
2. 代码必须带注释
3. 拒绝非技术问题
"""
PARAMETER temperature 0.5
```

**构建自定义模型**

```powershell
ollama create my-win-coder -f .\Modelfile
```

实现

![image-20250420002039468](../AppData/Roaming/Typora/typora-user-images/image-20250420002039468.png)

## Ollama API 交互

Ollama 提供了基于 HTTP 的 API，允许开发者通过编程方式与模型进行交互。

### 1. 启动 Ollama 服务

在使用 API 之前，需要确保 Ollama 服务正在运行。可以通过以下命令启动服务：

```undefined
ollama serve
```

默认情况下，服务会运行在 http://localhost:11434。

![image-20250416233559168](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250416233559168.png)

### 2.API 端点

Ollama 提供了以下主要 API 端点：

#### 生成文本（Generate Text）

**端点**：`POST /api/generate`

**功能**：向模型发送提示词（prompt），并获取生成的文本。

```
import requests

response = requests.post(
    "http://localhost:11434/api/generate",
    json={
        "model": "phi3",
        "prompt": "为什么天空是蓝色的？",
        "stream": False
    }
)
print(response.json())
```

![image-20250416233820850](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250416233820850.png)

```
# Python 示例
import requests

response = requests.post(
    "http://localhost:11434/api/generate",
    json={
        "model": "phi3",
        "prompt": "用五步解释如何泡茶",
        "stream": True  # 启用流式
    },
    stream=True
)

for chunk in response.iter_lines():
    if chunk:
        print(chunk.decode('utf-8'))  # 实时打印片段
```

![image-20250416234212696](C:\Users\24961\AppData\Roaming\Typora\typora-user-images\image-20250416234212696.png)