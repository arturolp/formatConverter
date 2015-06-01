package edu.pitt.dbmi.dataset;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.pitt.dbmi.tools.FileManager;
import edu.pitt.dbmi.tools.Tools;
import edu.pitt.dbmi.tools.Util;

public class ConvertBIFtoXDSL {
	public String bif = "";
	public String xdsl = "";
	public String hash = "";
	public boolean useCodeDescriptions = false;
	Hashtable<String, String> codesTable;

	public void updateXDSL(String s){
		xdsl = xdsl + s;
	}

	public void transformToXDSL(){

		//ADD header
		xdsl = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
				" <smile version=\"1.0\" id=\"Network1\" numsamples=\"1000\">\n" +
				"\t<nodes>\n";

		//Create Nodes
		String[] variables = Tools.parseXML(bif, "<VARIABLE TYPE=\"nature\">", "</VARIABLE>");
		String[] definitions = Tools.parseXML(bif, "<DEFINITION>", "</DEFINITION>");

		//for each node add values
		ArrayList<String> nodes = new ArrayList<String>();
		for(int i = 0; i < variables.length; i++){
			nodes.add(getNode(variables[i], definitions));
			//System.out.println(nodes.get(i));
		}

		ArrayList<String> ordered = sort(nodes);
		for(int i = 0; i < ordered.size(); i++){
			updateXDSL(ordered.get(i));
		}

		//Middle Text
		String middle = "\t</nodes>\n" +
				"\t<extensions>\n" +
				"\t <genie version=\"1.0\" app=\"GeNIe 2.0.2603.0\" name=\"Flu\" faultnameformat=\"nodestate\">\n";

		updateXDSL(middle);


		//Update nodes
		String node = "";
		for(int i = 0; i < variables.length; i++){

			String[] id_code = Tools.parseXML(variables[i], "<NAME>", "</NAME>");

			 
			String name = id_code[0];
			if(useCodeDescriptions == true){
				//System.out.print("id_code = "+id_code[0]);
				name = codesTable.get(id_code[0]);
				//System.out.println("  name = "+name+" : "+(name==null));
				if(name==null){
					//System.out.println("id_code = "+id_code[0]);
					name = id_code[0];
				}
			}

			node = "\t\t<node id=\""+id_code[0]+"\">\n" +
					"\t\t\t<name>"+name+"</name>\n" +
					"\t\t\t<interior color=\"e5f6f7\" />\n" +
					"\t\t\t<outline color=\"000080\" />\n" +
					"\t\t\t<font color=\"000000\" name=\"Arial\" size=\"8\" />\n";

			node = node + "\t\t\t<position>225 489 297 540</position>\n";

			node = node + "" +
					"\t\t</node>\n";

			updateXDSL(node);
		}

		//Last (final) text
		String last = "\t </genie>\n" +
				"\t</extensions>\n" +
				"</smile>";
		updateXDSL(last);

	}

	public ArrayList<String> sort(ArrayList<String> nodes){
		ArrayList<String> ordered = new ArrayList<String>();
		ArrayList<String> pool = new ArrayList<String>();

		for(int i = 0; i < nodes.size(); i++){
			String[] parents = Tools.parseXML(nodes.get(i), "<parents>", "</parents>");
			if(parents.length == 0){
				String[] name = Tools.parseXML(nodes.get(i), "<cpt id=\"", "\">");
				ordered.add(nodes.get(i));
				pool.add(name[0]);
			}
		}


		for(int i = 0; i < nodes.size(); i++){
			for(int j = 0; j < nodes.size(); j++){
				String[] parentline = Tools.parseXML(nodes.get(j), "<parents>", "</parents>");
				String[] name = Tools.parseXML(nodes.get(j), "<cpt id=\"", "\">");
				if(!pool.contains(name[0]) && parentline.length > 0){
					boolean restriction = true;
					String[] parents = parentline[0].split(" ");
					for(int k = 0; k < parents.length; k++){
						if(!pool.contains(parents[k])){
							restriction = false;
						}
					}
					if(restriction == true){
						ordered.add(nodes.get(j));
						pool.add(name[0]);
					}
				}
			}
		}


		return ordered;
	}

	public String getNode(String variable, String definitions[]){
		String node = "";
		//System.out.println(nodes[i].toString());

		String[] id_code = Tools.parseXML(variable, "<NAME>", "</NAME>");

		node = node +"\t<cpt id=\""+id_code[0]+"\">\n";
		String[] states = Tools.parseXML(variable, "<OUTCOME>", "</OUTCOME>");
		for(int j = 0; j < states.length; j++){
			node = node + "\t\t<state id=\""+states[j]+"\" />\n";
		}

		//Add parents and search probabilities
		for(int j = 0; j < definitions.length; j++){
			String[] nameFOR = Tools.parseXML(definitions[j], "<FOR>", "</FOR>");
			if(nameFOR[0].equals(id_code[0])){

				String[] parents = Tools.parseXML(definitions[j], "<GIVEN>", "</GIVEN>");

				if(parents.length>0){
					node = node + "\t\t<parents>";
					for(int k = 0; k < parents.length; k++){
						node = node + parents[k]+" ";
					}
					node = node + "</parents>\n";
				}

				String[] probabilities = Tools.parseXML(definitions[j], "<TABLE>", "</TABLE");
				String prob = probabilities[0].replaceAll("\n", "");


				node = node + "\t\t<probabilities>"+prob+"</probabilities>\n";
				node = node + "\t</cpt>\n";
			}
		}
		//Conclude the value of the node 
		return node;
	}


	public void runner(String inputFile, String outputDirectory, String hashFile){

		// GET INPUT DATA
		System.out.println("\n------------\n Reading file... ");
		System.out.println(inputFile);
		bif = FileManager.read(inputFile);
		if(hashFile != "empty"){
			useCodeDescriptions = true;
			System.out.println("\n------------\n Reading hash file... ");
			System.out.println(hashFile);
			codesTable = FileManager.readHashTable(hashFile);
		}

		// TRANSFORM TO XDSL
		System.out.println("\n------------\n Transforming... ");
		transformToXDSL();

		// CREATE OUTPUT FILE
		System.out.println("\n------------\n Creating file... ");
		String name[] = inputFile.split("\\.");
		String outputName = name[0] + ".xdsl";
		System.out.println(outputName);
		FileManager.write(outputName, xdsl);
		System.out.println("\n------------\nNew File created");
	}

}
