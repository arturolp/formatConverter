package edu.pitt.dbmi.dataset;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import edu.pitt.dbmi.tools.FileManager;
import edu.pitt.dbmi.tools.Tools;



public class ConvertWekaNBtoGENIE {
	public String inputName = "";
	public String nbweka = "";
	public Hashtable<String, String> codesTable;
	public boolean useCodeDescriptions = false;
	public String out = "";


	public void transformToXDSL(String fileName){


		//------------------------------
		//1.ADD header
		out = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
				" <smile version=\"1.0\" id=\"Network1\" numsamples=\"1000\">\n" +
				"\t<nodes>\n";


		//------------------------------
		//2. Get all lines and trim
		String target = "class";
		String[] linesAll = nbweka.split("\n");
		ArrayList<String> lines = new ArrayList<String>();

		boolean copy = false;
		int empty = 0;
		for(int i = 0; i < linesAll.length; i++){
			if(linesAll[i].equals("Naive Bayes Classifier")){
				copy=true;
			}
			if(linesAll[i].equals("")){
				empty++;
				if(empty == 2){
					copy = false;
				}
			}
			else if(!linesAll[i].equals("")){
				empty=0;
			}
			else if(copy == true && linesAll[i].contains("Time taken to build model")){
				copy=false;
			}
			if(copy == true){
				lines.add(linesAll[i]);
				//System.out.println(linesAll[i]);
			}
		}

		//------------------------------
		//3. FOR TARGET NODE
		String[] targetClasses = Tools.innerTrim(lines.get(3)).split(" ");
		String targetNode = "\t<cpt id=\""+target+"\">\n";
		//System.out.println("target = "+targetClasses);
		for(int j = 1; j < targetClasses.length; j++){
			targetNode = targetNode + "\t\t<state id=\""+targetClasses[j]+"\" />\n";
		}
		String[] targetCPTs = Tools.innerTrim(lines.get(4)).split(" ");
		targetNode = targetNode + "\t\t<probabilities> ";
		for(int j = 0; j < targetCPTs.length; j++){
			String prob = Tools.parseXML(targetCPTs[j], "(", ")")[0];
			targetNode = targetNode + prob+" ";
		}
		targetNode = targetNode + "</probabilities>\n\t</cpt>\n";

		//System.out.println(targetNode);
		out += targetNode;

		//------------------------------
		//4. FOR THE REST OF NODES
		ArrayList<String> nodeIDs = new ArrayList<String>();

		String node = "";
		for(int i = 6; i < lines.size(); i++){ //the nodes start at line 6
			if(!lines.get(i).equals("")){


				String id_code = lines.get(i).trim();
				id_code = id_code.replace(".", "_");
				nodeIDs.add(id_code);
				node = node + "\t<cpt id=\""+id_code+"\">\n";
				i++;
				ArrayList<String[]> nodeStates = new ArrayList<String[]>();

				String[] state = Tools.innerTrim(lines.get(i)).split(" ");
				do{
					nodeStates.add(state);
					i++;
					state = Tools.innerTrim(lines.get(i)).split(" ");
				}while(!state[0].equals("[total]"));
				nodeStates.add(state);
				i++;

				//add states

				for(int j = 0; j < nodeStates.size()-1; j++){
					String stateName = getGenieCanonicalName(nodeStates.get(j)[0]);
					node = node + "\t\t<state id=\""+stateName+"\" />\n";
				}
				boolean dummy = false;
				if(nodeStates.size() <= 2){
					node = node + "\t\t<state id=\"DummyState\" />\n";
					dummy = true;
				}


				node = node + "\t\t<parents>"+target+"</parents>\n";
				node = node + "\t\t<probabilities> ";

				//get probabilities
				for(int k = 1; k < nodeStates.get(0).length; k++){
					Double probs[] = new Double[nodeStates.size()-1];
					double probTotal=Double.parseDouble(nodeStates.get(nodeStates.size()-1)[k]);
					double sumProbs = 0;
					int indexMax = 0;
					double maxProb = 0;
					for(int j = 0; j < nodeStates.size()-1; j++){
						double probCount= Double.parseDouble(nodeStates.get(j)[k]);
						double prob = probCount/probTotal;

						//BigDecimal bd = new BigDecimal(prob).setScale(11, RoundingMode.HALF_EVEN);

						//DecimalFormat df = new DecimalFormat("#.###########");
						//df.setRoundingMode(RoundingMode.DOWN);


						//probs[j] = bd.doubleValue();
						//probs[j] = Double.parseDouble(df.format(prob));
						probs[j] = prob;

						String stateName = getGenieCanonicalName(nodeStates.get(j)[0]);
						//System.out.println(stateName+": "+probCount+" / "+probTotal+" = "+probs[j]);

						sumProbs += probs[j];
						if(maxProb <= probs[j]){
							indexMax = j;
						}
					}


					double difference = 1-sumProbs;
					//System.out.println("difference: "+difference);

					//System.out.println("***"+difference);

					probs[indexMax] += difference;


					//double check = 0;
					for(int j = 0; j < probs.length; j++){
						//check += probs[j];
						node = node + probs[j] + " ";
					}
					if(dummy == true){
						for(int j = 0; j < probs.length; j++){
							node = node + "0.0 ";
						}
					}
					//System.out.println("**SumProbs: "+sumProbs + ", Difference: "+difference+", Check: "+(check==1));
					//System.out.println("Check: "+(check==1));

				}

				node = node + "</probabilities>\n\t</cpt>\n";

			}
		}
		out += node;

		//------------------------------
		//5. Middle Text
		String middle = "\t</nodes>\n" +
				"\t<extensions>\n" +
				"\t <genie version=\"1.0\" app=\"GeNIe 2.0.2603.0\" name=\""+fileName+"\" faultnameformat=\"nodestate\">\n";

		out += middle;


		//------------------------------
		//6. Update nodes
		targetNode = "\t\t<node id=\""+target+"\">\n" +
				"\t\t\t<name>"+target+"</name>\n" +
				"\t\t\t<interior color=\"e5f6f7\" />\n" +
				"\t\t\t<outline color=\"000080\" />\n" +
				"\t\t\t<font color=\"000000\" name=\"Arial\" size=\"8\" />\n";

		targetNode = targetNode + "\t\t\t<position>225 489 297 540</position>\n";

		//s = "\t\t\t<position>225 489 297 540</position>\n";
		targetNode = targetNode + "\t\t</node>\n";
		out += targetNode;

		for(int i = 0; i < nodeIDs.size(); i++){

			String name = nodeIDs.get(i);


			node = "\t\t<node id=\""+nodeIDs.get(i)+"\">\n" +
					"\t\t\t<name>"+name+"</name>\n" +
					"\t\t\t<interior color=\"e5f6f7\" />\n" +
					"\t\t\t<outline color=\"000080\" />\n" +
					"\t\t\t<font color=\"000000\" name=\"Arial\" size=\"8\" />\n";

			node = node + "\t\t\t<position>225 489 297 540</position>\n";

			//s = "\t\t\t<position>225 489 297 540</position>\n";


			node = node + "\t\t</node>\n";

			out += node;
		}

		//------------------------------
		//7. Last (final) text
		String last = "\t </genie>\n" +
				"\t</extensions>\n" +
				"</smile>";
		out += last;





	}


