import java.util.*;

	import java.net.URI;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	import org.apache.hadoop.filecache.*;


public class NgramPiece {
    public int Gram;
    public StringBuilder[] words; 
    public int write; 
    NgramPiece(int GRAM) {
	Gram = GRAM;
	write = 0;
	words = new StringBuilder[GRAM];
    }
    public void addWord(String input) {
	if(write < Gram){
		words[write] = new StringBuilder(input);
		write++;
	}
    }

    public boolean match (NgramPiece np){
	if(this.Gram != np.Gram){
		return false;	
	}else{
		for (int i=0; i< Gram; i++){
			if(! this.words[i].toString().equals( np.words[i].toString() )){
				return false;
			}
		}
	}
	return true;
    }

    public String toString(){
	StringBuilder my = new StringBuilder();
	for(int i=0; i<Gram; i++){
		my.append(words[i]);
		my.append(" ");
	}
	return my.toString();
    }	
}
