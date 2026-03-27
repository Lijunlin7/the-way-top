# langchain

LangChain 是一个用于构建基于大语言模型（LLM）应用的开发框架，它通过模块化设计简化了LLM应用的开发流程，支持灵活组合各种组件（如模型调用、数据检索、工作流控制等）。

## **1. 核心功能**

- **模型集成**
  支持多种LLM（如OpenAI、Anthropic、HuggingFace等），可轻松切换或组合不同模型。
- **模块化组件**
  提供标准化接口的模块，包括：
  - **Prompt模板**：动态生成提示词，支持变量注入。
  - **记忆（Memory）**：管理对话历史（如聊天机器人上下文）。
  - **数据检索**：集成向量数据库（如FAISS、Pinecone）实现RAG（检索增强生成）。
  - **链（Chains）**：将多个步骤串联（如“调用API→处理结果→生成回答”）。
  - **代理（Agents）**：让LLM自主调用工具（如搜索、计算等）。
- **扩展性**
  支持自定义工具和插件，方便对接外部API或私有数据源。

------

## **2. 典型应用场景**

- **问答系统**：结合RAG从文档中提取答案。
- **智能助手**：集成工具调用（如日历、天气API）。
- **数据增强分析**：将LLM与SQL、Python工具链结合。
- **自动化流程**：处理多步骤任务（如邮件分类→摘要生成→回复起草）。

## Langchain模型I/O之模型包装器

### 使用LLM模型包装器进行调用

从langchain.llms中引入OpenAI，这里引入的是LLM模型

```python
import langchain
import os

//从langchain.llms中引入OpenAI，这里引入的是LLM模型
from langchain.llms import OpenAI
 
api_key = os.getenv("OPENAI_API_KEY")

llm = OpenAI(
    openai_api_key = api_key, 
    temperature=0.5,
    base_url="https://wdapi7.61798.cn/v1"
 
)
response = llm.invoke('谁是中国改革开放的总设计师？')
print(response,type(response))
 
```

#### temperature

**temperature** 是一个关键的超参数，用于控制生成文本的**随机性和创造性**。

 **核心作用**:**控制概率分布的平滑度**
模型每一步会生成一个所有可能词的概率分布，`temperature` 通过调整该分布的陡峭程度，决定是否更倾向于高概率词（保守）或给低概率词更多机会（创新）。

| **Temperature值** | **生成效果**                           | **适用场景**                       |
| :---------------- | :------------------------------------- | :--------------------------------- |
| **低（0~0.3）**   | 输出确定性高，选择最可能的词，重复性低 | 事实性问答、代码生成、标准化文本   |
| **中（0.5~0.7）** | 平衡创造性和连贯性                     | 聊天机器人、内容创作（如博客草稿） |
| **高（>1.0）**    | 高度随机，可能不连贯或荒谬             | 诗歌生成、头脑风暴、探索性创意     |

### 使用聊天模型包装器进行调用

```python
import langchain
import os
 
from langchain_openai import ChatOpenAI
 
 
api_key = os.getenv("OPENAI_API_KEY")
 
llm = ChatOpenAI(
openai_api_key=api_key,
temperature = 0.5,
base_url = "https://apejhvxcd.cloud.sealos.io/v1"
)
 
response = llm.invoke("中国改革开放的总设计师是谁?")
 
print(response,type(response))
```

**注意两者中模型只是封装模型的容器发生了变化**

## 提示Prompt

引入了langchain_core.prompts  里面 ChatPromptTemplate 模板

用from_messages()方法来创建的模板

```python
import langchain
import os
 
from langchain_openai import ChatOpenAI
 
# 引入模板 ChatPromptTemplate
from langchain_core.prompts import ChatPromptTemplate
 
api_key = os.getenv("OPENAI_API_KEY")
 
llm = ChatOpenAI(
    openai_api_key=api_key,
    temperature = 0.5,
    base_url = "https://apejhvxcd.cloud.sealos.io/v1"
)
 
 
 
prompt = ChatPromptTemplate.from_messages([
    ("system", "你是一个中国历史专家."),
    ("user", "{input}")
])
 
chain = prompt | llm
 
chain.invoke({"input":"中国改革开放的总设计师是谁?"})
```

