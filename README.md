# 大数据管理与分析 实验一：大数据系统基本实验

本仓库为《大数据管理与分析》课程 **实验一** 的实验代码与脚本，运行于 **Hadoop 3.3.6 伪分布式** 环境。

## 实验内容

| 部分 | 内容 | 目录 |
|---|---|---|
| 第一部分 | 熟悉常用的 Linux 操作和 Hadoop 操作（21 项） | —（纯命令操作，见实验报告） |
| 第二部分 | 熟悉常用的 HDFS 操作（Java API + Shell，10 项） | `part2/` |
| 第三部分 | MapReduce 初级编程（合并去重、排序、祖孙关系挖掘） | `part3/` |

## 实验环境

- 操作系统：Ubuntu 22.04 LTS
- JDK：OpenJDK 1.8.0
- 大数据平台：Apache Hadoop 3.3.6（伪分布式，单节点）

## 目录结构

```
lab1/
├── part2/                  # HDFS 操作
│   ├── HDFSAPI.java        # 10 个功能的 Java API 实现
│   └── shell.md            # 10 个功能对应的 Shell 命令
├── part3/                  # MapReduce 初级编程
│   ├── Dedup.java          # 1) 文件合并去重
│   ├── Sort.java           # 2) 整数排序
│   ├── GrandParent.java    # 3) 祖孙关系挖掘
│   └── data/               # 输入样例数据
└── scripts/                # 构建与运行脚本
```

## 构建与运行

各部分详见对应目录下的说明；简要如下（需在 Hadoop 客户端可用的 `hadoop` 用户下执行）：

```bash
# 第二部分
cd part2
javac -classpath `hadoop classpath` -d build HDFSAPI.java
java  -classpath `hadoop classpath`:build HDFSAPI list /

# 第三部分
cd part3
javac -classpath `hadoop classpath` -d build *.java
jar cf lab1-part3.jar -C build .
hadoop jar lab1-part3.jar Dedup /lab1/dedup /lab1/out/dedup
```

## 实验报告

代码仓库（公开）：**https://github.com/dryingbox/bigdata-lab1**

实验报告提交至课程邮箱，报告中已附本仓库链接。
