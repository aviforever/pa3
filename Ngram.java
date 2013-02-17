	import java.io.IOException;
	import java.util.*;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	
	public class Ngram {

	    private static int GRAM;
	
	    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
	      private final static IntWritable one = new IntWritable(1);
	      private Text title = new Text();
	      private Text[] words = new Text[GRAM];
	
	      public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

		for(int i=0; i<GRAM; i++){
		  words[i] = new Text();	
		}

	        String line = value.toString();
	        StringTokenizer tokenizer = new StringTokenizer(line);
	        while (tokenizer.hasMoreTokens()) {
		  for (int i=0; i<GRAM-1; i++){
			words[i] = words[i+1];	 // left shift
		  }
	          words[GRAM-1].set(tokenizer.nextToken());

	          // Temp hack . Fixme. 
		  title.set(words[0].toString() + " " + words[1].toString() + " " + words[2].toString());

	          output.collect(title, one);
	        }
	      }
	    }
	
	    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
	      public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
	        int sum = 0;
	        while (values.hasNext()) {
	          sum += values.next().get();
	        }
	        output.collect(key, new IntWritable(sum));
	      }
	    }
	
	    public static void main(String[] args) throws Exception {
	      
	      GRAM = Integer.parseInt(args[0]);

	      JobConf conf = new JobConf(Ngram.class);
	      conf.setJobName("wordcount");
	
	      conf.setOutputKeyClass(Text.class);
	      conf.setOutputValueClass(IntWritable.class);
	
	      conf.setMapperClass(Map.class);
	      conf.setCombinerClass(Reduce.class);
	      conf.setReducerClass(Reduce.class);
	
	      conf.setInputFormat(TextInputFormat.class);
	      conf.setOutputFormat(TextOutputFormat.class);

	   
	      // Currently pointing to query file. Point to arg [2] instead. 
	      FileInputFormat.setInputPaths(conf, new Path(args[2]));
	      FileOutputFormat.setOutputPath(conf, new Path(args[3]));
	
	      JobClient.runJob(conf);
	    }
	}
