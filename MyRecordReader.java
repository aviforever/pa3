        import java.io.IOException;
        import java.util.*;
        import java.net.URI;

        import org.apache.hadoop.fs.Path;
        import org.apache.hadoop.conf.*;
        import org.apache.hadoop.io.*;
        import org.apache.hadoop.mapred.*;
        import org.apache.hadoop.util.*;
        import org.apache.hadoop.filecache.*;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;



class MyRecordReader implements RecordReader<Text, Text> {

  private LineRecordReader lineReader;
  private LongWritable lineKey;
  private Text lineValue;
  private boolean isTitle;
  private StringBuilder content;

  private static final Pattern TITLE_BEGIN_END = Pattern.compile(".*<title>(.*)</title>.*");
  private static final Pattern TITLE_BEGIN = Pattern.compile("(.*)<title>(.*)");
  private static final Pattern TITLE_END = Pattern.compile("(.*)</title>(.*)");

  public MyRecordReader(Configuration job, FileSplit split) throws IOException {
    lineReader = new LineRecordReader(job, split);

    lineKey = lineReader.createKey();
    lineValue = lineReader.createValue();
    isTitle = false;
    content = new StringBuilder();
  }

  public boolean next(Text key, Text value) throws IOException {
    // get the next line
//    if (!lineReader.next(lineKey, lineValue)) {
//      return false;
//    }

    Matcher m;
   
  //  Check if No Title. 
    if(!isTitle) {
    	while (lineReader.next(lineKey, lineValue)) {
		if((m= TITLE_BEGIN_END.matcher(lineValue.toString()) ).matches()){
			isTitle = true;
			break;
		}
	}	
    }
    
    if((m= TITLE_BEGIN_END.matcher(lineValue.toString()) ).matches()) key.set(m.group(1));
//    key.set(lineValue.toString());
    content = new StringBuilder();
    while (lineReader.next(lineKey, lineValue)) {
	if((m= TITLE_BEGIN_END.matcher(lineValue.toString()) ).matches()){
		value.set(content.toString());
		return true;
	}else{
		content.append(" " + lineValue.toString());
	}
    }	
    	
    if(content.toString().equals("")) {
	return false; 
    }

    return true;

  }

  public Text createKey() {
    return new Text();
  }

  public Text createValue() {
    return new Text();
  }

  public long getPos() throws IOException {
    return lineReader.getPos();
  }

  public void close() throws IOException {
    lineReader.close();
  }

  public float getProgress() throws IOException {
    return lineReader.getProgress();
  }
}
