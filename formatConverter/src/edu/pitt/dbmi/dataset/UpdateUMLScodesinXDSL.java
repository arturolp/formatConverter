package edu.pitt.dbmi.dataset;

import java.util.Hashtable;

import edu.pitt.dbmi.tools.FileManager;
import edu.pitt.dbmi.tools.Tools;
import edu.pitt.dbmi.tools.Util;




public class UpdateUMLScodesinXDSL {
	public static String umls_xdsl = "";
	public static String xdsl = "";
	public boolean useCodeDescriptions = false;
	Hashtable<String, String> codesTable;


	public static void updateBif(String s){
		umls_xdsl = s;
	}

	public static void updateXDSL(String s){
		xdsl = xdsl + s;
	}

	public void transformToXDSL(String target){

		String[] lines = Tools.parseByLine(umls_xdsl);

		String fileOut = "";
		xdsl = "";

		for(int i = 0; i < lines.length; i++){

			String[] name = Tools.parseXML(lines[i], "<name>", "</name>");
			if(name.length>0){
				String umlsName = codesTable.get(name[0]);
				//System.out.println("  name = "+name);
				if(umlsName==null){
					umlsName = name[0];
				}
				fileOut = "\t\t\t<name>"+umlsName+"</name>\n";
			}
			else if (lines[i].equals("\t\t\t<parents></parents>")){
					fileOut = "\t\t\t<parents>"+target+"</parents>\n";
				}
				else{
					fileOut = lines[i]+ "\n";
				}

			updateXDSL(fileOut);
		}


	}


	public void runner(String inputFile, String target, String outputPath, String hashFile){


		// GET INPUT DATA
		System.out.println("\n------------\n Reading file... ");
		System.out.println(inputFile);
		xdsl = FileManager.read(inputFile);
		if(hashFile != "empty"){
			useCodeDescriptions = true;
			System.out.println("\n------------\n Reading hash file... ");
			System.out.println(hashFile);
			codesTable = FileManager.readHashTable(hashFile);
		}
		
		
		// UPDATE UMLS IN XDSL
		System.out.println("\n------------\n Updating... ");
				updateXDSL(xdsl);
		

		// CREATE OUTPUT FILE
		System.out.println("\n------------\n Creating file... ");
		String name[] = inputFile.split("\\.");
		String outputName = name[0] + "-umls.xdsl";
		System.out.println(outputName);
		FileManager.write(outputName, xdsl);
		System.out.println("\n------------\nNew File created");


	}

}