我们实例化了一个ChatPromtTemplate，并使用它的from_messages()方法来接受信息输入。这个方法获取一个List列表，List里有两个元组，第一个元组是system系统消息，这个是对语言模型响应的定义和限制

使用一个更常用的方法from_template()来创建提示词模板

```python
prompt = (
    ChatPromptTemplate.from_template("给我讲一个关于{topic}的笑话")
    + "\n\n 并且用 {language} 来讲"
)
 
chain = prompt | llm
 
chain.invoke({"topic":"爱情","language":"英文"})
```

## 提示词工程

比较长的template内容，可以这样进行设置更加清晰（这种形式是使用的最多的），注意，你可以在你的提示词里加入明确的对大模型的要求，比如这里的template中就加入了对输出的要求，希望大模型不仅要回答正确的问题，还要用诗一样的语言来描述这个答案，这个就是提示词工程的雏形，就是你可以用prompt去引导大语言模型如何思考和输出

```python
from langchain import PromptTemplate
 
template = """
你是一个旅游销售员，你不仅要告诉用户正确的答案，还要用诗一样的语言来描述这个答案！
请告诉我{country}的首都是那座城市？
"""
 
prompt3 = PromptTemplate(template = template, input_variables=["country"])
 
chain3 = prompt3 | llm
 
chain3.invoke({"country":"英国",})
```

## 输出解析器

