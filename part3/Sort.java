import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 实验一 第三部分 2)：对输入文件的整数进行升序排序
 * 输出格式：每行两个整数 —— “位次  原整数”。
 * 思路：Map 输出 (整数, null)，框架按 key 升序排序；使用 1 个 Reducer 保证全局有序，
 *       Reducer 内用计数器依次编号输出 (位次, 整数)。
 */
public class Sort {

    public static class SortMapper extends Mapper<Object, Text, IntWritable, NullWritable> {
        private final IntWritable outKey = new IntWritable();

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String s = value.toString().trim();
            if (s.isEmpty()) return;
            try {
                outKey.set(Integer.parseInt(s));
                context.write(outKey, NullWritable.get());
            } catch (NumberFormatException e) {
                // 忽略非整数行
            }
        }
    }

    public static class SortReducer extends Reducer<IntWritable, NullWritable, IntWritable, IntWritable> {
        private int rank = 1;

        @Override
        protected void reduce(IntWritable key, Iterable<NullWritable> values, Context context)
                throws IOException, InterruptedException {
            context.write(new IntWritable(rank++), key);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "integer-sort");
        job.setJarByClass(Sort.class);
        job.setMapperClass(SortMapper.class);
        job.setReducerClass(SortReducer.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(1);                 // 单 Reducer，保证全局升序
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
