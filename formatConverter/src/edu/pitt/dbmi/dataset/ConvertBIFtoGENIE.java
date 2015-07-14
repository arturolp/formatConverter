package edu.pitt.dbmi.dataset;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.pitt.dbmi.tools.FileManager;
import edu.pitt.dbmi.tools.Tools;

public class ConvertBIFtoGENIE {
	public String bif = "";
	public Hashtable<String, String> codesTable;
	public String hash = "";
	public String inputName = "";
	public String out = "";
	public boolean useCodeDescriptions = false;

	public String getNodeXDSL(String variable, String definitions[]){
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
	
	public String getNodeNET(String variable){
		String node = "node ";
		//System.out.println(nodes[i].toString());

		String[] id_code = Tools.parseXML(variable, "<NAME>", "</NAME>");

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
		node = node +id_code[0]+"\n{\n\tlabel = \""+name+"\";\n";
		
		node = node + "\tposition = (74 278);\n";
		node = node + "\tstates = (";
		String[] states = Tools.parseXML(variable, "<OUTCOME>", "</OUTCOME>");
		for(int j = 0; j < states.length-1; j++){
			node = node + "\""+states[j]+"\" ";
		}
		node = node + "\""+ states[states.length-1]+"\");\n";
		node = node + "}\n\n";

	
		return node;
	}

	public Hashtable<String, Boolean> isConnectedXDSL(ArrayList<String> nodes){

		Hashtable<String, Boolean> keep = new Hashtable<String, Boolean>(nodes.size());
		for(int i = 0; i < nodes.size(); i++){
			String iname = Tools.parseXML(nodes.get(i), "<cpt id=\"", "\">")[0];
			keep.put(iname, false);
		}


		for(int i = 0; i < nodes.size(); i++){
			String[] iparents = Tools.parseXML(nodes.get(i), "<parents>", "</parents>");
			String iname = Tools.parseXML(nodes.get(i), "<cpt id=\"", "\">")[0];
			if(iparents.length > 0){
				keep.put(iname, true); //because it has parents

				String[] kparents = iparents[0].split(" ");
				for(int k = 0; k < kparents.length; k++){
					keep.put(kparents[k], true); //because it has children
				}
			}
		}





		return keep;
	}



	public ArrayList<String> sortNodeXDSL(ArrayList<String> nodes){
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

	private void transformToNET() {

		//ADD header
		updateOut("net \n{\n\tnode_size = (76 36);\n}\n\n");

		//Create Nodes
		String[] variables = Tools.parseXML(bif, "<VARIABLE TYPE=\"nature\">", "</VARIABLE>");
		String[] definitions = Tools.parseXML(bif, "<DEFINITION>", "</DEFINITION>");

		//for each node add values
		ArrayList<String> nodes = new ArrayList<String>();
		ArrayList<String> potentials = new ArrayList<String>();
		for(int i = 0; i < variables.length; i++){
			nodes.add(getNodeNET(variables[i]));
			//System.out.println(nodes.get(i));
		}
		for(int i = 0; i < definitions.length; i++){
			potentials.add(getPotentialNET(definitions[i]));
			//System.out.println(potentials.get(i));
		}
		
		Hashtable<String, Boolean> keep = isConnectedNET(definitions);


		//Update nodes
		for(int i = 0; i < nodes.size(); i++){
			String iname = nodes.get(i).split("\n")[0];
			iname = iname.substring(5, iname.length());
			if(keep.get(iname) == true){
				updateOut(nodes.get(i));
			}
		}
		
		//Update potentials
		for(int i = 0; i < potentials.size(); i++){
			String iname = potentials.get(i).split("\n")[0];
			iname = Tools.parseXML(iname, "potential (", " |")[0];
			if(keep.get(iname) == true){
				updateOut(potentials.get(i));
			}
		}
	}


	private String getPotentialNET(String definition) {
		String potential = "potential (";
		//System.out.println(nodes[i].toString());

		String[] id_code = Tools.parseXML(definition, "<FOR>", "</FOR>");

		
		potential = potential +id_code[0] + " | ";
		
		String[] parents = Tools.parseXML(definition, "<GIVEN>", "</GIVEN>");
		for(int j = 0; j < parents.length-1; j++){
			potential = potential +parents[j]+" ";
		}
		if(parents.length>0){
			potential = potential + parents[parents.length-1]+")\n{\n";
		}
		else{
			potential = potential + ")\n{\n";
		}
		
		potential = potential + "\tdata = (";

		String[] data = Tools.parseXML(definition, "<TABLE>", "</TABLE>");
		String[] table = data[0].split("\n");
		for(int j = 1; j < table.length; j++){
			String subtable = table[j].substring(0,table[j].length()-1);
			potential = potential + "(" + subtable + ")";
			if(j!=table.length-1){
				potential = potential + "\n\t";
			}
		}
		
		potential = potential + ");\n}\n\n";
	
		return potential;
	}

	private Hashtable<String, Boolean> isConnectedNET(String[] definitions) {
		Hashtable<String, Boolean> keep = new Hashtable<String, Boolean>(definitions.length);
		for(int i = 0; i < definitions.length; i++){
			String[] iname = Tools.parseXML(definitions[i], "<FOR>", "</FOR>");
			keep.put(iname[0], false);
		}
		
		
		for(int i = 0; i < definitions.length; i++){
			String[] iname = Tools.parseXML(definitions[i], "<FOR>", "</FOR>");
			String[] iparents = Tools.parseXML(definitions[i], "<GIVEN>", "</GIVEN>");

			if(iparents.length > 0){
				keep.put(iname[0], true); //because it has parents

				for(int k = 0; k < iparents.length; k++){
					keep.put(iparents[k], true); //because it has children
				}
			}
		}
		
		
		return keep;
	}

	public void transformToXDSL(){

		//ADD header
		out = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
				" <smile version=\"1.0\" id=\""+inputName+"\">\n" +
				"\t<nodes>\n";

		//Create Nodes
		String[] variables = Tools.parseXML(bif, "<VARIABLE TYPE=\"nature\">", "</VARIABLE>");
		String[] definitions = Tools.parseXML(bif, "<DEFINITION>", "</DEFINITION>");

		//for each node add values
		ArrayList<String> nodes = new ArrayList<String>();
		for(int i = 0; i < variables.length; i++){
			nodes.add(getNodeXDSL(variables[i], definitions));
			//System.out.println(nodes.get(i));
		}


		Hashtable<String, Boolean> keep = isConnectedXDSL(nodes);

		ArrayList<String> connected = new ArrayList<String>();

		for(int i = 0; i < nodes.size(); i++){
			String iname = Tools.parseXML(nodes.get(i), "<cpt id=\"", "\">")[0];
			if(keep.get(iname) == true){
				connected.add(nodes.get(i));
			}
		}

		ArrayList<String> sorted = sortNodeXDSL(connected);

		for(int i = 0; i < sorted.size(); i++){
			updateOut(sorted.get(i));
		}

		//Middle Text
		String middle = "\t</nodes>\n" +
				"\t<extensions>\n" +
				"\t <genie version=\"1.0\" app=\"GeNIe 2.0.2603.0\" name=\"Flu\" faultnameformat=\"nodestate\">\n";

		updateOut(middle);


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

			if(keep.get(id_code[0]) == true){
				node = "\t\t<node id=\""+id_code[0]+"\">\n" +
						"\t\t\t<name>"+name+"</name>\n" +
						"\t\t\t<interior color=\"e5f6f7\" />\n" +
						"\t\t\t<outline color=\"000080\" />\n" +
						"\t\t\t<font color=\"000000\" name=\"Arial\" size=\"8\" />\n";
				node = node + "\t\t\t<position>225 489 297 540</position>\n";
				node = node + "" +
						"\t\t</node>\n";
				updateOut(node);
			}
		}

		//Last (final) text
		String last = "\t </genie>\n" +
				"\t</extensions>\n" +
				"</smile>";
		updateOut(last);

	}

	public void updateOut(String s){
		out = out + s;
	}

	public void runner(String inputFile, String outputDirectory, String hashFile, String format){

		// GET INPUT DATA
		System.out.println("\n------------\n Reading file... ");
		System.out.println(inputFile);
		inputName = inputFile;
		bif = FileManager.read(inputFile);
		bif = bif.replace("&apos;", "");
		if(hashFile != "empty"){
			useCodeDescriptions = true;
			System.out.println("\n------------\n Reading hash file... ");
			System.out.println(hashFile);
			codesTable = FileManager.readHashTable(hashFile);
		}

		// TRANSFORM TO XDSL
		System.out.println("\n------------\n Transforming... ");
		if(format.equals("xdsl")){
			transformToXDSL();
		}
		else if(format.equals("net")){
			transformToNET();
		}

		// CREATE OUTPUT FILE
		System.out.println("\n------------\n Creating file... ");
		String name[] = inputFile.split("\\.");
		String outputName = name[0] + "."+format;
		System.out.println(outputName);
		FileManager.write(outputName, out);
		System.out.println("\n------------\nNew File created");
	}

	
	
}