使用的聊天[包装器](https://so.csdn.net/so/search?q=包装器&spm=1001.2101.3001.7020)输出的是一个AIMessage类型的消息体，而我们在组建chain链的时候，往往需要输出str或者其他格式的输出，因此这里会引入输出解析器的概念，它的作用把langchain的输出进行格式转化

```python
import langchain
import os
 
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
 
api_key = os.getenv("OPENAI_API_KEY")
 
llm = ChatOpenAI(
    openai_api_key=api_key,
    temperature=0.9,
    base_url = "https://apejhvxcd.cloud.sealos.io/v1"
)
 
prompt = ChatPromptTemplate.from_messages([
    ("system", "你是一个中国历史专家."),
    ("user", "{input}")
])
 
//输出提示器
output_parser = StrOutputParser()
 
chain = prompt | llm | output_parser
 
response = chain.invoke({"input":"简单介绍一下宋朝的历史"})
print(response,type(response))
```

llm模型进行函数绑定和控制，这里我们给一个最简单的示例，在上面代码的基础上给llm模型加一个绑定和限制。这个绑定的意思是说，当模型的输出遇到换行符（“\n”）就停止不再输出了，加上这个之后我们可以看到同样的问题

```python
llm2 = ChatOpenAI(
    openai_api_key=api_key,
    temperature=0.9,
    base_url = "https://apejhvxcd.cloud.sealos.io/v1"
)
 
prompt2 = ChatPromptTemplate.from_messages([
    ("system", "你是一个中国历史专家."),
    ("user", "{input}")
])
 
output_parser = StrOutputParser()
 
chain2 = prompt2 | llm2.bind(stop=["\n"]) | output_parser
 
chain2.invoke({"input":"简单介绍一下宋朝的历史"})
```

## 数据增强

1. 加载数据（Load）
        我们可以加载的数据源包括但不限于各种文本文档（PDF、TXT、HTML、CSV等），数据库，互联网实时数据，API接口数据等。在本文中首先将会以PDF文件为例进行加载，在后面介绍create_sql_query_chain 的文章中，我们将会介绍如何加载数据库数据。

        在langchain_community中有一个专门的库，document_loaders中有各种数据加载器，比如有PDF加载器PyPDFLoader，有网络数据加载器AsyncChromiumLoader用于加载网页数据等等。

2. 转化数据（Transform）
        这一步是将加载后的数据进行处理，基本上都是做切分处理，把较大块的文本数据切分成小块的文本数据，方便进行向量化处理。

3. 词嵌入（Embedding）
        将文本转化为词向量的过程，只有向量化之后的文本才可以进行相似性比较等运算，这里不再展开，大家感兴趣的可以自己查找相关文章，这里面学问很大很大，要是展开了讲，讲两天也不一定能讲完。

4. 向量存储（Store to Vector）
        将向量化的数据存储入FAISS或Chroma等向量数据库。

5. 检索获取数据（Retrieval）
        在向量数据库里进行数据检索和查找，获取所需数据。
### 加载数据

    ```python
    import sys
     
    from langchain_community.document_loaders import PyPDFLoader


​     
​    loader = PyPDFLoader(r"C:\Users\Administrator\Desktop\AI\PRD.pdf", extract_images=True)
​    pages = loader.load()
​     
​    print(pages[1])
​    print(type(pages))
​    ```

通过下面的代码查看加载出来数据的内存地址和内存大小

```python
memory_address = id(pages)
print(f"Memory Address: {memory_address}")
 
object_size = sys.getsizeof(pages)
print(f"Object Size: {object_size} bytes")
```

### 数据切分

加载出来的数据一般都比较大，需要进行切分，就是把大块的数据切成小块的数据方便进行向量化处理

引入了一个RecursiveCharacterTextSplitter，我们设置我们切分文本的大小为200个字符，然后chunk_overlap的意思是重叠，就是为了防止我们切分的时候把一个完整的句子拆散，我们需要一部分重叠来做冗余保护。之前我们使用loader的load()方法，此时，我们要是用load_and_split方法，然后把我们构造的text_splitter传进去，这样我们就成功对pages这个对象进行了数据转换（切分）

```
from langchain.text_splitter import RecursiveCharacterTextSplitter
 
text_splitter = RecursiveCharacterTextSplitter(
                chunk_size = 200,
                chunk_overlap  = 50,
                length_function = len,
            )
 
pages_splitter = loader.load_and_split(text_splitter)
 
 
object_size = sys.getsizeof(pages_splitter)
print(f"Object Size: {object_size} bytes")
 
print(pages_splitter)
```

### 词嵌入

```python
import os

//首先引入向量数据库Chroma
from langchain.vectorstores import Chroma 
// 引入openai的词向量工具
from langchain_openai import OpenAIEmbeddings
 
api_key = os.getenv("OPENAI_API_KEY")
 
    //   然后构造一个Chroma向量数据库，将上一节课我们已经做好切分的pages_splitter传入，将词向量工具embedding传入，这样就构造出来了一个向量数据库，可以使用向量数据库的similarity_search方法进行向量搜索，就是寻找向量数据库里和查询问题语义最相近的向量值并返回结果，可以得到查询结果  
    
vectorstore = Chroma.from_documents(
    pages_splitter, embedding=OpenAIEmbeddings(openai_api_key=api_key,base_url = "https://apejhvxcd.cloud.sealos.io/v1"),
)
 
query = "需求总体思路是什么？"
docs = vectorstore.similarity_search(query)
print(docs[0].page_content)
```

我们将四条事实向量化存储进了数据库，然后我们提了一个问题，langchain将向量化的数据库以检索器的形式和问题（同样向量化）一并送给了大模型进行匹配，大模型通过向量搜索发现针对我们的问题，“harrison worked at kensho”这条数据的相似度是最高的，他依据这条匹配结果正确地回答了我们的问题

### 一个完整的加载、转换、词嵌入、存储和检索过程

```python
import os
 
 #这个向量数据库有bug
# from langchain_community.vectorstores import DocArrayInMemorySearch
 
#换一个新的数据库， pip install chromadb
from langchain.vectorstores import Chroma 
 
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.runnables import RunnableParallel, RunnablePassthrough
 
#这里不写chat_models ,直接从langchain_openai里调ChatOpenAI也OK
from langchain_openai import ChatOpenAI
from langchain_openai.embeddings import OpenAIEmbeddings
api_key = os.getenv("OPENAI_API_KEY")
vectorstore = Chroma.from_texts(
    ["harrison worked at kensho", "bears like to eat honey","jerry likes jenny","today is a good day"],
    OpenAIEmbeddings(base_url = "https://apejhvxcd.cloud.sealos.io/v1"),
)
     
#使用from_texts方法，加载一组字符串，然后还是用openai的向量工具做embedding词嵌入，然后我们使用as_retriever()这个方法构造了一个检索器 retriever    
retriever = vectorstore.as_retriever()

template = """Answer the question based only on the following context:
{context}
Question: {question}
"""
     
prompt = ChatPromptTemplate.from_template(template)
model = ChatOpenAI(openai_api_key=api_key,temperature=0.8)
output_parser = StrOutputParser()
 
    #这个检索器放在了一个并行方法RunnableParallel 里，RunnableParallel的输入呢是一个字典，里面有两个键值对，一个是context就是需要检索的内容，这里我们是用刚才我们构造的检索器retriever来获取；一个是question就是提出的问题，提出的问题是用RunnablePassthrough()来占位的，标识这个的输入是从模型的其他组件而来，这里只是占位。
setup_and_retrieval = RunnableParallel(
    {"context": retriever, "question": RunnablePassthrough()}
)
 
chain = setup_and_retrieval | prompt | model | output_parser
 
response = chain.invoke("where did harrison work?")
 
print(response)
```

## 查询链

```python
llm = ChatOpenAI(
    temperature=0,
    openai_api_key = os.getenv("OPENAI_API_KEY"),
    base_url = os.getenv("OPENAI_BASE_URL")
)
 
 
 
prompt = ChatPromptTemplate.from_messages([
    ("system", "你是一个中国历史专家."),
    ("user", "{input}")
])
 
#prompt提示词和大语言模型llm构成了一个最简单的链。
chain = prompt | llm
 
chain.invoke({"input":"中国改革开放的总设计师是谁?"})
```

### 数据库查询链create_sql_query_chain

#### 1. 建立一个本地的MYSQL数据库

```python
#进入到MYSQL里执行就OK了，然后用下面的代码来连接和测试MYSQL数据库
from langchain_community.utilities import SQLDatabase
 
db_user = "root"
db_password = "161212"
db_host = "127.0.0.1"
db_name = "langchain"
 
#注意要安装pymysql这个库
 
db = SQLDatabase.from_uri(f"mysql+pymysql://{db_user}:{db_password}@{db_host}/{db_name}")
 
print(db.dialect)
print(db.get_usable_table_names())
db.run("SELECT * FROM user;")
```

#### 2. 使用create_sql_query_chain来构链

```python
import os
 
from langchain.chains import create_sql_query_chain
from langchain_openai import ChatOpenAI
 
llm = ChatOpenAI(
    temperature=0,
    openai_api_key = os.getenv("OPENAI_API_KEY"),
    base_url = os.getenv("OPENAI_BASE_URL")
)
 
#db就是数据库
chain = create_sql_query_chain(llm, db)
response = chain.invoke({"question": "How many people are there in the user table?"})
response
```

#### 3. 用 get_prompts()方法来查看langchain内置的prompt样式

用chain.get_prompts()方法来查看链的内置prompt模板template是什么样的

```python
chain.get_prompts()[0].pretty_print()

#template的全文：
You are a MySQL expert. Given an input question, first create a syntactically correct MySQL query to run, then look at the results of the query and return the answer to the input question.
Unless the user specifies in the question a specific number of examples to obtain, query for at most {top_k} results using the LIMIT clause as per MySQL. You can order the results to return the most informative data in the database.
Never query for all columns from a table. You must query only the columns that are needed to answer the question. Wrap each column name in backticks (`) to denote them as delimited identifiers.
Pay attention to use only the column names you can see in the tables below. Be careful to not query for columns that do not exist. Also, pay attention to which column is in which table.
Pay attention to use CURDATE() function to get the current date, if the question involves "today".
 
