package edu.pitt.dbmi.dataset;

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


	public void transformToXDSL(){


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
		for(int i = 0; i < linesAll.length; i++){
			if(linesAll[i].equals("Naive Bayes Classifier")){
				copy=true;
			}
			else if(linesAll[i].contains("Time taken to build model")){
				copy=false;
			}
			if(copy == true){
				lines.add(linesAll[i]);
				System.out.println(linesAll[i]);
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
					node = node + "\t\t<state id=\""+nodeStates.get(j)[0]+"\" />\n";
				}


				node = node + "\t\t<parents>"+target+"</parents>\n";
				node = node + "\t\t<probabilities> ";

				//add probabilities
				for(int k = 1; k < nodeStates.get(0).length; k++){
					for(int j = 0; j < nodeStates.size()-1; j++){
						double probInd= Double.parseDouble(nodeStates.get(j)[k]);
						double probTotal=Double.parseDouble(nodeStates.get(nodeStates.size()-1)[k]);
						String prob = ""+probInd/probTotal;
						node = node + prob + " ";
					}
				}

				node = node + "</probabilities>\n\t</cpt>\n";

			}
		}
		out += node;

		//------------------------------
		//5. Middle Text
		String middle = "\t</nodes>\n" +
				"\t<extensions>\n" +
				"\t <genie version=\"1.0\" app=\"GeNIe 2.0.2603.0\" name=\"Flu\" faultnameformat=\"nodestate\">\n";

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


	public String getUMLS(String disease){
		String umls = "";

		String[][] list = new String[75][2];

		list[1][0]="C1883552"; list[1][1]="Weakness";
		list[2][0]="C0036082"; list[2][1]="Saline Solution";
		list[3][0]="C0015672"; list[3][1]="Fatigue";
		list[4][0]="C0015967"; list[4][1]="Fever";
		list[5][0]="BC0021400"; list[5][1]="Influenza";
		list[6][0]="C0003123"; list[6][1]="Anorexia";
		list[7][0]="C0013404"; list[7][1]="Dyspnea";
		list[8][0]="C0003862"; list[8][1]="Arthralgia";
		list[9][0]="C0018681"; list[9][1]="Headache";
		list[10][0]="C0008031"; list[10][1]="Chest Pain";
		list[11][0]="C0000729"; list[11][1]="Abdominal Cramps";
		list[12][0]="C0232498"; list[12][1]="Abdominal tenderness";
		list[13][0]="C0235592"; list[13][1]="Cervical lymphadenopathy";
		list[14][0]="C0010200"; list[14][1]="Coughing";
		list[15][0]="C0010520"; list[15][1]="Cyanosis";
		list[16][0]="C0027497"; list[16][1]="Nausea";
		list[17][0]="C0085593"; list[17][1]="Chills";
		list[18][0]="C0043144"; list[18][1]="Wheezing";
		list[19][0]="C0032285"; list[19][1]="Pneumonia";
		list[20][0]="C0011991"; list[20][1]="Diarrhea";
		list[21][0]="C0231528"; list[21][1]="Myalgias";
		list[22][0]="C0009319"; list[22][1]="Colitis";
		list[23][0]="C0011175"; list[23][1]="Dehydration";
		list[24][0]="C0242429"; list[24][1]="Sore Throat";
		list[25][0]="C0009951"; list[25][1]="Convulsions";
		list[26][0]="C0202010"; list[26][1]="Fecal analysis procedure";
		list[27][0]="C0151594"; list[27][1]="Hemorrhagic diarrhea";
		list[28][0]="C0231218"; list[28][1]="Malaise";
		list[29][0]="C1260880"; list[29][1]="Rhinorrhea";
		list[30][0]="C0009763"; list[30][1]="Conjunctivitis";
		list[31][0]="IC0021400"; list[31][1]="Suspected Flu";
		list[32][0]="C1321898"; list[32][1]="Blood in stool";
		list[33][0]="C0201811"; list[33][1]="Fecal occult blood test";
		list[34][0]="C0016479"; list[34][1]="Food Poisoning";
		list[35][0]="C0019079"; list[35][1]="Hemoptypsis";
		list[36][0]="C0017160"; list[36][1]="Gastroenteritis";
		list[37][0]="C0239430"; list[37][1]="Pain with eye movement";
		list[38][0]="C0085636"; list[38][1]="Photophobia";
		list[39][0]="C0521026"; list[39][1]="Viral";
		list[40][0]="C0232726"; list[40][1]="Rectal tenesmus";
		list[41][0]="C0004610"; list[41][1]="Bacteremia";
		list[42][0]="C0019825"; list[42][1]="Hoarseness";
		list[43][0]="C0267596"; list[43][1]="Rectal hemorrhage";
		list[44][0]="C0042740"; list[44][1]="Viral syndrome";
		list[45][0]="LC0021400"; list[45][1]="Lab confirmed flu";
		list[46][0]="C0521839"; list[46][1]="Influenza-like illness";
		list[47][0]="IC0013371"; list[47][1]="Suspected Shigella Infections";
		list[48][0]="C0558348"; list[48][1]="Bacterial gastroenteritis";
		list[49][0]="C0420679"; list[49][1]="Nasal swab taken";
		list[50][0]="C0074447"; list[50][1]="Shiga Toxin";
		list[51][0]="C0025289"; list[51][1]="Meningitis";
		list[52][0]="C0042963"; list[52][1]="Vomiting";
		list[53][0]="null"; list[53][1]="meningitis  meningismus"; //C0025289
		list[54][0]="C0000737"; list[54][1]="Abdominal Pain";
		list[55][0]="C0013604"; list[55][1]="Edema";
		list[56][0]="C0497156"; list[56][1]="Lymphadenopathy";
		list[57][0]="C0004093"; list[57][1]="Asthenia";
		list[58][0]="C0850149"; list[58][1]="Dry cough";
		list[59][0]="C2029900"; list[59][1]="Fast heart rate (symptom)";
		list[60][0]="C0034642"; list[60][1]="Rales";
		list[61][0]="C0009443"; list[61][1]="Common Cold";
		list[62][0]="C0039070"; list[62][1]="Syncope";
		list[63][0]="C0038450"; list[63][1]="Stridor";
		list[64][0]="C0239182"; list[64][1]="Watery diarrhoea";
		list[65][0]="C0018932"; list[65][1]="Hematochezia";
		list[66][0]="C0004611"; list[66][1]="Bacteria";
		list[67][0]="C0011168"; list[67][1]="Deglutition Disorders";
		list[68][0]="C1971624"; list[68][1]="Loss of appetite (finding)";
		list[69][0]="C0237849"; list[69][1]="Peeling of skin";
		list[70][0]="C0009676"; list[70][1]="Confusion";
		list[71][0]="C0014038"; list[71][1]="Encephalitis";
		list[72][0]="C0235710"; list[72][1]="Chest discomfort";
		list[73][0]="C0020649"; list[73][1]="Hypotension";
		list[74][0]="C0028081"; list[74][1]="Night sweats";


		for(int i = 0; i < list.length; i++){
			if(disease.equals(list[i][0])){
				umls = list[i][1];
				break;
			}
		}

		return umls;
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
			transformToXDSL();
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
