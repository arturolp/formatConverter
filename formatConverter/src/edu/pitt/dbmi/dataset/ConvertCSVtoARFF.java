package edu.pitt.dbmi.dataset;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.util.ArrayList;

import edu.pitt.dbmi.tools.FileManager;
import edu.pitt.dbmi.tools.Tools;

//import weka.core.Attribute;
//import weka.core.Instances;
//import weka.core.converters.CSVLoader;

/**
 *
 * @author arturo l�pez pineda
 */
public class ConvertCSVtoARFF {

	public Tools t = new Tools();
	public int columnValue = 2;
	public ArrayList<String[]> dataInput;
	public String dataOutput;
	public String[][] outputTable;
	public String[] attributes;
	public String[] attributeType; // (0) numeric, (1) nominal, (2) string, (3) date
	public String[] instances;
	public String inputName = "";
	public String outputName = "";
	public String inputPath = "";
	public String outputPath = "";
	public int IDindex;
	public int classIndex;

	public void updateOutput(String data) {

		FileManager.write((outputPath+outputName), data);
		System.out.println((outputPath+outputName) + " [created]");
	}

	public void appendOutput(String data) {

		FileManager.append((outputPath+outputName), data);
	}

	public void getLabels() {

		int numInsts = dataInput.size();
		int numAtts = dataInput.get(0).length;

		instances = new String[numInsts-1];
		attributes = new String[numAtts];
		attributeType = new String[numAtts];

		System.out.println("Instances = " + numInsts);
		System.out.println("Attributes = " + numAtts);

		//GET ATTRIBUTES LABELS
		attributes = dataInput.get(0);


		//GET INSTANCES LABELS

		for(int i = 0 ; i < attributes.length; i++){
			if(attributes[i].charAt(0) == '#'){
				System.out.println("IDattribute = "+attributes[i].substring(1) + "["+i+"]");
				IDindex = i;
				for(int j = 0; j < dataInput.size()-1; j++){
					instances[j] = dataInput.get(j+1)[i];
				}
				attributeType[i]="string";
			}
			else if(attributes[i].charAt(0) == '@'){
				System.out.println("classAttribute = "+attributes[i].substring(1) + "["+i+"]");
				classIndex = i;
				attributeType[i]= getNominal(i);
			}
			else{
				//determine attribute numeric or nominal
				if(isNumeric(dataInput.get(1)[i])){
					attributeType[i]="numeric";
				}
				else{
					attributeType[i]=getNominal(i);
				}
			}
		}

	}

	public String getNominal(int index){
		ArrayList<String> labels = new ArrayList<String>();

		labels.add(dataInput.get(1)[index]);


		// Order if they are ranges
		if((labels.get(0).contains("(") && labels.get(0).contains(")")) || (labels.get(0).contains("(") && labels.get(0).contains("]"))){
			for(int i = 2; i < dataInput.size(); i++){
				if(!labels.contains(dataInput.get(i)[index])){
					int labIndex = labels.size()-1;
					boolean insert = false;
					while(labIndex>=0 && insert == false){
						if(greaterThan(dataInput.get(i)[index], labels.get(labIndex))){
							labels.add((labIndex+1), dataInput.get(i)[index]);
							insert = true;
						}
						labIndex--;
					}
					if(!labels.contains(dataInput.get(i)[index])){
						labels.add(0, dataInput.get(i)[index]);
					}
				}
			}
		}
		//add in order of appearance
		else{ 
			for(int i = 2; i < dataInput.size(); i++){
				if(!labels.contains(dataInput.get(i)[index])){
					labels.add(dataInput.get(i)[index]);
				}
			}
		}



		return "{"+Tools.toString(labels)+"}";
	}

	public boolean greaterThan(String rangeA, String rangeB){
		boolean b = true;
		

		String ranA = rangeA.replace("'","");
		String ranB = rangeB.replace("'","");
		ranA = ranA.replace("\\","");
		ranB = ranB.replace("\\","");
		ranA = ranA.replace("(","");
		ranB = ranB.replace("(","");
		ranA = ranA.replace(")","");
		ranB = ranB.replace(")","");
		ranA = ranA.replace("]","");
		ranB = ranB.replace("]","");

		String[] bitsA = ranA.split("-");
		String[] bitsB = ranB.split("-");
		
		if(bitsA[0].equals("")){
			bitsA[0]=bitsA[1];
			bitsA[1]=bitsA[2];
		}
		if(bitsB[0].equals("")){
			bitsB[0]=bitsB[1];
			bitsB[1]=bitsB[2];
		}

		if(bitsA[0].equals("inf") || bitsB[1].equals("inf")){
			b = false;
		}
		else if(bitsA[1].equals("inf") || bitsB[0].equals("inf")){
			b = true;
		}
		else{
			try{
				if(Double.parseDouble(bitsA[0]) > Double.parseDouble(bitsA[0])){
					b = true;
				}
				else{
					b = false;
				}
			}
			catch(Exception e){

			}
		}
		
		return b;
	}

	public boolean isNumeric(String input){
		try{
			Integer.parseInt(input);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}

	public void getInputData(String file) {
		try {
			String[] f = file.split("/");
			inputPath = "";
			for (int i = 0; i < f.length - 1; i++) {
				inputPath = inputPath + f[i] + "/";

			}
			System.out.println(inputPath);
			inputName = f[f.length - 1];
			System.out.print(inputName + " -> ");
			String name[] = inputName.split("\\.");
			outputName = name[0] + ".arff";
			System.out.println(outputName);

			dataInput = FileManager.readCSV(inputPath+inputName);

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	public void createOutputFile() {
		String file = "@relation "+inputName+"\n\n";


		//SET RELATIONS
		for (int i = 0; i < attributes.length; i++) {
			if(i == IDindex || i == classIndex){
				file = file + "@attribute " + attributes[i].substring(1) + " "+attributeType[i]+"\n";
			}
			else{
				file = file + "@attribute " + attributes[i] + " "+attributeType[i]+"\n";
			}
		}
		file = file + "\n@data";
		updateOutput(file);


		//SET DATA
		int ii=0;
		for (int i = 0; i < instances.length; i++) {
			file = "";
			for (int j = 0; j < attributes.length-1; j++) {
				if (dataInput.get(i+1)[j] != null) {
					file = file + dataInput.get(i+1)[j] + ",";
				} else {
					file = file + "?,";
				}
			}
			file = file + dataInput.get(i+1)[attributes.length-1] + "\n";
			appendOutput(file);


			System.out.print(".");
			ii++;
			if(ii == 10){
				System.out.println(" ["+((int)i*100/instances.length)+"%]");
				ii=0;
			}

		}
		System.out.println(" [100%]");


		appendOutput(file);

	}

	public void runner(String file, String outputDirectory) {

		outputPath = outputDirectory;

		// GET INPUT DATA
		System.out.println("Reading from input file...");
		getInputData(file);

		//GET ATTRIBUTES AND INSTANCES
		System.out.println("Creating headers...");
		getLabels(); //state which is the target attribute

		//CREATE OUTPUT FILE
		System.out.println("Writing the output file... ");
		createOutputFile();
	}

}
