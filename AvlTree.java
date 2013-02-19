import java.io.IOException;
import java.util.*;
import java.net.URI;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.filecache.*;


public class AvlTree {

    Node root;
    
    AvlTree () {
	root = null;
    }
    /**
     * Walks up from the most recent deleted item to the tree root in
    order to
    * inform all entities 
    * @param a The current node that has to be revalidated after a tree
    * alteration.
    */
    public void up(Node a) {
	
	Node next = a.parent;
	int orgHeigth = a.height;
	
	System.out.println("Validating element:" +a.value+ "(balance/height:" +a.balance + a.height);
	a.update();
	System.out.println("bal :" + a.balance + "height"+ a.height);
	
	// Check if tree node is out of balance.
	if(Math.abs(a.balance) >= 2)
	    a = rotate(a);
	
	// Update parent nodes only if the tree root was not yet reached
	// and if the height of the current node was altered.
	if(next != null && orgHeigth != a.height)
	    up(next);
	
    }

    /**
     * Adds a new value to this tree.
     * @param val The new value.
     */
    public void add(int val, Text page) {
	
	System.out.println("Adding element:"+ val);
	
	// No root yet exists -> Insert as root and return.
	if(this.root == null) {
	    this.root = new Node(val, page);
	    this.root.parent = null;
	    System.out.println(" -> Setting as root.");
	    return;
	}
	
	// Find right path for insertion.
	Node c = root;
	while(true) {
	    
	    // Element is smaller than current node's value -> Continue at
	    // right.
	    if(val < c.value) {
		
		System.out.println(" -> Smaller than"+c.value+ ": Going left%n");
		
		if(c.left == null) {
		    c.setLeft(new Node(val, page));
		    System.out.println(" -> Setting as left child of "+ c.value);
		    up(c);
		    return;
		} else
		    c = c.left;
		
		// Element is bigger than current node's value -> Continue
		// at right.
	    } else if(val > c.value) {
		
		System.out.println(" -> Bigger than "+ c.value+": Going right");
		
		if(c.right == null) {
		    c.setRight(new Node(val, page));
		    System.out.println(" -> Setting as right child of "+ c.value);
		    up(c);
		    return;
		} else
		    c = c.right;
		
	    } else {
		//System.out.println("Already Inserted");
		return ;
	    }
	    
	}
	
    }


    /**
     * Performs a rotation. Will only work correctly if rotation is
    actually required.
    * @param a The rotation anchor.
    * @return The node's replacement node.
    */
    public  Node rotate(Node a) {
	
	// Correct for right side overweight.
	if (a.balance == 2)
	    // Correct for right-left side overweight.
	    if(a.right.balance == -1)
		return rotateLeftDouble(a);
         	// Correct for right-right side overweight.
	    else //if(a.right.balance == 1 || /* delete only: */a.right.balance == 0)
		return rotateLeft(a);
    
            // Correct for left side overweight
	else //if(a.balance == -2)
	    // Correct for left-right side overweight.
	    if(a.left.balance == 1)
		return rotateRightDouble(a);
	    // Correct for left-left side overweight.
	    else //if(a.left.balance == -1 || /* delete only: */ a.left.balance == 0)
		    return rotateRight(a);
    }

    public Node rotateRight(Node a) {
	
	Node s = a.left;

	System.out.println(" -> Right single rotation on elements a:"+a.value+ " s:"+s.value);
	
	if (a == root) {
	    root = s;
	    root.parent = null;
	}
	else
	    a.parent.replace(a, s);
	
	a.setLeft(s.right);
	a.update();
	
	s.setRight(a);
	s.update();
	
	return s;
	
    }

    public Node rotateLeft(Node a) {
	
	Node s = a.right;

	System.out.println(" -> Left single rotation on elements a:"+a.value+" s:"+ s.value);
	
	if (a == root) {
	    root = s;
	    root.parent = null;
	}
	else
	    a.parent.replace(a, s);
	
	a.setRight(s.left);
	a.update();
	
	s.setLeft(a);
	s.update();
	
	return s;
	
    }
    
    public Node rotateLeftDouble(Node a) {
	
	Node s = a.right;
	Node b = s.left;

	System.out.println(" -> Double left rotation on elements a:"+a.value+ " s: " +s.value+ " b: "+b.value);
	
	if (a == root) {
	    root = b;
	    root.parent = null;
	}
	else
	    a.parent.replace(a, b);

	a.setRight(b.left);
	a.update();
	
	s.setLeft(b.right);
	s.update();
	
	b.setLeft(a);
	b.setRight(s);
	b.update();
	
	return b;
	
    }


    public Node rotateRightDouble(Node a) {
	
	Node s = a.left;
	Node b = s.right;
	
	System.out.println(" -> Double right rotation on elements a:"+a.value+ "s:" +s.value+  "b:" +b.value);
	
	if (a == root) {
	    root = b;
	    root.parent = null;
	}
	else
	    a.parent.replace(a, b);
	
	a.setLeft(b.right);
	a.update();
	
	s.setRight(b.left);
	s.update();
	
	b.setRight(a);
	b.setLeft(s);
	b.update();
	
	return b;
	
    }
    public  Node findMax(  )
    {
	Node t;
	if( root == null )
	    return null;
	t = root;
	while( t.right != null )
	    t = t.right;
	return t;
    }

}