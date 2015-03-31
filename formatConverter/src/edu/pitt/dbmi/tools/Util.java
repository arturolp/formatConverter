package edu.pitt.dbmi.tools;



//import corefiles.structures.data.dataset.attribute.*;
//import corefiles.structures.results.Predictions.BayesPrediction;
//import weka.classifiers.Evaluation;
//import weka.classifiers.evaluation.NominalPrediction;

//import weka.core.Attribute;
//import weka.core.FastVector;
//import weka.core.Instance;
//import weka.core.Instances;

public class Util {
	public static String FILE_PATH_SEP = System.getProperty("file.separator");

	
	
	public static String guessSeparator(String line) {
		int nCommas = 0;
		int nTabs = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) ==  ',') {
				nCommas++;
			} else if (line.charAt(i) == '	') {
				nTabs++;
			}
		}
		return (nTabs < nCommas) ? "," : "	"; 
	}	

	public static String trimDirName(String dirName) {
		String newDirName = dirName.trim();
		while (newDirName.endsWith(FILE_PATH_SEP)) {
			newDirName = newDirName.substring(0, newDirName.length() - FILE_PATH_SEP.length());
		}
		return newDirName;
	}

	public static String appendFileNameSuffix(String fileName, String suffix) {
		if (suffix == null || suffix == "")
			return fileName;
		String[] name = fileNameStemAndSuffix(fileName, ".");
		//return name[0] + "." + suffix + "." + name[1];
		String ret = "";
		if (name[0] != null && ! name[0].equals(""))
			ret += name[0]+ ".";
		ret += suffix;
		if (name[1] != null && ! name[1].equals(""))
			ret += "." + name[1];
		return ret;
	}
	
	public static String insertFileNamePrefix(String fileName, String prefix, String outputDirectory) {
		if (prefix == null || prefix == "")
			return fileName;
		String[] name = fileNameStemAndSuffix(fileName, "/");
		String ret = "";
		if (name[0] != null && ! name[0].equals("")){
			if(outputDirectory.equals("")){
			ret += name[0] + "/";
			}
			else{
				ret += outputDirectory;
			}
		}
		ret += prefix;
		if (name[1] != null && ! name[1].equals("")){
			ret += name[1];
			}
		return ret;
	}
	
	public static String replaceFileNameSuffix(String fileName, String suffix) {
		String[] name = fileNameStemAndSuffix(fileName, ".");
		return name[0] + "." + suffix;
	}
	
	public static String getFileName(String file) {
		return Util.fileNameStemAndSuffix(file, "/")[1];
	}
	
	public static String[] fileNameStemAndSuffix(String fileName, String delimiter) {
		String[] ret = new String[2];
		int i = fileName.lastIndexOf(delimiter);
		if (i >= 0) {
			ret[0] = fileName.substring(0, i);
			ret[1] = fileName.substring(i + 1, fileName.length());
		} else {
			ret[0] = fileName;
			ret[1] = "";
		}
		return ret;
	}
	
	public static String timeSince(long millis) {
		millis = System.currentTimeMillis() - millis;
		int seconds = (int) (millis / 1000);
		int days  = seconds / (60 * 60 * 24);
		seconds %= 60 * 60 * 24;
		int hours = seconds / (60 * 60);
		seconds %= 60 * 60;
		int minutes = seconds / 60;
		seconds %= 60;
		return 
				(days > 0 ? days + " d " : "")
				+ (hours > 0 ? hours + " h " : "")
				+ (minutes > 0 ? minutes + " m " : "")
				+ seconds + " s.";
	}
}