/**
     * Licensed to the Apache Software Foundation (ASF) under one
     * or more contributor license agreements.  See the NOTICE file
     * distributed with this work for additional information
     * regarding copyright ownership.  The ASF licenses this file
     * to you under the Apache License, Version 2.0 (the
     * "License"); you may not use this file except in compliance
     * with the License.  You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

        import java.io.IOException;
        import java.util.*;
        import java.net.URI;

        import org.apache.hadoop.fs.Path;
        import org.apache.hadoop.conf.*;
        import org.apache.hadoop.io.*;
        import org.apache.hadoop.mapred.*;
        import org.apache.hadoop.util.*;
        import org.apache.hadoop.filecache.*;
        import org.apache.hadoop.classification.InterfaceAudience;
        import org.apache.hadoop.classification.InterfaceStability;    


    
    /** An {@link InputFormat} for plain text files.  Files are broken into lines.
     * Either linefeed or carriage-return are used to signal end of line.  Keys are
     * the position in the file, and values are the line of text.. */
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public class MyInputFormat extends FileInputFormat<Text, Text> {
   
//      @Override
//      public RecordReader<Text, Text>  createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
//		return new MyRecordReader(context.getConfiguration(),(FileSplit) split);
//      } 

      @Override
      public RecordReader<Text, Text> getRecordReader(InputSplit split, JobConf job,  Reporter reporter) throws IOException{
		return new MyRecordReader(job,(FileSplit) split);
      }

    
      protected boolean isSplitable(JobContext context, Path file) {
//        final CompressionCodec codec =
//          new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
//        if (null == codec) {
//          return true;
//        }
//        return codec instanceof SplittableCompressionCodec;
	  return false;
      }
    
    }

