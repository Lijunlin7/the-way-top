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

### Stable Diffusion（图像生成模型）**

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

### **3. TTS（文本到语音模型，如OpenVoice）**

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