Use the following format:
 
Question: Question here
SQLQuery: SQL Query to run
SQLResult: Result of the SQLQuery
Answer: Final answer here
 
Only use the following tables:
{table_info}
 
Question: {input}
```

#### 4. 构造另外一个链来执行上一个链生成SQL

可以从langchain_community.tools.sql_database.tool引入一个 QuerySQLDataBaseTool 来执行 write_query 这个链生成的SQL语句

```
from langchain_community.tools.sql_database.tool import QuerySQLDataBaseTool
 
execute_query = QuerySQLDataBaseTool(db=db)
write_query = create_sql_query_chain(llm, db)

#有两个链，第一个链chain由write_query和execute_query组成，chain调用invoke方法提出问题，这个问题作为input输入给 write_query，write_query是第二个链，它使用之前构造好的模型 llm 和数据库 db ，生成了正确的SQL语句 ，通过 chain链传递给了 execute_query 进行执行

chain = write_query | execute_query
chain.invoke({"question": "user表里有多少个admin用户?"})
```

#### 5. 构造一个链来回答用户的问题

```python
from operator import itemgetter
 
from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import PromptTemplate
from langchain_core.runnables import RunnablePassthrough
 
answer_prompt = PromptTemplate.from_template(
"""Given the following user question, corresponding SQL query, and SQL result, answer the user question. 用中文回答最终答案
Question: {question}
SQL Query: {query}
SQL Result: {result}
Answer: """
)
 
