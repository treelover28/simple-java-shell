package edu.du.lai.khai.Assignment04;

/*
 * Khai Lai
 * COMP 2673 
 * Assignment 4- Filesystem Shell
 * Professor Mohammed Albow 
 * T.A Dalton Crutchfield and T.A Lombe Chileshe
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Driver {

	public static void main(String[] args) 
	{
		// try to read in existing fs.data.
		// if doesn't exist, create one
		Filesystem fs = readIn();
		
		Scanner keyboard = new Scanner(System.in);
		boolean on = true;
		while(on)
		{
			// split input into array of multiple words
			String[] input = keyboard.nextLine().split(" ");
			String[] command = new String[2];
			
			// first slot in command contains the actual command, like "cd", "ls", "mkdir", ....
			command[0] = input[0];
			
			// the rest of the input will rejoined into a single String
			StringBuilder param = new StringBuilder();
			for (int i = 1; i < input.length; i++)
			{
				param.append(input[i] + " ");
			}
			// this allow naming file/folder with more than one word
			// for example, you can have a file/folder named "Super long file name with spaces in between"
			
			command[1] = param.toString().trim();
			
			switch (command[0]) 
			// first word is the actual command 
			// second String is the parameter, path's name, file's name, and so on
			{
			case "cd":
				try 
				{
					if (command[1].equals("")) // no parameter is given
					{
						continue;
					}
					fs.cd(command[1]);
				}
				catch (IllegalStateException e)
				{
					System.out.println("No target directory named \"" + command[1] + "\" found in " + fs.currentDirectory());
				}				
				break;
				
			case "ls":
				fs.ls();
				break;
				
			case "mkdir":
				try
				{
					if (command[1].equals("")) // no parameter is given
					{
						continue;
					}
					fs.mkdir(command[1]);
				}
				catch (IllegalStateException e)
				{
					System.out.println("Folder with same name already exist in " + fs.currentDirectory());
				}
				break;
				
			case "touch":
				try
				{
					if (command[1].equals("")) // no parameter is given
					{
						continue;
					}
					fs.touch(command[1]);
				}
				catch (IllegalStateException e)
				{
					System.out.println("File with same name already exist in " + fs.currentDirectory());
				}
				break;
				
			case "pwd":
				fs.pwd();
				break;
				
			case "rm":
				try 
				{
					if (command[1].equals("")) // no parameter is given
					{
						continue; // do nothing
					}
					fs.rm(command[1]);
				}
				catch (IllegalStateException e)
				{
					System.out.println("No file with name \"" + command[1] + "\" found in current directory, "
							+ "or \"" + command[1] + "\" is a folder/directory");
				}
				break;
				
			case "rmdir":
				try 
				{
					if (command[1].equals("")) // no parameter is given
					{
						continue;
					}
					fs.rmdir(command[1]);
				}
				catch (IllegalStateException e)
				{
					System.out.println("Folder named \"" + command[1] + "\" in current directory is not empty. Or it does not exist at all");
				}
				break;
				
			case "tree":
				fs.tree();
				break;
				
			case "quit":
				on = false;
				break;
				
			default: // if user type nothing or other unknown command, program just does nothing.
				continue;
			}	
		}
		keyboard.close();
		saveFile(fs);
	}
	
	public static Filesystem readIn()
	{
		ObjectInputStream in = null;
		try 
		{
			FileInputStream fileIn = new FileInputStream("fs.data");	
			in = new ObjectInputStream(fileIn);
			
			// read in Object from fs.data and safe cast to Filesystem object
			Filesystem system = (Filesystem) in.readObject();
			in.close();
			return system;
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage() + "... New Filesystem will be created");
			return new Filesystem();
		} 
		catch (ClassNotFoundException e) 
		{
			System.out.println(e.getMessage() + "... New Filesystem will be created");
			return new Filesystem();	
		}
	}
	
	public static void saveFile(Filesystem currentSystem)
	{
		ObjectOutputStream out = null;
		try
		{
			// tries to save to existing file fs.data
			FileOutputStream fileOut = new FileOutputStream("fs.data");
			out = new ObjectOutputStream(fileOut);
			out.writeObject(currentSystem);
			out.close();
		}
		catch(FileNotFoundException e)
		{
			// else, save to a new file! 
			File saveFile = new File("fs.data");
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}
	}
}
