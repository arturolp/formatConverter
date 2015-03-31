package edu.pitt.dbmi.dataset;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

import java.util.Enumeration;

import edu.pitt.dbmi.tools.FileManager;
import edu.pitt.dbmi.tools.Util;

import weka.core.Attribute;
import weka.core.Instances;

import weka.core.converters.ConverterUtils.DataSource;
/**
 *
 * @author Arturo López Pineda <arl68@pitt.edu>
 */
public class ConvertARFFtoEBMC {

	private String fileInput = "";
	private String fileOutput = "";
	private String fileOutputPath = "";

	Instances data;

	public ConvertARFFtoEBMC() {
		super();
	}

	public String getFileInput() {
		return fileInput;
	}

	public void setFileInput(String fileInput) {
		this.fileInput = fileInput;
	}

	public String getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(String fileOutput) {
		this.fileOutput = fileOutput;
	}

	public String getFileOutputPath() {
		return fileOutputPath;
	}

	public void setFileOutputPath(String fileOutputPath) {
		this.fileOutputPath = fileOutputPath;
	}

	private void updateOutput(String data) {

		FileManager.write(fileOutput, data);
		System.out.println(fileOutput + " [created]");
	}

	private void appendOutput(String data) {

		FileManager.append(fileOutput, data);
	}

	private void readInputData(){

		DataSource source;
		try {
			source = new DataSource(this.fileInput);
			this.data = source.getDataSet();
			if (data.classIndex() == -1){
				data.setClassIndex(data.numAttributes() - 1);
			}

		} catch (Exception e) {
			System.out.println("==ERROR: ");
			e.printStackTrace();
		}

	}
	private void removeSingleStateAttributes() {
		int index = 0;
		int count = 0;
		int large_count = 0;

		int numAtts = data.numAttributes();
		int oindex = 0;;

		while(index < data.numAttributes()){
			int numStates = data.attribute(index).numValues();
			if(numStates <= 1){
				data.deleteAttributeAt(index);
			}
			else{
				index++;
			}

			if(count > 30){
				System.out.print(".");
				System.out.flush();
				count = 0;
				large_count++;
			}
			if(large_count > 30){
				System.out.println(" ["+((int)oindex*100/(int)numAtts)+"%]");
				System.out.flush();
				large_count = 0;
			}

			count++;
			oindex++;
		}
		System.out.println();
	}

	private void createNAM() {
		System.out.println("Writing NAM file "+this.fileOutput+"...");

		//First Line: Num attributes
		updateOutput(""+data.numAttributes());

		//Second Line: Name of attributes
		for(int i = 0; i < data.numAttributes(); i++){
			appendOutput(data.attribute(i).name()+"\n");
			//Third Line: Number of states of each attribute
			int numStates = data.attribute(i).numValues();
			appendOutput(numStates+"\n");
			//Fourth Line: Names of the states for each attribute
			Enumeration<?> labEnum = data.attribute(i).enumerateValues();
			for(int j = 0; j < numStates; j++){
				String label = labEnum.nextElement().toString().replace("'", "");
				appendOutput(label+"\n");
			}
			
			/*Enumeration<?> labEnum = data.attribute(i).enumerateValues();
			String labels[] = new String[numStates];
			for(int j = 0; j < numStates; j++){
				labels[j] = labEnum.nextElement().toString();
			}
			java.util.Arrays.sort(labels);
			for(int j = 0; j < labels.length; j++){
				String label = labels[j].replace("'", "");
				appendOutput(label+"\n");
			}
			*/
		}


		System.out.println("Done.");

	}

	private void createCAS() {
		System.out.println("Writing CAS file "+this.fileOutput+"...");

		//First Line: Num attributes
		updateOutput(""+data.numAttributes());

		//Second Line: Array of Num of states of each attribute
		appendOutput(" ");
		for(int i = 0; i < data.numAttributes(); i++){
			int numStates = data.attribute(i).numValues();
			appendOutput(numStates+ " ");
		}
		appendOutput("\n");
		
		//Third Line: Num of instances
		appendOutput(""+data.numInstances()+"\n");
		
		//Fourth Line: The dataset in nominal values
		for(int i = 0; i< data.numInstances(); i++){
			String buf = "";
			for(int j = 0; j < data.numAttributes(); j++){
				buf = buf + " "+((int)data.instance(i).value(j) +1);
			}
			appendOutput(buf+"\n");
		}
		
		
		System.out.println("Done.");
		
	}



	public void runner(String fileInput, String fileOutputPath) {
		this.fileInput = fileInput;
		this.fileOutputPath = fileOutputPath;
		String outPath = Util.fileNameStemAndSuffix(fileInput, "/")[0];
		String outName = Util.fileNameStemAndSuffix(fileInput, "/")[1];
		String fileCAS = outPath+"/"+Util.fileNameStemAndSuffix(outName, ".")[0] + ".cas";
		String fileNAM = outPath+"/"+Util.fileNameStemAndSuffix(outName, ".")[0] + ".nam";


		System.out.println("Removing attributes with a single state...");
		readInputData();

		removeSingleStateAttributes();

		this.fileOutput = fileNAM;
		createNAM();

		this.fileOutput = fileCAS;
		createCAS();


	}



}
