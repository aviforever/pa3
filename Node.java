import java.io.IOException;
import java.util.*;
import java.net.URI;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.filecache.*;





    public class Node {
    

    public  Node left, right, parent;
    
    /**
     * The value of this node.
     */
     final int value;
     final Text page;
    
    /**
     * The balance and height of this node.
     */
    public  int balance, height = 1;
    Node(int val, Text page) {
	this.value = val;
	this.page = new Text();
	this.page.set(page);
    }
    
    /**
     * Sets this node's left child.
     * @param left The new left child.
     */
    public void setLeft(Node left) {
	
	this.left = left;
	if(left != null)
	    left.parent = this;
	
    }
    
    /**
     * Sets this node's right child.
     * @param right The right child.
     */
    public void setRight(Node right) {
	
	this.right = right;
	if(right != null)
	    right.parent = this;
	
    }
    
    /**
     * Sets this node as root.
     */
	/*
    private void setRoot() {
	    
	    AvlTree.this.root = this;
	    parent = null;
	    
	    }*/


    public  void replace(Node old, Node rep) {
	
	if(left == old)
	    setLeft(rep);
	
	else if(right == old)
	    setRight(rep);
	
	else
	    throw new IllegalArgumentException("Cannot replace node because node is no son.");
	
    }
    
    /**
     * Updates this node's height and balance values.
     */
    public void update() {
	
	int[] sonHeight = sonHeight();
	
	balance = sonHeight[0] - sonHeight[1];
	height = Math.max(sonHeight[0], sonHeight[1]) + 1;
	
    }
    
    /**
     * Computes an array containing the heights of both sons.
     * (0: left son, 1: right son)
     * @return The heights of the sons as an array.
     */
    public  int[] sonHeight() {
	
	return new int[] {
	    right == null ? 0 : right.height,
	    left == null ? 0 : left.height
	};
	
    }
}