answer = answer_prompt | llm | StrOutputParser()
```

 这个链的prompt模板很重要，这个模板需要有三个参数输入，一个是用户最初的问题question，一个是write_query生成的SQL语句，一个是execute_query生成的结果，基于这三个参数，answer链才可以回答最后的问题

#### 6. 构造最后一个FINAL链来把上述流程串起来

我们还需要最后一个final链把这些流程串起来，形成最后一个链：

```python
final_chain = (
    RunnablePassthrough.assign(query=write_query).assign(result=itemgetter("query") | execute_query)
    | answer
)
 
final_chain.invoke({"question": "user表里有多少个admin用户?"})
```

RunnablePassthrough方法作用是接收和传递参数。在这里他先接收 write_query链处理的结果赋值给 query（"SELECT COUNT(*) FROM `user` WHERE `role` = 'admin'") , query是一个 键值对{“query”:"SELECT COUNT(*) FROM `user` WHERE `role` = 'admin"}。然后又用itemgetter把query的值赋给 result ，然后把result传给 execute_query去执行，执行的结果（[(4,)]）传给 answer链去解答。

## 互联网查询

将互联网网页的信息进行抓取和查询

### 1. url_to_text方法实现输入URL返回一段str文本

```python
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.runnables import RunnableParallel, RunnablePassthrough
from langchain_core.output_parsers import StrOutputParser

#“Loader_Transformer ” 和 “transformer ”是自己构造的两个类

#构造一个Loader_Transformer 的实例，输入的参数是“www.baidu.com”的首页，然后调用Loader_Transformer 的url_to_text方法，输入是一个url，输出是一段文本

from Loader_Transformer import Loader_Transformer
from transformer import text_to_vectorstore
 
 
 
if __name__ == '__main__':
 
    loader = Loader_Transformer("https://www.baidu.com")
    text = loader.url_to_text()
```

两个类

```python
import re
from langchain_community.document_loaders import AsyncChromiumLoader
from langchain_community.document_transformers import BeautifulSoupTransformer
 
class Loader_Transformer:
    '''该类的url_to_text方法输入一个url，并将该页面的全部中文字符获取下来，拼接后返回一个大的字符串作为函数的输出 '''
 
    def __init__(self, url):
        self.url = url
 
    def display_info(self):
        print(f"URL: {self.url}")
 
 
    def url_to_text(self):
 
 #AsyncChromiumLoader，这是一个异步的浏览器加载器，用于加载网页，然后调用 AsyncChromiumLoader 的load()方法，加载和返回一个网页文件 html
        loader = AsyncChromiumLoader([self.url])
        html = loader.load()
 
        # print(html)
    #文档转换器
#BeautifulSoupTransformer，使用BeautifulSoupTransformer的transform_documents 方法来把这个html文件进行切分，就是把 <span>等标签里面的东西提取出来，得到一个转化后的文档。
        bs_transformer = BeautifulSoupTransformer()
        docs_transformed = bs_transformer.transform_documents(
            html,
            tags_to_extract=["span", "li", "p", "div", "a"]
        )
 
        # print(type(docs_transformed))
        # object_size = sys.getsizeof(docs_transformed)
        # print(f"Object Size: {object_size} bytes")
 
        chinese_text = re.findall("[\u4e00-\u9fa5]+", docs_transformed[0].page_content)
 
        # print(type(chinese_text), chinese_text)
 
        text = ''
