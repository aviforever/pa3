	import java.io.IOException;
	import java.util.*;
	import java.net.URI;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	import org.apache.hadoop.filecache.*;
	
	import java.io.FileReader;
	import java.io.BufferedReader;

	public class Ngram {

	    //	    private static int GRAM;
	    static AvlTree   tree = new AvlTree();
	
	    public static class Map extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
		//	      private final static IntWritable one = new IntWritable(1);
	      //	      private Text titleX = new Text();
	      //	      private StringBuilder title = new StringBuilder();
	      //	      private Text word = new Text();
	      //	      private Text[] words = new Text[GRAM];
	      private QueryProcessor qp; 
	
//	      private FileSystem fs;
	      private Path[] QueryFile;
	      private static int GRAM;
	      private StringBuilder query = new StringBuilder();
	      private BufferedReader readBuffer;

	      //  Not needed. 
	      //	      private boolean isTitle = false;
	      	
	      public void configure(JobConf job) {


		
        	 // Get the cached archives/files
	 	 try{
		     GRAM = Integer.parseInt(job.get("gram"));
		     //All your IO Operations
		  //   fs = FileSystem.getLocal(new Configuration());
		     	             	QueryFile = DistributedCache.getLocalCacheFiles(job);
   	            //fs.close();
	
			// Query Processing
	     	 	readBuffer = new BufferedReader(new FileReader(QueryFile[0].toString()));
			String str;
			while((str = readBuffer.readLine()) != null){
				query.append(" " + str);			
			}		
			readBuffer.close();
			qp = new QueryProcessor(query.toString(), GRAM);
		       
		 } catch(IOException ioe){
		     //Handle exception here, most of the time you will just log it.
		     System.out.println("FATAL: Could not read in mapper -" +  QueryFile.toString());
		     }
	      }

	      	
	      public void map(Text key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {


		  //	        IntWritable out_value = new IntWritable(0);
	        int out = 0;
	        String line = value.toString();
		int SIZE = line.length();

		     //All your IO Operations
		  //   fs = FileSystem.getLocal(new Configuration());
		     //	             	QueryFile = DistributedCache.getLocalCacheFiles(job);
   	            //fs.close();

			// Query Processing
		//	readBuffer = new BufferedReader(new FileReader(QueryFile[0].toString()));
		//	String str;
		//	while((str = readBuffer.readLine()) != null){
		//		query.append(str);			
		//	}		
		//	readBuffer.close();
		//	qp = new QueryProcessor(query.toString(), GRAM);




//	         output.collect(key, one);

	//	if(line.indexOf("<Title>")>-1) System.out.println(line);

//		if(isTitle) {
//			i = line.indexOf("</Title>");
//			if(i > -1) {
//				title.append(line.substring(0,i));
//				isTitle = false;  // Found end of title
//				titleX.set(title.toString());
	        	  //      output.collect(titleX, one);
//			} else {
//				title.append(line);
//			}
//		} else {
//			i = line.indexOf("<Title>");
//			if(i > -1) {
//				title = new StringBuilder(line.substring(i,SIZE));
//				isTitle = true;  // Found end of title
//			} else {
//			}
//		}

		NgramPiece[] nps = new NgramPiece[GRAM];
	        for(int i=0;i<GRAM;i++){
        	        nps[i] = new NgramPiece(GRAM);
	        }


	        int i = 0, j = 0, k, l;
		String token;
	    //    String lineInPage = value.toString();
	        Tokenizer tokenizer = new Tokenizer(line);
	        while(tokenizer.hasNext()){
        	        token = tokenizer.next();
                	if(i==GRAM) i=0;
	                for(j=0; j<GRAM; j++){
        	                if(i==j) nps[i] = new NgramPiece(GRAM);
                	        nps[j].addWord(token);
                        	if(nps[j].write == GRAM){
                                	if(qp.compare(nps[j])){
						out++;
//						System.out.println("Hurray! - Found a match with Title " + key.toString());
//						System.out.println("Matching : " + nps[j].toString());
//						System.out.println(line);
					}
        	                }
               	 	}
	                i++;
	        }

		if (out != 0) {
		    StringBuilder titleScore = new StringBuilder();
		    Text values = new Text();
		    Text all    = new Text();
		    titleScore.append(key+" #");
		    titleScore.append(Integer.toString(out));
		    values.set(titleScore.toString());
		    all.set("one");
	       	    output.collect(all, values);
		}

		//                reporter.setStatus("$$$$Query Processor = " +qp.pieces.size());

//		for(i=0; i<GRAM && tokenizer.hasNext(); i++){
//		  words[i] = new Text();	
//		  words[i].set(tokenizer.next());
//		}

//		if (i < GRAM) {
//		    
//		}
//		else if ( i == GRAM && (tokenizer.hasNext() == false)) {
//		    StringBuilder nGram = new StringBuilder();
//		    i = 0;
//		    nGram.append(words[i]);
//		    for (i = 1; i < GRAM; i++) {
//			nGram.append(" " + words[i]);
//		    }
//
//		    // if this ngram present in query string
//                    //if (queryString.contains(nGram.toString())
//		    output.collect(key, new NgramPerPage(nGram.toString(), one));
//		}
//		else {
//		    j = 0;
//		    while (tokenizer.hasNext()) {
//			StringBuilder nGram = new StringBuilder();
//			//if ((i+1) == GRAM) {
//			k = j;
//			nGram.append(words[k]);
//			for (l = 1; l < GRAM; l++) {
//			    k = (k + 1) % GRAM ;
//			    nGram.append(" " + words[k]);
//			}
//                        j = (j + 1) % GRAM;
//			i = (i + 1) % GRAM;
//                        // if this ngram present in query string
//                        //if (queryString.contains(nGram.toString())
//			output.collect(key, new NgramPerPage(nGram.toString(), one));
//			words[i++].set(tokenizer.next());
//		        //}
//		   }
//		}
	      }
	    }
	    /*
	    public static class Combine extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterator<IntWritable>
				   values, OutputCollector<Text, IntWritable> output, Reporter
				    reporter) throws IOException {
		    int sum = 0;
		    while (values.hasNext()) {
			sum += values.next().get();
		    }
		    if (sum != 0)
			output.collect(key, new IntWritable(sum));
		}
	    }
	    */

	    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		    int maxScore = -1;
		    int scorePos = 0;
		    int score =0;
		    String title = " ";
		    String titleScore;
		    Text highScoreTitle = new Text();
		    Text highScore = new Text();
		    
		    while (values.hasNext()) {
			titleScore = values.next().toString();
			scorePos = titleScore.indexOf("#");
			score = Integer.parseInt(titleScore.substring(scorePos+1));

			if (score > maxScore) {
			    maxScore = score;
     			    title = titleScore.substring(0, scorePos);
			}else if (score == maxScore){
			   String temp = titleScore.substring(0, scorePos);
			   if(temp.compareTo(title) > 0){
				title = temp;
			   } 
			}
		    }
		    highScoreTitle.set(title);
		    highScore.set(Integer.toString(maxScore));
		    output.collect(highScore, highScoreTitle);
			
			

		  
		    /*

		if (values.hasNext()) {
                    tree.add(values.next().get(), key);
		}
		Node node = tree.findMax();
		System.out.println(node.page.toString()+ "score:="+node.value);
		output.collect(node.page, new IntWritable(node.value));
		    */
	      }
	    }
	
	    public static void main(String[] args) throws Exception {

		//readBuffer = new BufferedReader(new FileReader(QueryFile[0].toString()));
		//	      GRAM = Integer.parseInt(args[0]);

	      JobConf conf = new JobConf(Ngram.class);
	      //	      Configuration config = new Configuration();
	      //	      Path inFile = new Path(args[1]);
	      //	      FSDataInputStream in = fs.open(inFile); 
	      

	      conf.set("gram", args[0]);
	      //	      config.set(
	      conf.setJobName("wordcount");
	
	      conf.setOutputKeyClass(Text.class);
	     // conf.setOutputValueClass(NgramPerPage.class);
	      conf.setOutputValueClass(Text.class);
	
	      conf.setMapperClass(Map.class);
	      //	      conf.setCombinerClass(Combine.class);
	      conf.setReducerClass(Reduce.class);
	      conf.setNumReduceTasks(1);
	
	      conf.setInputFormat(MyInputFormat.class);
	      conf.setOutputFormat(TextOutputFormat.class);

	      // This is the Query File passed to Mapper. 
	      DistributedCache.addCacheFile(new URI(args[1]),conf);  
 
	      FileInputFormat.setInputPaths(conf, new Path(args[2]));
	      FileOutputFormat.setOutputPath(conf, new Path(args[3]));
	
	      JobClient.runJob(conf);
	    }
	}
