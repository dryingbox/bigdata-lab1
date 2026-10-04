# 第二部分：HDFS 操作的 Shell 命令对照

> 与 `HDFSAPI.java` 的 10 个功能一一对应。在 `hadoop` 用户下执行。
> 示例中的路径可按需替换。

## 1) 上传文件；目标已存在时“追加”或“覆盖”
```bash
# 目标不存在：直接上传
hdfs dfs -put local.txt /user/hadoop/test/
# 目标已存在 —— 覆盖
hdfs dfs -put -f local.txt /user/hadoop/test/
# 目标已存在 —— 追加到末尾
hdfs dfs -appendToFile local.txt /user/hadoop/test/local.txt
```

## 2) 下载文件；本地同名自动重命名
```bash
if [ -e local.txt ]; then
  i=1; while [ -e "local.txt.$i" ]; do i=$((i+1)); done
  hdfs dfs -get /user/hadoop/test/local.txt "local.txt.$i"
else
  hdfs dfs -get /user/hadoop/test/local.txt ./local.txt
fi
```

## 3) 将 HDFS 文件内容输出到终端
```bash
hdfs dfs -cat /user/hadoop/test/local.txt
```

## 4) 显示文件的权限、大小、创建时间、路径
```bash
hdfs dfs -ls /user/hadoop/test/local.txt
hdfs dfs -stat "权限=%a 大小=%b 修改=%y 路径=%n" /user/hadoop/test/local.txt
```

## 5) 递归列出目录下所有文件的信息
```bash
hdfs dfs -ls -R /user/hadoop
```

## 6) 文件创建 / 删除（目录不存在自动创建）
```bash
hdfs dfs -touchz /user/hadoop/newdir/file.txt    # 若 newdir 不存在，先 -mkdir -p
hdfs dfs -mkdir -p /user/hadoop/newdir
hdfs dfs -touchz /user/hadoop/newdir/file.txt
hdfs dfs -rm /user/hadoop/newdir/file.txt
```

## 7) 目录创建 / 删除（空才删）
```bash
hdfs dfs -mkdir -p /user/hadoop/a/b/c            # 自动创建缺失的父目录
hdfs dfs -rmdir /user/hadoop/a/b/c               # rmdir：目录为空才删除
hdfs dfs -rm -r  /user/hadoop/a                  # 递归删除（非空时用）
```

## 8) 向文件的开头 / 末尾追加内容
```bash
# 末尾追加（HDFS 原生支持）
hdfs dfs -appendToFile add.txt /user/hadoop/test/local.txt

# 开头插入（HDFS 不支持任意位置写，需“读-改-写-改名”）
hdfs dfs -get /user/hadoop/test/local.txt orig.txt
cat add.txt orig.txt > merged.txt
hdfs dfs -rm /user/hadoop/test/local.txt
hdfs dfs -put merged.txt /user/hadoop/test/local.txt
```

## 9) 删除 HDFS 中指定的文件
```bash
hdfs dfs -rm /user/hadoop/test/local.txt
```

## 10) 移动文件（源路径 → 目的路径）
```bash
hdfs dfs -mv /user/hadoop/test/local.txt /user/hadoop/moved.txt
```
