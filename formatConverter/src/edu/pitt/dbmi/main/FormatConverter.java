package edu.pitt.dbmi.main;

import edu.pitt.dbmi.dataset.ConvertARFFtoEBMC;
import edu.pitt.dbmi.dataset.ConvertBIFtoGENIE;
import edu.pitt.dbmi.dataset.ConvertCSVtoARFF;
import edu.pitt.dbmi.dataset.ConvertEAVtoARFF;
import edu.pitt.dbmi.dataset.ConvertWekaNBtoGENIE;
import edu.pitt.dbmi.dataset.UpdateUMLScodesinXDSL;
import edu.pitt.dbmi.tools.Util;
/**
 *
 * @author Arturo L�pez Pineda
 * 
 * email: arl68@pitt.edu
 * 
 */
public class FormatConverter {

	public static void showInfo(){
		System.out.println("formatConverter version 1.2");
		System.out.println("Expected commands format: -input data.arff [-target class] [-inputFormat eav|bif] [-output Desktop/results/] [-outputFormat ebmc|arff|xdsl]");
		System.out.println("   -input data.txt \t data.arff is the input file, or data.csv");
		System.out.println("   -target class \t the name of the target attribute. Default: class");
		System.out.println("   -inputFormat eav \t the format of the input data. Default: file extension");
		System.out.println("   -output Desktop/results/ \t The output Path where the new files are going to be created. Default is the same as input");
		System.out.println("   -outputFormat ebmc \t Create the file(s) speficied by the new format. Default is arff");
		System.out.println("   -discretize 0.5 \t Discretize all columns using the cutoff point given. Default is not discretized");
		System.out.println("   -transpose true \t Transpose the values, rows to columns and columns to rows. Default is false");
		System.out.println("   -hashfile filename.csv \t A .csv file with the codes and descriptions");
		System.out.println("   -------");
		System.out.println("   ARFF to EBMC");
		System.out.println("   BIF to XDSL | NET");
		System.out.println("   NBweka to XDSL");
		System.out.println("   CSV to ARFF");
		System.out.println("   EAV to ARFF");
	}
	
	
	public static void main(String args[]) {

		if((args.length != 2) && (args.length != 4) && (args.length != 6) && (args.length != 8) && (args.length != 10) && (args.length != 12)){
			System.err.println("Incorrect arguments.");
			showInfo();
			System.exit(1);
		}


		String inputFile = "";
		String inputFormat = "";
		String outputPath = "";
		String outputFormat = "arff";
		String target = "class";
		String hashFile = "empty";
		boolean discretize = false;
		double discretizeValue = 0.5;
		boolean transpose = false;


		for(int i = 0; i < args.length; i++){
			if(args[i].equalsIgnoreCase("-input")){
				inputFile = args[(1+i)];
			}
			else if(args[i].equalsIgnoreCase("-target")){
				target = args[(1+i)];
			}
			else if(args[i].equalsIgnoreCase("-output")){
				outputPath = args[++i];
			}
			else if(args[i].equalsIgnoreCase("-inputFormat")){
				if(args[(1+i)].equalsIgnoreCase("eav")){
					inputFormat = "eav";
				}
				else if(args[(1+i)].equalsIgnoreCase("bif")){
					inputFormat = "bif";
				}
				else if(args[(1+i)].equalsIgnoreCase("nbweka")){
					inputFormat = "nbweka";
				}
			}
			else if(args[i].equalsIgnoreCase("-discretize")){
					discretizeValue = Double.parseDouble(args[i+1]);
			}
			else if(args[i].equalsIgnoreCase("-discretize")){
				if(args[(1+i)].equalsIgnoreCase("true") | args[(1+i)].equalsIgnoreCase("t")){
				transpose = true;
				}
				else if(args[(1+i)].equalsIgnoreCase("false") | args[(1+i)].equalsIgnoreCase("f")){
					transpose = false;
					}
			}
			else if(args[i].equalsIgnoreCase("-outputFormat")){
				if(args[(1+i)].equalsIgnoreCase("ebmc")){
					outputFormat = "ebmc";
				}
				else if(args[(1+i)].equalsIgnoreCase("arff")){
					outputFormat = "arff";
				}
				else if(args[(1+i)].equalsIgnoreCase("umls")){
					outputFormat = "umls";
				}
				else if(args[(1+i)].equalsIgnoreCase("xdsl")){
					outputFormat = "xdsl";
				}
				else if(args[(1+i)].equalsIgnoreCase("net")){
					outputFormat = "net";
				}
			}
			else if(args[i].equalsIgnoreCase("-hashFile")){
				hashFile = args[(1+i)];
			}
		}

		if(inputFormat.equals("")){
			inputFormat = Util.fileNameStemAndSuffix(inputFile, ".")[1];
		}
		if(outputPath.equals("")){
			outputPath = Util.fileNameStemAndSuffix(inputFile, "/")[0];
			//outputPath = (new File(inputFile)).getParent();
		}

		if(inputFormat.equalsIgnoreCase("arff") && outputFormat.equalsIgnoreCase("ebmc")){
			System.out.println("ARFFtoEBMC");
			System.out.println("inputFile = "+inputFile);
			System.out.println("outputPath = "+outputPath);
			System.out.println("----");
			ConvertARFFtoEBMC conv = new ConvertARFFtoEBMC();
			conv.runner(inputFile, outputPath);
		}
		else if(inputFormat.equalsIgnoreCase("eav") && outputFormat.equalsIgnoreCase("arff")){
			System.out.println("EAVtoARFF");
			System.out.println("input = "+inputFile);
			System.out.println("target = "+target);
			System.out.println("output = "+outputPath);
			System.out.println("----");
			ConvertEAVtoARFF conv = new ConvertEAVtoARFF();
			conv.runner(inputFile, target, outputPath);
		}
		else if(inputFormat.equalsIgnoreCase("xdsl") && outputFormat.equalsIgnoreCase("umls")){
			System.out.println("Update UMLS codes in XDSL");
			System.out.println("input = "+inputFile);
			System.out.println("target = "+target);
			System.out.println("output = "+outputPath);
			System.out.println("hashFile = "+hashFile);
			System.out.println("----");
			UpdateUMLScodesinXDSL conv = new UpdateUMLScodesinXDSL();
			conv.runner(inputFile, target, outputPath, hashFile);
		}
		else if(inputFormat.equalsIgnoreCase("csv") && outputFormat.equalsIgnoreCase("arff")){
			System.out.println("CSVtoARFF");
			System.out.println("input = "+inputFile);
			System.out.println("target = "+target);
			System.out.println("output = "+outputPath);
			System.out.println("----");
			ConvertCSVtoARFF conv = new ConvertCSVtoARFF();
			conv.runner(inputFile, outputPath);
		}
		else if(inputFormat.equalsIgnoreCase("bif") && (outputFormat.equalsIgnoreCase("xdsl") || outputFormat.equalsIgnoreCase("net"))){
			System.out.println("BIFtoGENIE");
			System.out.println("input = "+inputFile);
			System.out.println("target = "+target);
			System.out.println("output = "+outputPath);
			System.out.println("hashFile = "+hashFile);
			System.out.println("----");
			ConvertBIFtoGENIE conv = new ConvertBIFtoGENIE();
			conv.runner(inputFile, outputPath, hashFile, outputFormat);
		}
		else if(inputFormat.equalsIgnoreCase("nbweka") && (outputFormat.equalsIgnoreCase("xdsl") || outputFormat.equalsIgnoreCase("net"))){
			System.out.println("NBweka-to-GENIE");
			System.out.println("input = "+inputFile);
			System.out.println("target = "+target);
			System.out.println("output = "+outputPath);
			System.out.println("hashFile = "+hashFile);
			System.out.println("----");
			ConvertWekaNBtoGENIE conv = new ConvertWekaNBtoGENIE();
			conv.runner(inputFile, outputPath, hashFile, outputFormat);
		}
		else{
			System.out.println("inputFormat: "+inputFormat+" - outputformat: "+outputFormat);
			System.out.println("Review your arguments");
			showInfo();
		}
	}
	

}
