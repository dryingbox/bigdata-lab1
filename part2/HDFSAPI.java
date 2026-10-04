import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 实验一 第二部分：HDFS 常用操作（Java API 实现，共 10 个功能）
 *
 * 用法：
 *   javac -classpath `hadoop classpath` -d build HDFSAPI.java
 *   java  -classpath `hadoop classpath`:build HDFSAPI <命令> [参数...]
 *
 * 命令一览：
 *   upload  <local> <hdfs> [append|overwrite]   1) 上传；目标存在时按参数追加/覆盖（缺省交互询问）
 *   download <hdfs> <local>                     2) 下载；本地同名自动重命名
 *   cat     <hdfs>                              3) 输出文件内容到终端
 *   info    <hdfs>                              4) 显示文件权限/大小/时间/路径
 *   lsr     <dir>                               5) 递归列出目录下所有文件信息
 *   touch   <path> [create|delete]              6) 文件创建/删除（目录不存在自动创建）
 *   mkdir   <path> [create|delete]              7) 目录创建/删除（空才删）
 *   append  <path> <content> <head|tail>        8) 向文件开头/末尾追加内容
 *   rm      <path>                              9) 删除文件
 *   mv      <src> <dst>                         10) 移动文件
 *   list    <dir>                               附加：列出目录
 */
public class HDFSAPI {

    private static Configuration conf = new Configuration();
    private static FileSystem fs;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        fs = FileSystem.get(conf);
        if (args.length == 0) { usage(); fs.close(); return; }
        String cmd = args[0].toLowerCase();
        try {
            switch (cmd) {
                case "upload":
                    upload(args[1], args[2], args.length > 3 ? args[3] : "ask");
                    break;
                case "download":
                    download(args[1], args[2]);
                    break;
                case "cat":
                    cat(args[1]);
                    break;
                case "info":
                    info(args[1]);
                    break;
                case "lsr":
                    listRecursive(args[1]);
                    break;
                case "touch":
                    createOrDeleteFile(args[1], args.length > 2 ? args[2] : "create");
                    break;
                case "mkdir":
                    createOrDeleteDir(args[1], args.length > 2 ? args[2] : "create");
                    break;
                case "append":
                    appendContent(args[1], args[2], args[3]);
                    break;
                case "rm":
                    delete(args[1]);
                    break;
                case "mv":
                    move(args[1], args[2]);
                    break;
                case "list":
                    list(args[1]);
                    break;
                default:
                    usage();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("参数不足。");
            usage();
        } finally {
            fs.close();
        }
    }

    /** 1) 上传文件；若目标已存在，按 mode=append/overwrite 处理，mode=ask 时交互询问 */
    private static void upload(String local, String dst, String mode) throws IOException {
        Path dstPath = new Path(dst);
        if (!fs.exists(dstPath)) {
            fs.copyFromLocalFile(false, true, new Path(local), dstPath);
            System.out.println("已上传: " + local + " -> " + dst);
            return;
        }
        String m = mode;
        if ("ask".equalsIgnoreCase(m)) {
            System.out.print("HDFS 目标 " + dst + " 已存在，追加(a)还是覆盖(o)？[a/o]: ");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line = br.readLine();
            m = (line != null && line.trim().toLowerCase().startsWith("a")) ? "append" : "overwrite";
        }
        if ("append".equalsIgnoreCase(m)) {
            writeAppend(dstPath, readLocal(local));
            System.out.println("已追加到原有文件末尾: " + local + " -> " + dst);
        } else {
            fs.copyFromLocalFile(false, true, new Path(local), dstPath);
            System.out.println("已覆盖原有文件: " + local + " -> " + dst);
        }
    }

    /** 2) 下载文件；本地同名时自动重命名为 name.1, name.2 ... */
    private static void download(String src, String local) throws IOException {
        String target = local;
        if (new File(local).exists()) {
            int i = 1;
            do { target = local + "." + i; i++; } while (new File(target).exists());
            System.out.println("本地已存在同名文件，自动重命名为: " + target);
        }
        fs.copyToLocalFile(false, new Path(src), new Path(target), true);
        System.out.println("已下载: " + src + " -> " + target);
    }

    /** 3) 输出文件内容到终端 */
    private static void cat(String src) throws IOException {
        try (FSDataInputStream in = fs.open(new Path(src));
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        }
    }

    /** 4) 显示文件的权限、大小、创建/修改时间、路径 */
    private static void info(String src) throws IOException {
        FileStatus st = fs.getFileStatus(new Path(src));
        System.out.println("路径:     " + st.getPath());
        System.out.println("权限:     " + st.getPermission());
        System.out.println("大小:     " + st.getLen() + " 字节");
        System.out.println("修改时间: " + SDF.format(new Date(st.getModificationTime())));
        System.out.println("所有者:   " + st.getOwner() + "  组: " + st.getGroup());
        System.out.println("副本数:   " + st.getReplication());
        System.out.println("类型:     " + (st.isDirectory() ? "目录" : "文件"));
    }

    /** 5) 递归输出目录下所有文件的信息 */
    private static void listRecursive(String dir) throws IOException {
        RemoteIterator<LocatedFileStatus> it = fs.listFiles(new Path(dir), true);
        while (it.hasNext()) {
            LocatedFileStatus st = it.next();
            System.out.println(st.getPermission() + "\t" + st.getLen() + "\t"
                    + SDF.format(new Date(st.getModificationTime())) + "\t" + st.getPath());
        }
    }

