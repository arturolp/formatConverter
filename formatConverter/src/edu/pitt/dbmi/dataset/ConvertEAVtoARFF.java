package edu.pitt.dbmi.dataset;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.io.File;

import edu.pitt.dbmi.tools.FileManager;
import edu.pitt.dbmi.tools.Tools;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

/**
 *
 * @author arturo l�pez pineda
 */
public class ConvertEAVtoARFF {

	public Tools t = new Tools();
	public int columnValue = 2;
	public Instances dataInput;
	public Instances dataOutput;
	public String[][] outputTable;
	public String[] attributes;
	public String[] patients;
	public String inputName = "";
	public String outputName = "";
	public String inputPath = "";
	public String outputPath = "";

	public void updateOutput(String data) {

		FileManager.write((outputPath+"/"+outputName), data);
		System.out.println((outputPath+"/"+outputName) + " [created]");
	}

	public void appendOutput(String data) {

		FileManager.append((outputPath+"/"+outputName), data);
	}

	public void getLabels(String className) {

		//Read every instance-attribute and assign it to the correct place in the new table
		dataInput.sort(0);

		int numPatients = dataInput.numDistinctValues(0);
		int numAtts = dataInput.numDistinctValues(1);

		//System.out.println(dataInput.attribute(0).value(0) + " > "
		//    + dataInput.attribute(1).value(0) + " > "
		//    + dataInput.attribute(2).value(0));

		//dataInput.sort(0);

		//System.out.println(dataInput.attribute(0).value(0) + " > "
		//    + dataInput.attribute(1).value(0) + " > "
		//    + dataInput.attribute(2).value(0));

		patients = new String[numPatients];
		attributes = new String[numAtts];

		System.out.println("Patients = " + numPatients);
		System.out.println("Attributes = " + numAtts);



		//GET PATIENT LABELS
		int index = 0;
		String pat = dataInput.instance(0).toString().split(",")[0];
		patients[index] = pat;
		for (int i = 1; i < dataInput.numInstances(); i++) {
			pat = dataInput.instance(i).toString().split(",")[0];
			if (!pat.equals(patients[index])) {
				index++;
				patients[index] = pat;
			}
			//System.out.println(""+index+","+pat);
		}


		//GET PATIENT LABELS
		/*int index = 0;
    Attribute pat = dataInput.attribute(0);
    patients[index] = pat.value(0);
    for (int i = 1; i < pat.numValues(); i++) {
      if (!pat.value(i).equals(patients[index])) {
        index++;
        patients[index] = pat.value(i);
      }
    }*/


		//GET ATTRIBUTE LABELS
		index = 0;
		Attribute att = dataInput.attribute(1);
		//Attribute attDesc = dataInput.attribute(2);

		for (int i = 0; i < att.numValues(); i++) {


			if (!att.value(i).equals(className)) {
				//if (!att.value(i).equals(attributes[index])) {
				//System.out.println("(" + i + ")");

				attributes[index] = att.value(i);

				String list = "";
				list = list + "list[" + index + "][0]=\"" + att.value(i) + "\";\n";

				//list = list + "\n";

				//System.out.println(list);

				index++;


				// }
			}

		}
		//System.out.println("[[" + index + "]]");
		attributes[index] = className; // Adding the target to the end


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

			CSVLoader loader = new CSVLoader();
			loader.setSource(new File(file));
			dataInput = loader.getDataSet();


			/*
			 * ArffSaver saver = new ArffSaver(); saver.setInstances(dataInput);
			 * saver.setFile(new File(fileNameOutput)); saver.setDestination(new
			 * File(fileNameOutput)); saver.writeBatch();
			 */


		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	public void populateRelationalInstances(String target) {

		outputTable = new String[patients.length][attributes.length];

		//Read every instance-attribute and assign it to the correct place in the new table
		dataInput.sort(0);

		int rowIndex = 0;
		int count = 0;

		for (int i = 0; i < dataInput.numInstances(); i++) {
			//System.out.println(">>");
			//System.out.flush();
			//Get the values and names
			//System.out.println(""+dataInput.instance(i).toString().split(",")[0]);
			String patientName = dataInput.instance(i).toString().split(",")[0]; //only string, check if they are numbers
			String attributeName = dataInput.instance(i).stringValue(1);
			String attributeValue = dataInput.instance(i).stringValue(3);
			//try {
			//  patientName = dataInput.instance(i).stringValue(0);
			//  attributeName = dataInput.instance(i).stringValue(1);
			//  attributeValue = dataInput.instance(i).stringValue(3);
			//} catch (Exception e) {
			//  System.out.println("" + dataInput.instance(i).toString());
			//  System.out.println("" + e.getMessage());
			//}



			//Search the correct row index
			//System.out.println(patientName + " ?= "+patients[0]);
			if (!patientName.equals(patients[rowIndex])) {
				rowIndex++;
			}
			//System.out.println(""+rowIndex+ ", "+ patients[rowIndex]);

			//Search the correct column index
			int columnIndex = 0;
			boolean found = false;
			while (found == false && columnIndex < attributes.length) {
				//System.out.println(""+attributeName+" =? "+attributes[columnIndex]);
				if (attributeName.equals(attributes[columnIndex])) {
					found = true;
				} else {
					columnIndex++;
				}
			}



			//System.out.println("Row="+rowIndex+" \tCol="+columnIndex + "\t patient = "+patientName+ "\tattribute ="+attributeName+ "\tvalue="+attributeValue);


			outputTable[rowIndex][columnIndex] = attributeValue;
			if (attributeName.equals(target) && attributeValue.endsWith("T")) {
				count++;
			}
		}

		System.out.println("T: " + count);

	}

	public void createOutputFile() {
		String file =
				"% 1. Title: UPMC ED reports\n"
						+ "% \n"
						+ "% 2. Sources:\n"
						+ "%   (a) Creator: UPMC\n"
						+ "%   (b) Donor: Dr. X (email)\n"
						+ "%   (c) Date: Some date, 2011\n"
						+ "% \n\n"
						+ "@RELATION Influenza\n\n";


		//SET RELATIONS
		file = file + "@ATTRIBUTE patientID string\n";
		for (int i = 0; i < attributes.length; i++) {
			file = file + "@ATTRIBUTE " + attributes[i] + " {T, F}\n";
		}
		file = file + "\n@DATA";
		updateOutput(file);


		//SET DATA
		for (int i = 0; i < outputTable.length; i++) {
			//System.out.println("<<"+i);
			file = patients[i] + ",";
			for (int j = 0; j < outputTable[i].length - 1; j++) {
				if (outputTable[i][j] != null) {
					file = file + outputTable[i][j] + ",";
				} else {
					file = file + "?,";
				}
			}
			file = file + outputTable[i][outputTable[i].length - 1] + "\n";
			appendOutput(file);
		}


		//appendOutput(file);

	}

	public void runner(String file, String target, String outputDirectory) {
		
		outputPath = outputDirectory;

		// GET INPUT DATA
		System.out.println("Reading from input file...");
		getInputData(file);

		//GET ATTRIBUTES AND PATIENTS
		System.out.println("Creating headers...");
		getLabels(target); //state which is the target attribute

		//GET NEW INSTANCES
		System.out.println("Filling the database...");
		populateRelationalInstances(target);

		//CREATE OUTPUT FILE
		System.out.println("Writing the output file... ");
		createOutputFile();
	}

}
