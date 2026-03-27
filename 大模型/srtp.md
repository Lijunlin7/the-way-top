# srtp

## 模型分别对于项目的作用

### **1. Phi-3.5（大语言模型）**

**作用**：作为AI智能体的“大脑”，负责任务规划、决策与协调。
**具体功能**：

- **任务理解与分解**：解析用户输入的文本描述（如“生成一个推销咖啡的虚拟代言人视频”），将其拆解为子任务（生成广告文案→生成图像→生成语音→合成视频）。 将拆解后的任务输入到不同起作用的模型之中去。
- **工具调度**：根据任务需求调用其他模型（如触发Stable Diffusion生成图像、TTS生成语音）。
- **流程控制**：确保各模块按顺序执行，处理模块间的数据传递（如将生成的图像和音频传递给Real3DPortrait）。
- **优化交互**：通过高质量Prompt设计，明确任务边界，提升生成内容的准确性和连贯性。

**项目中的创新点**：

- 采用轻量级模型Phi-3.5，降低计算资源需求，适合嵌入式或移动端部署。
- 未来可扩展为自主决策（如动态调整视频风格以适应不同受众）。

### 流程

1. **任务解析**：理解用户输入，生成结构化任务步骤。
2. **工具调用**：根据任务步骤依次调用其他模型。
3. **数据传递**：将前一个模型的输出作为下一个模型的输入。

####  **Phi-3.5 任务规划与调度**

```python
from transformers import AutoModelForCausalLM, AutoTokenizer
import json

# 加载Phi-3.5模型
model_name = "microsoft/phi-3-mini-4k-instruct"
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForCausalLM.from_pretrained(model_name)

def plan_tasks(user_input):
    """使用Phi-3.5分解用户输入为任务步骤"""
    prompt = f"""将以下需求分解为任务步骤（JSON格式）：
    输入：{user_input}
    输出示例：{{"steps": ["生成广告文案", "生成人物图像", "生成语音", "合成视频"]}}"""
    
    inputs = tokenizer(prompt, return_tensors="pt")
    outputs = model.generate(**inputs, max_length=500)
    plan = tokenizer.decode(outputs[0], skip_special_tokens=True)
    return json.loads(plan.split("```json")[-1].split("```")[0])  # 提取JSON部分

# 示例：用户输入
user_input = "生成一个咖啡广告视频，代言人为年轻职业女性"
task_plan = plan_tasks(user_input)
print("任务计划:", task_plan)
```

**输出示例**：

```python
{
  "steps": [
    "生成广告文案：'体验我们的新品咖啡，唤醒你的早晨！'",
    "生成人物图像：20岁亚洲女性，职业装，手持咖啡杯",
    "生成语音：将广告文案转为女性推销员语音",
    "合成视频：将图像和语音合成为说话人视频"
  ]
}
```

#### 调用模型

```python
from transformers import AutoModelForCausalLM, AutoTokenizer

# 加载Phi-3.5模型
model_name = "microsoft/phi-3-mini-4k-instruct"
tokenizer = AutoTokenizer.from_pretrained(model_name)
model = AutoModelForCausalLM.from_pretrained(model_name)

# 输入任务描述
prompt = """根据以下需求生成任务步骤：
1. 生成一段咖啡广告文案；
2. 生成一个年轻女性手持咖啡杯的职业形象；
3. 将文案转为语音；
4. 合成说话人视频。"""

inputs = tokenizer(prompt, return_tensors="pt")
outputs = model.generate(**inputs, max_length=500)
print(tokenizer.decode(outputs[0], skip_special_tokens=True))  	
```

### **2. Stable Diffusion（图像生成模型）**

**作用**：根据文本生成高质量人物/场景图像，为视频提供视觉输入。

- **角色定制**：生成符合描述的人物形象（如“20岁亚洲女性，微笑，手持咖啡杯”）。
- **风格控制**：支持写实、卡通等不同风格适配广告或小说场景。

**调用代码示例（Python + Diffusers库）**：

```python
from diffusers import StableDiffusionPipeline
import torch

# 加载Stable Diffusion模型
pipe = StableDiffusionPipeline.from_pretrained("runwayml/stable-diffusion-v1-5", torch_dtype=torch.float16)
pipe = pipe.to("cuda")

# 生成图像
prompt = "A professional young woman holding a coffee cup, smiling, photorealistic"
image = pipe(prompt).images[0]
image.save("coffee_woman.png")
```

------

#### **调用Stable Diffusion生成图像**

```python
from diffusers import StableDiffusionPipeline
import torch

def generate_image(prompt):
    pipe = StableDiffusionPipeline.from_pretrained("runwayml/stable-diffusion-v1-5", torch_dtype=torch.float16)
    pipe = pipe.to("cuda")
    image = pipe(prompt).images[0]
    image.save("generated_image.png")
    return "generated_image.png"

# 从任务计划中提取图像描述
image_prompt = task_plan["steps"][1].split("：")[-1]
image_path = generate_image(image_prompt)
```

### ```image_prompt = task_plan["steps"][1].split("：")[-1]```代码逐层解析

#### **(1) 选择任务步骤**

python

```
task_plan["steps"][1]
```

- `task_plan["steps"]`：获取所有步骤的列表。
- `[1]`：选择列表中的第2个元素（Python索引从0开始），即：
  `"生成人物图像：20岁亚洲女性，职业装，手持咖啡杯"`

#### **(2) 分割字符串**

python

```
.split("：")
```

- 用中文冒号 `：` 分割字符串，得到列表：
  `["生成人物图像", "20岁亚洲女性，职业装，手持咖啡杯"]`

