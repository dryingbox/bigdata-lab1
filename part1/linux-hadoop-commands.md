# 第一部分：熟悉常用的 Linux 操作和 Hadoop 操作

> 建议在 `hadoop` 登录 shell 中执行：`sudo usermod -aG sudo hadoop`（一次性）→ `sudo -iu hadoop`
> 下列命令中标注 `[sudo]` 的需要提权（写到 `/usr`、`/`、`chown root` 等系统位置）。

## 1) cd 命令：切换目录
```bash
cd /usr/local          # (1) 切换到 /usr/local
cd ..                  # (2) 切换到上一级目录
cd ~                   # (3) 切换到当前用户主文件夹
```

## 2) ls 命令：查看文件与目录
```bash
ls /usr                # 查看 /usr 下的所有文件和目录
```

## 3) mkdir 命令：新建目录
```bash
cd /tmp && mkdir a && ls /tmp          # (1) 创建 /tmp/a 并查看
cd /tmp && mkdir -p a1/a2/a3/a4        # (2) 创建 /tmp/a1/a2/a3/a4
```

## 4) rmdir 命令：删除空目录
```bash
rmdir /tmp/a                           # (1) 删除 /tmp/a
rmdir -p /tmp/a1/a2/a3/a4              # (2) 删除 /tmp/a1/a2/a3/a4（连同空的上级）
ls /tmp
```

## 5) cp 命令：复制文件或目录
```bash
sudo cp ~/.bashrc /usr/bashrc1         # (1)[sudo] 复制并重命名为 /usr/bashrc1
mkdir /tmp/test && sudo cp -r /tmp/test /usr/   # (2)[sudo] 复制 test 到 /usr
```

## 6) mv 命令：移动文件与目录，或更名
```bash
sudo mv /usr/bashrc1 /usr/test/        # (1)[sudo] 移动 bashrc1 到 /usr/test
sudo mv /usr/test /usr/test2           # (2)[sudo] test 重命名为 test2
```

## 7) rm 命令：移除文件或目录
```bash
sudo rm /usr/test2/bashrc1             # (1)[sudo] 删除 bashrc1
sudo rm -r /usr/test2                  # (2)[sudo] 删除 test2 目录
```

## 8) cat 命令：查看文件内容
```bash
cat ~/.bashrc
```

## 9) tac 命令：反向查看文件内容
```bash
tac ~/.bashrc
```

## 10) more 命令：一页一页翻动查看
```bash
more ~/.bashrc
```

## 11) head 命令：取出前面几行
```bash
head -n 20 ~/.bashrc                   # (1) 前 20 行
head -n -50 ~/.bashrc                  # (2) 不显示后 50 行（即显示前面部分）
```

## 12) tail 命令：取出后面几行
```bash
tail -n 20 ~/.bashrc                   # (1) 最后 20 行
tail -n +50 ~/.bashrc                  # (2) 从第 50 行开始到最后
```

## 13) touch 命令：修改文件时间或创建新文件
```bash
touch /tmp/hello && ls -l /tmp/hello                 # (1) 创建空文件并查看时间
touch -d "5 days ago" /tmp/hello && ls -l /tmp/hello # (2) 时间改为 5 天前
```

## 14) chown 命令：修改文件所有者
```bash
sudo chown root /tmp/hello && ls -l /tmp/hello       # [sudo] 所有者改为 root
```

## 15) find 命令：文件查找
```bash
find ~ -name .bashrc                   # 在主文件夹下查找 .bashrc
```

## 16) tar 命令：压缩
```bash
sudo mkdir /test                                   # (1)[sudo] 根目录下新建 test
sudo tar -czf /test.tar.gz -C / test               # (1)[sudo] 打包为 /test.tar.gz
sudo tar -xzf /test.tar.gz -C /tmp                 # (2)[sudo] 解压到 /tmp
ls -l /tmp/test
```

## 17) grep 命令：查找字符串
```bash
grep 'examples' ~/.bashrc
```

## 18) hadoop 用户登录并启动 Hadoop，创建 HDFS 用户目录
```bash
start-dfs.sh ; start-yarn.sh ; jps     # 若未启动则启动
hdfs dfs -mkdir -p /user/hadoop        # 创建 /user/hadoop
hdfs dfs -ls /user
```

## 19) 在 HDFS 的 /user/hadoop 下创建 test 并查看
```bash
hdfs dfs -mkdir /user/hadoop/test
hdfs dfs -ls /user/hadoop
```

## 20) 上传本地 ~/.bashrc 到 HDFS 的 test 文件夹
```bash
hdfs dfs -put ~/.bashrc /user/hadoop/test/
hdfs dfs -ls /user/hadoop/test
```

## 21) 将 HDFS 的 test 目录复制到本地 /usr/local/hadoop
```bash
sudo mkdir -p /usr/local/hadoop && sudo chown $(whoami) /usr/local/hadoop
hdfs dfs -get /user/hadoop/test /usr/local/hadoop
ls -R /usr/local/hadoop
```