# 抽取出来的chinese_text是一个列表，再把列表里的每个元素拼接出来形成一个大的str文本作为函数的结果返回 
        for i in chinese_text:
            text = text + ',' + str(i)
 
        return text
```

### 2. text_to_vect 方法实现str文本向量化并返回一个向量数据库

从transformer中引入text_to_vectorstore这个类，实例化一个text_to_vectorstore类，并调用这个类的text_to_vect 方法，将一段文本转化为一个向量数据库：

```python
vector = text_to_vectorstore(text)
vectorstore = vector.text_to_vect()


#text_to_vectorstore类以及text_to_vect方法的代码
 
import os
 
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import Chroma
from langchain_openai import OpenAIEmbeddings
 
 
 
class text_to_vectorstore:
    ''' 调用text_to_vect()方法，将输入的一段文本存储在向量数据库里，并将该向量数据库作为结果返回'''
 
 
    def __init__(self, text):
        self.text = text
 
    def display_info(self):
        print(f"Input text as : {self.text}")
 
    def text_to_vect(self):
 
        text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=100,
            chunk_overlap=20,
            length_function=len,
        )
 
        splitter_content = text_splitter.split_text(self.text)
 
 
        api_key = os.getenv("OPENAI_API_KEY")
 
        persist_path = r'C:\Users\PycharmProjects\langchain'
 
        vectorstore = Chroma.from_texts(
            splitter_content,
            embedding=OpenAIEmbeddings(
                openai_api_key=api_key,
                base_url="https://wdapi7.61798.cn/v1"),
                persist_directory = persist_path
        )
 
 
        return vectorstore
```

### 3. 设置一个问题，然后可以先做一下向量相似度检索

```python
    query = '韩国什么视频爆红？'
    search_result = vectorstore.similarity_search_with_score(query)
    print(search_result[0])
```

### 4. 也可以构造一个检索器，进行问题的检索

```python
retriever = vectorstore.as_retriever()
 
    template = """Answer the question based only on the following context and use chinese to answer:
    {context}
    Question: {question}
    """
 
    setup_and_retrieval = RunnableParallel(
        {"context": retriever, "question": RunnablePassthrough()}
    )
    prompt = ChatPromptTemplate.from_template(template)
    output_parser = StrOutputParser()
    llm = ChatOpenAI(
        model_name="gpt-3.5-turbo",
        temperature=0.7,
        base_url="https://wdapi7.61798.cn/v1"
    )
 
    chain = setup_and_retrieval | prompt | llm | output_parser
 
    result = chain.invoke(query)
    print(result)
```

### 5.(3)和(4)的区别

第一段

- **仅测试检索效果**：
  1. **相似性搜索**：直接调用`similarity_search_with_score`，返回与问题最相关的文档片段**及其相似度分数**。
  2. **输出**：打印**原始检索结果**（如文档内容+匹配分数），不经过LLM生成。
- **目的**：验证向量数据库的检索质量（如检查是否返回了“韩国流行视频”相关内容）。

第二段

#### **作用**

- **端到端问答系统**：
  1. **检索**：通过`retriever`从`vectorstore`中获取与问题相关的文档片段（`context`）。
  2. **生成回答**：将检索到的上下文和问题一起交给LLM（GPT-3.5），生成符合要求的自然语言回答。
- **输出**：直接返回一个**完整的中文答案**（如“韩国近期爆红的视频是XXX，因为...”）。

- **第一段代码**：像一名“研究员”先查资料（检索），再写报告（生成）。如果需要**直接给用户答案** → 用第一段代码（问答链）。
- **第二段代码**：像一名“图书管理员”只负责找书（检索），不解释内容。如果**检查数据是否被正确索引** → 用第二段代码（纯检索）。

## 记忆模块

利用langchain构造最多的就是聊天机器人，而要进行聊天的业务功能，就必须使得大语言模型能够获得上下文，这样对话才能继续。

 **记忆组件会将处理过的聊天信息数据注入提示词模板中，将模板传给大语言模型来实现记忆功能**。

记忆的功能其实是通过prompt模板来实现的，在prompt中要给memory组件预留好一个位置并告诉模板，这个是记忆内容，并在加载模板的时候把上下文注入进去传给大模型，大模型才能够知道你上文说的是啥。

### 1. 了解和使用一个最简单的记忆组件ConversationBufferMemory

```python
from langchain.memory import ConversationBufferMemory
 
