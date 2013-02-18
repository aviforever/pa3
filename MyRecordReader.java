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

  private static final Pattern TITLE_BEGIN_END = Pattern.compile("(.*)<title>(.*)</title>(.*)");
  private static final Pattern TITLE_BEGIN = Pattern.compile("(.*)<title>(.*)");
  private static final Pattern TITLE_END = Pattern.compile("(.*)</title>(.*)");

  public MyRecordReader(Configuration job, FileSplit split) throws IOException {
    lineReader = new LineRecordReader(job, split);

    lineKey = lineReader.createKey();
    lineValue = lineReader.createValue();
  }

  public boolean next(Text key, Text value) throws IOException {
    // get the next line
    if (!lineReader.next(lineKey, lineValue)) {
      return false;
    }

    Matcher m;

    while (lineReader.next(lineKey, lineValue)) {
	if((m= TITLE_BEGIN.matcher(lineValue.toString()) ).matches()){
	     key.set(m.group(1));
	     key.set(lineValue.toString());
    	     value.set("Harshit's text value");
	     return true;
        };
	if(lineValue.toString().indexOf("<title>") > -1){
	     //key.set(lineValue.toString());
    	     //value.set("Harshit's text value");
	     //return true;
        };
    }


    // parse the lineValue which is in the format:
//    // objName, x, y,
//    Text [] pieces = lineValue.toString().split(",");
//    if (pieces.length != 4) {
//      throw new IOException("Invalid record received");
//    }
//
//    // try to parse floating point components of value
//    float fx, fy, fz;
//    try {
//      fx = Float.parseFloat(pieces[1].trim());
//      fy = Float.parseFloat(pieces[2].trim());
//      fz = Float.parseFloat(pieces[3].trim());
//    } catch (NumberFormatException nfe) {
//      throw new IOException("Error parsing floating point value in record");
//    }
//
//    // now that we know we'll succeed, overwrite the output objects
//
//    key.set(pieces[0].trim()); // objName is the output key.
//
//    value.x = fx;
//    value.y = fy;
//    value.z = fz;

//    System.out.println("Within Record Reader");

    key.set("Harshit's text key");
    value.set("Harshit's text value");
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
