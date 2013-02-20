import java.util.*;

	import java.net.URI;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	import org.apache.hadoop.filecache.*;


public class QueryProcessor {

    public ArrayList<NgramPiece> pieces;

    QueryProcessor(String Query, int GRAM) {
	pieces = new ArrayList<NgramPiece>();
	NgramPiece[] nps = new NgramPiece[GRAM];
	for(int i=0;i<GRAM;i++){
		nps[i] = new NgramPiece(GRAM);
	}
	
	Tokenizer tokenizer = new Tokenizer(Query);
	String[][] tokens = new String[GRAM][GRAM];
	String token;

	int i=0;
	int j=0;
 	while(tokenizer.hasNext()){
	        token = tokenizer.next();
		if(i==GRAM) i=0;
		for(j=0; j<GRAM; j++){
			if(i==j) nps[i] = new NgramPiece(GRAM);							
		        nps[j].addWord(token);	
			if(nps[j].write == GRAM){
				pieces.add(nps[j]);
				System.out.println("Printing Query NGram: " + nps[j].toString());
			}
		}
		i++;
	}	
	
    }

    public boolean compare(NgramPiece in){
	for (NgramPiece np: pieces){
		if(np.match(in)) return true;
	}
	return false;
    } 

}
