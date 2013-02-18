	import java.io.IOException;
	import java.util.*;
	import java.net.URI;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	import org.apache.hadoop.filecache.*;
	
	public class Ngram {

	    private static int GRAM;
	
	    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
	      private final static IntWritable one = new IntWritable(1);
	      private Text title = new Text();
	      private Text[] words = new Text[GRAM];

	      private Path[] QueryFile;
	
	      public void configure(JobConf job) {
        	 // Get the cached archives/files
	 	 try{
		     //All your IO Operations
	             QueryFile = DistributedCache.getLocalCacheFiles(job);
		     System.out.println(QueryFile.toString());
		 }catch(IOException ioe){
		     //Handle exception here, most of the time you will just log it.
		     System.out.println("FATAL: Could not read in mapper -" +  QueryFile.toString());
		 }
	      }
	
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

	      // This is the Query File passed to Mapper. 
	      DistributedCache.addCacheFile(new URI(args[1]),conf);  
 
	      FileInputFormat.setInputPaths(conf, new Path(args[2]));
	      FileOutputFormat.setOutputPath(conf, new Path(args[3]));
	
	      JobClient.runJob(conf);
	    }
	}
