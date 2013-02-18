import java.util.*;

	import java.net.URI;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	import org.apache.hadoop.filecache.*;


public class NgramPerPage {
    Text nGram;
    IntWritable count;
    NgramPerPage(String nGram, IntWritable count) {
	this.nGram = new Text();
	this.nGram.set(nGram);
	this.count = count;
    }
    public IntWritable getCount() {
	return count;
    }
}
