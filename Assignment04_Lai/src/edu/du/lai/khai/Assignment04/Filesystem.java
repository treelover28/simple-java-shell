package edu.du.lai.khai.Assignment04;

/*
 * Khai Lai
 * COMP 2673 
 * Assignment 4- Filesystem Shell
 * Professor Mohammed Albow 
 * T.A Dalton Crutchfield and T.A Lombe Chileshe
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public class Filesystem implements Serializable
{
	private Node root;
	private Node currentDirectory;
	
	public Filesystem()
	{
		this.root = new Node("", null, true);
		this.currentDirectory = root;
	}
	
	private void checkMakeFile(String name)
	{
		if (currentDirectory.children() != null) // only need to check if directory actually has children
		{
			for (Node c : currentDirectory.children())
			{
				if (c.name.equals(name)) 
				{
					// throw exception if file/folder with same name already exist in current directory
					throw new IllegalStateException("File with same name already exist!");
				}
				// else, do nothing
			}
		}
	}
	
	/** print all the children of current directory */
	public void ls() 
	{
		StringBuilder s = new StringBuilder();
		int filePerLine = 0;
		for (Node c : currentDirectory.children())
		{
			s.append(c.name + "\t  "); // large space in between files
			filePerLine++;
			if (filePerLine % 5 == 0)
			{
				s.append("\n"); // only print 5 items per line
			}
		}
		System.out.println(s.toString());
	}
	
	/*
	 * Add a new directory child node to the current directory.
	 * Throw exception if name already exist
	 */
	public void mkdir(String name) 
	{
		checkMakeFile(name);
		// if name already exist, checkMakeFile(name) will throw an exception before creating the new child
		currentDirectory.appendChild(name, true);
		
	}
	
	/*
	 * Add a new file child node to the current directory.
	 * Throw exception if name already exist
	 */
	public void touch(String name)
	{
		checkMakeFile(name);
		currentDirectory.appendChild(name, false);
	}
	
	/*
	 * Print the full path name of current directory starting with root.
	 * Each directory is separated by dashes /
	 */
	public void pwd()
	{
		Stack<String> directories = new Stack<>();
		Node temp = currentDirectory;
		while (!temp.isRoot()) // while we haven't reach the root, add current directory's name to Stack
		{
			directories.push(temp.name);
			temp = temp.parent; // move up
		}
		
		// add root directory's name to stack
		directories.push(temp.name); // currentDirectory is now at the root
		
		// reverse the list and add dash /
		StringBuilder pathName = new StringBuilder();
		
		while(!directories.isEmpty())
		{
			pathName.append(directories.pop( ) + "/");
		}	
		// Print path to console
		System.out.println(pathName.toString());
	}
	
	/*
	 * Change currentDirectory to name.
	 * If currentDirectory doesn't have a directory child named "name",
	 * throw an exception.
	 */
	public void cd(String name)
	{
		if (name.equals("..")) // allow command: cd .. which let user move back to previous directory
			// not required by Assignment but I just added to help ease traversal.
		{
			if (currentDirectory.parent != null)
			{
				currentDirectory = currentDirectory.parent;
				return;
			}
			else // if already at highest directory, does nothing
			{
				return;
			}
		}
		
		for (Node c : currentDirectory.children()) 
			// search through list of all children and set currentDirectory to matching directory
		{
			if (c.name.equals(name) && c.isDirectory())
			{
				currentDirectory = c;
				return;
			}
		}
		throw new IllegalStateException("No directory with name " + "\"" + name +"\"" + " found");
	}
	
	/*
	 * remove the file "name" from currentDirectory
	 * Throw an exception if it's a directory or does not exist
	 */
	public void rm(String name) 
	{
		for (Node c : currentDirectory.children())
		{
			if (c.name.equals(name) && !c.isDirectory())
			{
				// if a file with matching name is found. Remove it
				currentDirectory.children.remove(c); 
				return;
			}
		} // else, if it's a directory or does not exist, throw an exception
		throw new IllegalStateException("No file with name " + "\"" + name +"\" found in "+ currentDirectory());
	}
	
	/*
	 * remove the directory "name" from the currentDirectory.
	 * Throw an exception if it's not a directory, or if it's not empty
	 */
	public void rmdir(String name)
	{
		for (Node c: currentDirectory.children())
		{
			if (c.name.equals(name) &&
				c.isDirectory() &&
				c.children.size()== 0)	
			{
				// if matching empty directory is found within currentDirectory's children, remove it
				currentDirectory.children.remove(c); 
				return;
			}
		} // else, throw exception
		throw new IllegalStateException("No empty directory named " + "\"" + name + "\" found in " + currentDirectory());
	}
	
	private String preorderPrint(Node r, int level, StringBuilder sb) // private helper method to be used in tree()
	{
		for(int i = 0; i < level; i++)
		{
			sb.append("\t"); // indent to distinguish different levels
		}
		
		sb.append("|" + r + "\n"); // each node on a separate line
		// I use the | to help make each level more distinguishable
		
		if (r.children != null) // if currentDirectory has children, recursively print them out
		{
			for (int i = 0; i < r.children.size(); i++)
			{
				preorderPrint(r.children.get(i), level+1, sb);
			}
		}
		return sb.toString();
	}
	
	/*
	 * Print the whole tree regardless of currentDirectory
	 * Uses helper method preorderPrint( Node r, int level, StringBuilder sb)
	 */
	public void tree()
	{
		StringBuilder tree = new StringBuilder();
		preorderPrint(root, 0, tree);
		System.out.println(tree.toString());
	}
	
	
	public String currentDirectory()
	{
		return this.currentDirectory.name;
	}
	
	
	//******Private Class Node**********************************************************************************
	private class Node implements Serializable 
	{
		private String name;
		private ArrayList<Node> children;
		private Node parent;
		private boolean isDirectory;
		
		public Node(String name, Node parent, boolean isDirectory)
		{
			this.name = name;
			this.parent = parent;
			this.isDirectory = isDirectory; // folder or file ?
			
			if (!this.isDirectory) // only have array of children is the Node is a directory/folder
			{
				this.children = null;
			}
			else 
			{
				this.children = new ArrayList<>();
			}
		}
		
		public boolean isDirectory() // check is Node is a folder or file
		{
			return this.isDirectory;
		}
		
		public ArrayList<Node> children() // return an ArrayList of Node's children
		{
			return this.children;
		}
		
		public void appendChild(String name, boolean isDirectory)
		{
			Node newChild = new Node(name, this, isDirectory); // parent of new child is current Node
			// parent of newChild is calling object a.k.a current Node
			this.children.add(newChild);
		}
		
		public boolean isRoot()
		{
			// if parent is null, Node must be the root
			return this.parent == null;
		}
		
		public String toString()
		{
			return this.name;
		}
	}
	//**************End of Private class Node*******************************************************************

}
