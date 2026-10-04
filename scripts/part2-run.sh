#!/usr/bin/env bash
# 演示第二部分 10 个 HDFS 功能（Java API），输出写入 /tmp/lab1-out/part2.log
set -u
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export HADOOP_HOME="${HADOOP_HOME:-/home/hadoop/hadoop-3.3.6}"
export PATH="$PATH:$HADOOP_HOME/bin"
export HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/etc/hadoop}"

LOG=/tmp/lab1-out/part2.log
mkdir -p /tmp/lab1-out
exec > >(tee -a "$LOG") 2>&1

CP="$(hadoop classpath):$ROOT/part2/build"
run() { java -classpath "$CP" HDFSAPI "$@"; }
BASE=/user/hadoop/lab1-part2

set -x
hdfs dfs -rm -r -f "$BASE"
hdfs dfs -mkdir -p "$BASE"

echo "### 1) 上传（目标不存在直接上传；已存在时追加/覆盖）"
printf 'line1\nline2\n' > /tmp/lab1-a.txt
run upload /tmp/lab1-a.txt "$BASE/f.txt" overwrite
printf 'line3\n' > /tmp/lab1-b.txt
run upload /tmp/lab1-b.txt "$BASE/f.txt" append
run cat "$BASE/f.txt"

echo "### 8) 向文件末尾/开头追加内容"
run append "$BASE/f.txt" "[TAIL]" tail
run append "$BASE/f.txt" "[HEAD]" head
run cat "$BASE/f.txt"

echo "### 4) 查看文件信息"
run info "$BASE/f.txt"

echo "### 5) 递归列出目录"
run lsr "$BASE"

echo "### 2) 下载（第二次触发自动重命名）"
rm -f /tmp/lab1-dl.txt /tmp/lab1-dl.txt.*
run download "$BASE/f.txt" /tmp/lab1-dl.txt
run download "$BASE/f.txt" /tmp/lab1-dl.txt
ls -l /tmp/lab1-dl.txt*

echo "### 6) 文件创建/删除（父目录自动创建）"
run touch "$BASE/d1/d2/new.txt" create
run touch "$BASE/d1/d2/new.txt" delete

echo "### 7) 目录创建/删除（空才删；非空不删）"
run mkdir "$BASE/e1/e2/e3" create
run mkdir "$BASE/e1/e2/e3" delete
run mkdir "$BASE/e1" create
run touch "$BASE/e1/file.txt" create
run mkdir "$BASE/e1" delete

echo "### 10) 移动文件"
run mv "$BASE/e1/file.txt" "$BASE/moved.txt"

echo "### 9) 删除文件"
run rm "$BASE/moved.txt"

echo "### 最终目录"
run lsr "$BASE"
echo "第二部分演示结束"
