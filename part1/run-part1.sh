#!/usr/bin/env bash
# 实验一 第一部分：熟悉常用的 Linux 操作和 Hadoop 操作（21 项）
# 用法（推荐交互执行，便于 sudo 提示）：
#   sudo usermod -aG sudo hadoop      # 一次性
#   sudo -iu hadoop
#   bash part1/run-part1.sh
# 输出同时写入 /tmp/lab1-out/part1.log
set -u
LOG=/tmp/lab1-out/part1.log
mkdir -p /tmp/lab1-out
# 同时输出到终端与日志
exec > >(tee -a "$LOG") 2>&1
set -x

echo "===== 实验一 第一部分 开始 $(date) ====="
echo "当前用户: $(whoami)  主目录: $HOME"

echo "### 1) cd"
cd /usr/local && pwd
cd .. && pwd
cd ~ && pwd

echo "### 2) ls /usr"
ls /usr

echo "### 3) mkdir"
cd /tmp && mkdir -p a && ls /tmp
mkdir -p /tmp/a1/a2/a3/a4 && ls -R /tmp/a1

echo "### 4) rmdir"
rmdir /tmp/a
rmdir -p /tmp/a1/a2/a3/a4
ls /tmp

echo "### 5) cp"
sudo cp ~/.bashrc /usr/bashrc1
mkdir -p /tmp/test && sudo cp -r /tmp/test /usr/

echo "### 6) mv"
sudo mv /usr/bashrc1 /usr/test/
sudo mv /usr/test /usr/test2

echo "### 7) rm"
sudo rm -f /usr/test2/bashrc1
sudo rm -r /usr/test2

echo "### 8) cat"
cat ~/.bashrc

echo "### 9) tac"
tac ~/.bashrc

echo "### 10) more"
more ~/.bashrc < /dev/null || true

echo "### 11) head"
head -n 20 ~/.bashrc
head -n -50 ~/.bashrc

echo "### 12) tail"
tail -n 20 ~/.bashrc
tail -n +50 ~/.bashrc

echo "### 13) touch"
touch /tmp/hello && ls -l /tmp/hello
touch -d "5 days ago" /tmp/hello && ls -l /tmp/hello

echo "### 14) chown"
sudo chown root /tmp/hello && ls -l /tmp/hello

echo "### 15) find"
find ~ -name .bashrc

echo "### 16) tar"
sudo mkdir -p /test
sudo tar -czf /test.tar.gz -C / test
sudo tar -xzf /test.tar.gz -C /tmp
ls -l /tmp/test

echo "### 17) grep"
grep 'examples' ~/.bashrc || echo "(未找到 examples 字符串)"

echo "### 18) 启动 Hadoop 并创建 HDFS 用户目录"
if ! jps | grep -q NameNode; then start-dfs.sh; fi
if ! jps | grep -q ResourceManager; then start-yarn.sh; fi
jps
hdfs dfs -mkdir -p /user/hadoop
hdfs dfs -ls /user

echo "### 19) 创建 /user/hadoop/test"
hdfs dfs -mkdir -p /user/hadoop/test
hdfs dfs -ls /user/hadoop

echo "### 20) 上传 ~/.bashrc 到 HDFS test"
hdfs dfs -put -f ~/.bashrc /user/hadoop/test/
hdfs dfs -ls /user/hadoop/test

echo "### 21) 下载 test 到本地 /usr/local/hadoop"
sudo mkdir -p /usr/local/hadoop && sudo chown "$(whoami)" /usr/local/hadoop
hdfs dfs -get -f /user/hadoop/test /usr/local/hadoop
ls -R /usr/local/hadoop

echo "===== 实验一 第一部分 结束 $(date) ====="
