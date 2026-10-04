#!/usr/bin/env bash
# 编译第二部分 HDFS Java API
set -e
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
export HADOOP_HOME="${HADOOP_HOME:-/home/hadoop/hadoop-3.3.6}"
export PATH="$PATH:$HADOOP_HOME/bin"
export HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/etc/hadoop}"

mkdir -p "$ROOT/part2/build"
javac -classpath "$(hadoop classpath)" -d "$ROOT/part2/build" "$ROOT/part2/HDFSAPI.java"
echo "编译完成: $ROOT/part2/build/HDFSAPI.class"
