import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 实验一 第三部分 1)：文件的合并和去重
 * 输入：多个文件（每行一条记录）；输出：合并后去除重复、并按记录排序的结果。
 * 思路：以整行内容为 key，MapReduce 会按 key 排序，Reducer 对每个 key 只输出一次，天然完成“合并 + 去重 + 排序”。
 */
public class Dedup {

    public static class DedupMapper extends Mapper<Object, Text, Text, NullWritable> {
        private final Text outKey = new Text();

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString().trim();
            if (line.isEmpty()) return;
            // 归一化空白，避免仅因空格/制表符不同被当作不同记录
            line = line.replaceAll("\\s+", " ");
            outKey.set(line);
            context.write(outKey, NullWritable.get());
        }
    }

    public static class DedupReducer extends Reducer<Text, NullWritable, Text, NullWritable> {
        @Override
        protected void reduce(Text key, Iterable<NullWritable> values, Context context)
                throws IOException, InterruptedException {
            context.write(key, NullWritable.get());   // 每个 key 仅输出一次
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "merge-and-dedup");
        job.setJarByClass(Dedup.class);
        job.setMapperClass(DedupMapper.class);
        job.setReducerClass(DedupReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
