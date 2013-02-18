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
	
	    public static class Map extends MapReduceBase implements Mapper<Text, Text, Text, NgramPerPage> {
	      private final static IntWritable one = new IntWritable(1);
	      private Text titleX = new Text();
	      private StringBuilder title = new StringBuilder();
	      private Text word = new Text();
	      private Text[] words = new Text[GRAM];

	      private Path[] QueryFile;
              private boolean isTitle = false;	
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
	
	      public void map(Text key, Text value, OutputCollector<Text, NgramPerPage> output, Reporter reporter) throws IOException {
	        String line = value.toString();
		int SIZE = line.length();
	        int i = 0, j = 0, k, l;

	//	if(line.indexOf("<Title>")>-1) System.out.println(line);

		if(isTitle) {
			i = line.indexOf("</Title>");
			if(i > -1) {
				title.append(line.substring(0,i));
				isTitle = false;  // Found end of title
				titleX.set(title.toString());
	        	  //      output.collect(titleX, one);
			} else {
				title.append(line);
			}
		} else {
			i = line.indexOf("<Title>");
			if(i > -1) {
				title = new StringBuilder(line.substring(i,SIZE));
				isTitle = true;  // Found end of title
			} else {
			}
		}



	        String lineInPage = value.toString();
	        Tokenizer tokenizer = new Tokenizer(line);

		for(i=0; i<GRAM && tokenizer.hasNext(); i++){
		  words[i] = new Text();	
		  words[i].set(tokenizer.next());
		}

		if (i < GRAM) {
		    
		}
		else if ( i == GRAM && (tokenizer.hasNext() == false)) {
		    StringBuilder nGram = new StringBuilder();
		    i = 0;
		    nGram.append(words[i]);
		    for (i = 1; i < GRAM; i++) {
			nGram.append(" " + words[i]);
		    }

		    // if this ngram present in query string
                    //if (queryString.contains(nGram.toString())
		    output.collect(key, new NgramPerPage(nGram.toString(), one));
		}
		else {
		    j = 0;
		    while (tokenizer.hasNext()) {
			StringBuilder nGram = new StringBuilder();
			//if ((i+1) == GRAM) {
			k = j;
			nGram.append(words[k]);
			for (l = 1; l < GRAM; l++) {
			    k = (k + 1) % GRAM ;
			    nGram.append(" " + words[k]);
			}
                        j = (j + 1) % GRAM;
			i = (i + 1) % GRAM;
                        // if this ngram present in query string
                        //if (queryString.contains(nGram.toString())
			output.collect(key, new NgramPerPage(nGram.toString(), one));
			words[i++].set(tokenizer.next());
		    }
		}

	      }
	    }
	    /*
	    public static class Combine {
	    //extends MapReduceBase implements Combiner<Text, NgramPerPage, Text, IntWritable> {
		public void Combine(String key, Iterator<NgramPerPage>
				    values, OutputCollector<Text,
				    IntWritable> output, Reporter
				    reporter) {
		    int sum = 0;
		    StringBuilder nGram = new StringBuilder();
		    while (values.hasNext()) {
			sum += values.next.getCount();
			//if (first == true) {
			//    first = false;
			//    nGram.append(values.next.nGram);
			//}
		    }
		    //if (first == false)
		    output.collect(key, sum);
		}
	    }
	    */
	    public static class Reduce extends MapReduceBase implements Reducer<Text, NgramPerPage, Text, IntWritable> {
	      public void reduce(Text key, Iterator<NgramPerPage> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
	        int sum = 0;
		
                // TODO find biggest number of the iterator
		//	        while (values.hasNext()) {
		//sum += values.next().count;
	        //}
		// output.collect(key, new IntWritable(sum));
	      }
	    }
	
	    public static void main(String[] args) throws Exception {
	      
	      GRAM = Integer.parseInt(args[0]);

	      JobConf conf = new JobConf(Ngram.class);
	      conf.setJobName("wordcount");
	
	      conf.setOutputKeyClass(Text.class);
	      conf.setOutputValueClass(NgramPerPage.class);
	
	      conf.setMapperClass(Map.class);
	      //	      conf.setCombinerClass(Combine.class);
	      conf.setReducerClass(Reduce.class);
	
	      conf.setInputFormat(MyInputFormat.class);
	      conf.setOutputFormat(TextOutputFormat.class);

	      // This is the Query File passed to Mapper. 
	      DistributedCache.addCacheFile(new URI(args[1]),conf);  
 
	      FileInputFormat.setInputPaths(conf, new Path(args[2]));
	      FileOutputFormat.setOutputPath(conf, new Path(args[3]));
	
	      JobClient.runJob(conf);
	    }
	}
