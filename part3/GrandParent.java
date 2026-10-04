import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 实验一 第三部分 3)：从 child-parent 表格挖掘祖孙关系（grandchild-grandparent）
 * 思路（单作业 join）：
 *   Map：对每条 <child, parent> 输出两条带标记的记录
 *         key=child , value="P"+parent   （child 的父母）
 *         key=parent, value="C"+child    （parent 的子女）
 *   Reduce：对某个 key=k，收集 k 的父母集合 parents 与 k 的子女集合 children，
 *           则 children 中每个 c 的祖辈是 parents 中每个 p，输出 (c, p)。
 */
public class GrandParent {

    public static class GPMapper extends Mapper<Object, Text, Text, Text> {
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] arr = value.toString().trim().split("\\s+");
            if (arr.length < 2) return;
            String child = arr[0];
            String parent = arr[1];
            if ("child".equalsIgnoreCase(child)) return;   // 跳过表头
            context.write(new Text(child), new Text("P" + parent));
            context.write(new Text(parent), new Text("C" + child));
        }
    }

    public static class GPReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            List<String> parents = new ArrayList<>();
            List<String> children = new ArrayList<>();
            for (Text v : values) {
                String s = v.toString();
                if (s.startsWith("P")) parents.add(s.substring(1));
                else if (s.startsWith("C")) children.add(s.substring(1));
            }
            for (String c : children)
                for (String p : parents)
                    context.write(new Text(c), new Text(p));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "grandparent-mining");
        job.setJarByClass(GrandParent.class);
        job.setMapperClass(GPMapper.class);
        job.setReducerClass(GPReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