memory = ConversationBufferMemory()
memory.load_memory_variables({})

memory.chat_memory.add_user_message("hi!")
memory.chat_memory.add_ai_message("what's up?")
memory.load_memory_variables({})
```

![img](https://i-blog.csdnimg.cn/blog_migrate/0bc49839682e870f4a2f9b2c0ae9a336.png)

![img](https://i-blog.csdnimg.cn/blog_migrate/dd5223d9312e4e6e9063377fde5a5ffc.png)

### 2. 在LLMChain里加入memory功能

```python
import os
 
from langchain_openai import OpenAI
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain
from langchain.memory import ConversationBufferMemory
 
 
llm = OpenAI(
    temperature=0,
    openai_api_key = os.getenv("OPENAI_API_KEY"),
    base_url = os.getenv("OPENAI_BASE_URL")
)
 
# 注意，在模板里，要给聊天记录留下位置，聊天记录是通过注入给模板来实现的
 
template = """You are a nice chatbot having a conversation with a human.
Previous conversation:
{chat_history}
New human question: {question}
Response:"""
 
 
prompt = PromptTemplate.from_template(template)
 
# `memory_key`要和模板里的参数保持一致
 
memory = ConversationBufferMemory(memory_key="chat_history")
conversation = LLMChain(
    llm=llm,
    prompt=prompt,
    verbose=True, 
    memory=memory
)
 
conversation.invoke({"question":"hi"})
conversation.invoke({"question":"你最喜欢什么动物?"})
conversation.invoke({"question":"那么它最爱吃什么?"})
 
memory.load_memory_variables({})
```

  注意几个要点：

1. 模板里要预留好memory参数的位置，且要和构造memory时，设置的memory_key保持一致，如果不一致，会报错。

   ​     ![img](https://i-blog.csdnimg.cn/blog_migrate/c68b0f8c226bffccaad506c3b1442f14.png)

2. 在LLMChain的设置里，可以选择verbose=True，会把创建链的一些中间过程给体现出来，方便理解业务，我们来看代码的运行结果：

![img](https://i-blog.csdnimg.cn/blog_migrate/7607b0d3aca9dd017d762c4711f26e72.png)

![img](https://i-blog.csdnimg.cn/blog_migrate/e5da6d54f47c99fd4aab358d24b74409.png)

通过运行结果可以看到每次每次prompt模板加载的内容都有不同，就是每次在调用的时候memory的数据注入进了模板导致的。

### 3. 在LCEL语法中加入memory功能

```python
import langchain
import os
 
from operator import itemgetter
 
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate,MessagesPlaceholder
from langchain_core.runnables import RunnableLambda, RunnablePassthrough
from langchain.memory import ConversationBufferMemory
 
 
 
llm = ChatOpenAI(
    temperature=0,
    openai_api_key = os.getenv("OPENAI_API_KEY"),
    base_url = os.getenv("OPENAI_BASE_URL")
)

#用的是ChatOpenAI聊天模型包装器，所以return_messages 要选择True，否则memory的输出会是str
memory = ConversationBufferMemory(return_messages=True)
 
prompt = ChatPromptTemplate.from_messages([
    ("system", "你是一个中国历史专家."),

#在构造提示词的时候，需要用这个占位符来实现memory的插入，注意这里的variable_name是memory默认的history
MessagesPlaceholder(variable_name="history"),
    ("user", "{input}")
])
 
memory.load_memory_variables({})

#RunnablePassthrough把用户输入的input传入，同时使用RunnableLambda方法将memory做加载（第一轮是空的，不过没关系，第二次调用就有数据里），RunnableLambda把memory加载获得数据（字典）赋值给history，构造了一个字典{“history”:"{}"}，然后传给itemgetter，itemgetter把键值对里的值取出来然后再传给 prompt
chain = (
    RunnablePassthrough.assign(
        history=RunnableLambda(memory.load_memory_variables) | itemgetter("history")
    )
    | prompt
    | llm
)
 
 
input = {"input":"中国改革开放的总设计师是谁?"}
response = chain.invoke(input)
print(response)
 