	private String getGenieCanonicalName(String oldName) {
		String canonical = "";


		if(!oldName.substring(0, 1).matches("[a-zA-Z]")){

			canonical += "x_"+ oldName;
			canonical = canonical.replace(".", "_");
			canonical = canonical.replace("-", "_");
			canonical = canonical.replace("(", "");
			canonical = canonical.replace(")", "");
			canonical = canonical.replace("[", "");
			canonical = canonical.replace("]", "");
			canonical = canonical.replace("\\", "");
			canonical = canonical.replace("'", "");

			//System.out.println(canonical);

		}
		else{
			canonical = oldName;
		}

		return canonical;
	}


	public void runner(String inputFile, String outputPath, String hashFile, String outputFormat){


		// Read Input File
		System.out.println("\n------------\n Reading file... ");
		System.out.println(inputFile);
		inputName = inputFile;
		nbweka = FileManager.read(inputFile);


		if(hashFile != "empty"){
			useCodeDescriptions = true;
			System.out.println("\n------------\n Reading hash file... ");
			System.out.println(hashFile);
			codesTable = FileManager.readHashTable(hashFile);
		}


		// TRANSFORM TO GENIE
		System.out.println("\n------------\n Transforming... ");
		if(outputFormat.equals("xdsl")){
			transformToXDSL(inputFile);
		}
		else if(outputFormat.equals("net")){
			//TODO: transformToNET();
		}

		// CREATE OUTPUT FILE
		System.out.println("\n------------\n Creating file... ");
		String name[] = inputFile.split("\\.");
		String outputName = name[0] + "."+outputFormat;
		System.out.println(outputName);
		FileManager.write(outputName, out);
		System.out.println("\n------------\nNew File created");

	}

}