#### **(3) 提取最后部分**

python

```
[-1]
```

- 取分割后的最后一个元素（即冒号后的内容）：
  `"20岁亚洲女性，职业装，手持咖啡杯"`

### **3. TTS（文本到语音模型，如OpenVoice）**

**作用**：将文案转换为自然语音，为视频提供音频输入。

- **语音风格**：支持不同年龄、性别、情感的语音合成（如“热情推销员”或“温和讲故事”）。

**调用代码示例（Python + TTS库）**：

```python
from TTS.api import TTS

# 加载OpenVoice模型
tts = TTS(model_name="tts_models/multilingual/multi-dataset/your_tts", progress_bar=False)

# 生成语音
text = "Try our new aromatic coffee blend, perfect for your morning boost!"
tts.tts_to_file(text=text, file_path="ad_voice.wav")
```

------

#### **调用TTS生成语音**

```python
from TTS.api import TTS

def generate_speech(text, output_path="output_voice.wav"):
    tts = TTS(model_name="tts_models/multilingual/multi-dataset/your_tts")
    tts.tts_to_file(text=text, file_path=output_path)
    return output_path

# 从任务计划中提取广告文案
ad_text = task_plan["steps"][0].split("：")[-1].strip("'")
voice_path = generate_speech(ad_text)
```

### **4. Real3DPortrait（视频生成模型）**

**作用**：将静态图像与音频合成为动态说话人头视频，确保口型同步。

- **多模态对齐**：精确匹配语音节奏与人物口型、表情。
- **自然度优化**：生成高真实感的说话人视频。

**调用代码示例（Python + 自定义API）**：

```python
import requests

# 调用Real3DPortrait API（假设已部署）
url = "http://localhost:5000/generate_video"
files = {
    "image": open("coffee_woman.png", "rb"),
    "audio": open("ad_voice.wav", "rb")
}
```

#### **调用Real3DPortrait合成视频**

```python
import requests

def generate_video(image_path, audio_path, output_path="output_video.mp4"):
    # 假设Real3DPortrait已部署为本地API
    url = "http://localhost:5000/generate_video"
    files = {
        "image": open(image_path, "rb"),
        "audio": open(audio_path, "rb")
    }
    response = requests.post(url, files=files)
    with open(output_path, "wb") as f:
        f.write(response.content)
    return output_path

video_path = generate_video(image_path, voice_path)
print("视频生成完成:", video_path)
```



## 为什么选择phi-3.5模型

### **1. 轻量化与高效性**

- **低资源需求：(小模型)**
  Phi-3.5 是微软推出的 **小型语言模型（SLM）**，参数量相对较小（如Phi-3-mini仅3.8B参数），但对标更大模型的性能。相比GPT-4等巨型模型（万亿级参数），Phi-3.5 在 **内存占用和计算成本** 上显著更低，适合本地化部署或边缘设备（如桌面AI助手），降低项目硬件门槛。
- **实时响应**：
  轻量级架构使其在任务规划和工具调用时延迟更低，适合需要快速生成视频的流水线（如广告视频的实时渲染）。

------

### **2. 任务规划与工具调用的适配性**

- **精准控制**：
  Phi-3.5 的设计注重 **任务分解与工具协作**，其推理能力针对多步骤任务（如“文本→图像→语音→视频”的链式调用）进行了优化。相比之下，GPT-4等通用模型可能过度生成冗余内容，增加流程控制的复杂性。
- **Prompt工程友好**：
  项目提到依赖高质量Prompt明确任务边界，Phi-3.5 对结构化指令（如“现在调用Stable Diffusion生成图像”）的响应更稳定，减少不可预测的输出。

------

### **3. 开源与可定制性**

- **开放生态**：
  Phi-3.5 是 **开源模型**（MIT许可证），允许团队自由修改、微调或集成到现有系统中。而GPT-4等闭源模型仅能通过API调用，存在数据隐私、成本（按token计费）和功能限制（无法深度定制工具交互逻辑）。
- **本地化部署**：
  适合对隐私敏感的场景（如企业广告生成），避免用户数据外流到第三方云服务。

------

### **4. 与多模态工具的兼容性**

- **模块化协同**：
  Phi-3.5 作为“大脑”需与Stable Diffusion、Real3DPortrait等独立模型协作。其轻量化和接口设计更易与其他工具（如TTS服务）集成，而大型模型可能因复杂的输入输出格式增加集成难度。
- **资源分配平衡**：
  视频生成本身已需大量GPU资源（如Real3DPortrait），若LLM部分过于庞大，整体系统资源需求会成瓶颈。

------

### **5. 成本效益**

- **训练与推理成本低**：
  使用Phi-3.5可大幅降低长期运营成本（无需为API调用付费，或租用高端算力），尤其适合学术项目或初创团队。
- **性价比高**：
  在性能接近中型模型（如Llama 2-7B）的同时，保持更小的体积，适合预算有限但需高效落地的场景。

| 模型        | 主要缺点（对本项目而言）                                     |
| :---------- | :----------------------------------------------------------- |
| **GPT-4**   | 闭源、API依赖性强、成本高；过度生成内容可能干扰工具调用流程。 |
| **Gemini**  | 多模态能力冗余（本项目已分模块处理图像/音频）；闭源且需谷歌生态集成。 |
| **Claude**  | 长文本优化但对工具调度支持不足；同样存在API限制和隐私问题。  |
| **Llama 3** | 参数量较大（8B以上），本地部署资源需求仍高于Phi-3.5。        |
| **Mistral** | 虽轻量但更侧重通用对话，Phi-3.5在任务规划上针对性更强。      |