memory.save_context(input, {"output": response.content})
memory.load_memory_variables({})
```

## 回调模块

 回调（callback）是一个python中常用的技术，在langchain中回调作为一个主要模块主要应用在运行过程管理、查看日志、计算token等场景

### 1. 一个最简单的同步回调

我们从 langchain.[callbacks](https://so.csdn.net/so/search?q=callbacks&spm=1001.2101.3001.7020)库中引入 StdOutCallbackHandler类，并进行实例化

```python
import os
from langchain.callbacks import StdOutCallbackHandler
from langchain.chains import LLMChain
from langchain_openai import OpenAI
from langchain.prompts import PromptTemplate

# 初始化回调函数（打印模型调用细节到控制台）
handler = StdOutCallbackHandler()

# 配置OpenAI模型
llm = OpenAI(
    temperature=0,  # 完全确定性输出
    openai_api_key=os.getenv("OPENAI_API_KEY"),  # 从环境变量读取API密钥
    base_url=os.getenv("OPENAI_BASE_URL"),  # 自定义API地址（如代理或本地部署）
)

# 定义Prompt模板，{year}为占位符
prompt = PromptTemplate.from_template("{year}年是中国生肖年的哪年？")

# 创建LLM调用链，绑定模型、Prompt和回调函数
chain_callback = LLMChain(
    llm=llm,
    prompt=prompt,
    callbacks=[handler]  # 注册回调函数
)

# 执行调用链，传入年份参数
chain_callback.invoke({"year": 1987})
```

在构造链的时候我们加入回调 callbacks = [handler]，然后在运行链的过程中会看到回调的效果，就是在启动、结束链的时候可以看到相关的输出，这咋复杂链的构造中可以有效地帮助你定位问题

### 2. 使用FileCallbackHandler进行日志输出

实际项目运行中，不太可能去console去盯着看StdOutCallbackHandler的输出，我们往往使用loggin来记录、保存和回顾代码运行情况。

        注意我们要先pip安装 loguru 这个库，然后我们先声明一个logfile并添加进logger里，我们实例化一个 FileCallbackHandler并将logfile作为参数输入进去：
```python
from loguru import logger
from langchain.callbacks import FileCallbackHandler
 
logfile = 'output.log'
logger.add(logfile,colorize=True,enqueue = True)
handler = FileCallbackHandler(logfile)
 
chain =  LLMChain(
    llm=llm,
    prompt=prompt,
    callbacks = [handler],
    verbose = True
)

#通过logger.info()来查看log信息，我们同样可以在代码当前目录下面找到 output.log文件
response = chain.invoke({"year":1988})
logger.info(response)

with open('output.log','r',encoding='utf-8') as file:
    content = file.read()
    
print(content)
```

###  3. 使用 get_openai_callback 回调实现token计数

使用如下代码可以实现token的计数，这个在生产场景很有用，因为在线调用各种模型都是要付费的：

```
from langchain.callbacks import get_openai_callback
 
 
llm = OpenAI(
    temperature=0,
    openai_api_key = os.getenv("OPENAI_API_KEY"),
    base_url = os.getenv("OPENAI_BASE_URL"),
)
 
with get_openai_callback() as cb:
    llm.invoke("牛顿什么时候发现的万有引力定律？")
 
total_tokens = cb.total_tokens
assert total_tokens >0
 
print(total_tokens)
```

 我们也可以把整个链的调用放到 with as: 语句下面

```python
with get_openai_callback() as cb:
    logfile = 'output.log'
    logger.add(logfile,colorize=True,enqueue = True)
    handler = FileCallbackHandler(logfile)
 
    chain =  LLMChain(
    llm=llm,
    prompt=prompt,
    callbacks = [handler],
    verbose = True
    )
 
    response = chain.invoke({"year":1988})
    logger.info(response)
 
total_tokens = cb.total_tokens
assert total_tokens >0
 
print(total_tokens)    
```

    版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。

原文链接：https://blog.csdn.net/u013607702/article/details/135946335