    /** 6) 文件创建/删除；创建时若父目录不存在则自动创建 */
    private static void createOrDeleteFile(String path, String op) throws IOException {
        Path p = new Path(path);
        if ("delete".equalsIgnoreCase(op)) {
            if (fs.exists(p)) {
                fs.delete(p, false);
                System.out.println("已删除文件: " + path);
            } else {
                System.out.println("文件不存在: " + path);
            }
        } else {
            Path parent = p.getParent();
            if (parent != null && !fs.exists(parent)) {
                fs.mkdirs(parent);
                System.out.println("父目录不存在，已自动创建: " + parent);
            }
            if (!fs.exists(p)) {
                fs.create(p, true).close();
                System.out.println("已创建文件: " + path);
            } else {
                System.out.println("文件已存在: " + path);
            }
        }
    }

    /** 7) 目录创建/删除；创建时自动建父目录，删除时仅当目录为空才删除 */
    private static void createOrDeleteDir(String path, String op) throws IOException {
        Path p = new Path(path);
        if ("delete".equalsIgnoreCase(op)) {
            if (!fs.exists(p)) {
                System.out.println("目录不存在: " + path);
            } else if (fs.listStatus(p).length == 0) {
                fs.delete(p, false);
                System.out.println("目录为空，已删除: " + path);
            } else {
                System.out.println("目录非空，不删除: " + path);
            }
        } else {
            if (fs.mkdirs(p)) {
                System.out.println("已创建目录（含缺失的父目录）: " + path);
            } else {
                System.out.println("目录已存在: " + path);
            }
        }
    }

    /** 8) 向文件的开头(head)或末尾(tail)追加内容 */
    private static void appendContent(String path, String content, String pos) throws IOException {
        Path p = new Path(path);
        if ("tail".equalsIgnoreCase(pos)) {
            writeAppend(p, content.getBytes("UTF-8"));
            System.out.println("已追加到文件末尾: " + path);
        } else {
            byte[] old = fs.exists(p) ? readAll(p) : new byte[0];
            Path tmp = new Path(path + ".tmp." + System.currentTimeMillis());
            try (FSDataOutputStream out = fs.create(tmp, true)) {
                out.write(content.getBytes("UTF-8"));
                out.write(old);
            }
            if (fs.exists(p)) fs.delete(p, false);
            fs.rename(tmp, p);
            System.out.println("已插入到文件开头: " + path);
        }
    }

    /** 9) 删除文件 */
    private static void delete(String path) throws IOException {
        Path p = new Path(path);
        if (!fs.exists(p)) {
            System.out.println("文件不存在: " + path);
            return;
        }
        boolean ok = fs.delete(p, true);
        System.out.println((ok ? "已删除: " : "删除失败: ") + path);
    }

    /** 10) 移动文件（重命名） */
    private static void move(String src, String dst) throws IOException {
        boolean ok = fs.rename(new Path(src), new Path(dst));
        System.out.println((ok ? "已移动: " : "移动失败: ") + src + " -> " + dst);
    }

    /** 附加：列出目录 */
    private static void list(String dir) throws IOException {
        FileStatus[] arr = fs.listStatus(new Path(dir));
        for (FileStatus st : arr) {
            System.out.println(st.getPermission() + "\t" + st.getLen() + "\t"
                    + SDF.format(new Date(st.getModificationTime())) + "\t" + st.getPath());
        }
    }

    /** 读取 HDFS 文件全部内容 */
    private static byte[] readAll(Path p) throws IOException {
        try (FSDataInputStream in = fs.open(p);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) bos.write(buf, 0, n);
            return bos.toByteArray();
        }
    }

    /** 读取本地文件全部内容 */
    private static byte[] readLocal(String local) throws IOException {
        try (InputStream in = new FileInputStream(local);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) > 0) bos.write(buf, 0, n);
            return bos.toByteArray();
        }
    }

    /** 优先使用 append；若文件系统不支持（如本地 ChecksumFileSystem），退回“读-改-写” */
    private static void writeAppend(Path p, byte[] data) throws IOException {
        try {
            try (FSDataOutputStream out = fs.append(p)) {
                out.write(data);
            }
        } catch (UnsupportedOperationException e) {
            byte[] old = fs.exists(p) ? readAll(p) : new byte[0];
            try (FSDataOutputStream out = fs.create(p, true)) {
                out.write(old);
                out.write(data);
            }
        }
    }

    private static void usage() {
        System.out.println("用法: HDFSAPI <命令> [参数...]");
        System.out.println("  upload  <local> <hdfs> [append|overwrite]");
        System.out.println("  download <hdfs> <local>");
        System.out.println("  cat     <hdfs>");
        System.out.println("  info    <hdfs>");
        System.out.println("  lsr     <dir>");
        System.out.println("  touch   <path> [create|delete]");
        System.out.println("  mkdir   <path> [create|delete]");
        System.out.println("  append  <path> <content> <head|tail>");
        System.out.println("  rm      <path>");
        System.out.println("  mv      <src> <dst>");
        System.out.println("  list    <dir>");
    }
}
