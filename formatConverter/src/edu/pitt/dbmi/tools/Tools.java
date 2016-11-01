package edu.pitt.dbmi.tools;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Vector;

import weka.core.Instance;
import weka.core.Instances;

public class Tools {

	//-- BigInteger solution.
	public int factorial(int n) {


		if (n < 0) {
			//throw new RuntimeException("-- ERROR: Underflow error in factorial");
			return 1;
		} else if (n > 20) {
			throw new RuntimeException("-- ERROR: Overflow error in factorial");
		} else if (n == 0) {
			return 1;
		} else {
			return n * factorial(n - 1);
		}

	}

	//------------------
	// Compute factorial
	//------------------
	public BigInteger factorialBigInt(int n) {
		BigInteger fact = BigInteger.ONE;
		for (int i = n; i > 1; i--) {
			fact = fact.add(new BigInteger(Integer.toString(i)));
		}

		return fact;
	}

	//-- BigInteger solution 2.
	public int factorialLog(int n) {
		return Math.round((float) Math.exp(factorialLog2(n)));
	}

	public double factorialLog2(int n) {
		double sum = 0;
		if (n > 0) {
			sum = Math.log(n) + factorialLog2(n - 1);
		}

		return sum;
	}

	public double factorialLn(int n) {
		double result = 0.0;
		for (int i = 1; i <= n; i++) {
			result += Math.log((double)i);
		}
		return Math.exp(result);
	}

	public int[][] toDoubleArray(Vector<Vector<Integer>> content) {
		int[][] intArray;
		int x = content.size();
		int y = ((Vector) content.get(0)).size();

		intArray = new int[x][y];

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				intArray[i][j] = Integer.parseInt(((Vector) content.get(i)).get(j).toString());
			}
		}

		return intArray;

	}

	public String toSeparatedValueString(String[][] content, String separated) {
		String exitString = "";


		for (int i = 0; i < content.length; i++) {
			for (int j = 0; j < content[i].length; j++) {
				if(j == content[i].length - 1){
					exitString = exitString + content[i][j] + "\n";
				}
				else{
					exitString = exitString + content[i][j] + separated;
				}
			}
		}
		return exitString;
	}

	public String toSeparatedValueString(String[] content, String separated) {
		String exitString = "";


		for (int i = 0; i < content.length; i++) {
			if(i == content.length - 1){
				exitString = exitString + content[i] + "\n";
			}
			else{
				exitString = exitString + content[i] + separated;
			}
		}


		return exitString;

	}

	public String[] getColumnArray(String[][] content, int column) {
		String[] exitString = new String[content.length];

		for (int i = 0; i < content.length; i++) {
			exitString[i] = content[i][column];
		}

		return exitString;
	}

	public int[] toArray(Vector<Integer> content) {
		int[] intArray;
		int x = content.size();

		intArray = new int[x];

		for (int i = 0; i < x; i++) {
			intArray[i] = Integer.parseInt((content.get(i)).toString());
		}

		return intArray;

	}

	public static String[] toStringArray(Vector<String> content) {
		String[] stringArray;
		int x = content.size();

		stringArray = new String[x];

		for (int i = 0; i < x; i++) {
			stringArray[i] = (content.get(i)).toString();
		}

		return stringArray;

	}    

	public Vector<Integer> toVector(int[] array) {
		Vector<Integer> intVector = new Vector<Integer>();

		for (int i = 0; i < array.length; i++) {
			intVector.add(array[i]);
		}

		return intVector;

	}

	public static void print(String obj) throws Exception {
		System.out.println("" + obj);
	}
	public void print(int[][] a) {
		if (a != null) {
			if (a.length > 0) {

				for (int i = 0; i < a.length; i++) {
					for (int j = 0; j < a[i].length; j++) {
						System.out.print("" + a[i][j] + " ");
					}
					System.out.println("");
				}
			} else {
				System.out.println("int[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
	}

	public void print(int[][] a, int maxRows) {
		if (a != null) {
			if (a.length > 0) {

				for (int i = 0; i < maxRows; i++) {
					for (int j = 0; j < a[i].length; j++) {
						System.out.print("" + a[i][j] + " ");
					}
					System.out.println("");
				}
			} else {
				System.out.println("int[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
	}

	public void print(int[] a) {
		if (a != null) {
			if (a.length > 0) {
				for (int i = 0; i < a.length; i++) {
					System.out.print("" + a[i] + " ");
				}
				System.out.println("");
			} else {
				System.out.println("int[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
	}

	public void print(double[] a) {
		if (a != null) {
			if (a.length > 0) {
				for (int i = 0; i < a.length; i++) {
					System.out.print("" + a[i] + " ");
				}
				System.out.println("");
			} else {
				System.out.println("double[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
	}

	public static void print(double[][] obj) throws Exception {
		for (int i = 0; i < obj.length; i++) {
			for (int j = 0; j < obj[i].length - 1; j++) {
				System.out.print(obj[i][j] + ", ");
				System.out.flush();
			}
			System.out.println(obj[i][obj[i].length - 1]);
		}
	}
	public static void print(String[][] a) {
		if (a != null) {
			if (a.length > 0) {

				for (int i = 0; i < a.length; i++) {
					for (int j = 0; j < a[i].length; j++) {
						System.out.print("" + a[i][j] + "\t");
					}
					System.out.println("");
				}
			} else {
				System.out.println("String[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
	}

	public static void print(String[] a) {
		if (a != null) {
			if (a.length > 0) {
				for (int i = 0; i < a.length; i++) {
					System.out.print("" + a[i] + " ");
				}
				System.out.println("");
			} else {
				System.out.println("String[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
	}

	public static String toString(ArrayList<String> a) {
		String s = "";
		if (a != null) {
			if (a.size() > 0) {
				for (int i = 0; i < a.size()-1; i++) {
					s = s + a.get(i) + ",";
				}
				s = s + a.get(a.size()-1);
			} else {
				System.out.println("String[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
		return s;
	}
	
	public static String toString(String[] a) {
		String s = "";
		if (a != null) {
			if (a.length > 0) {
				for (int i = 0; i < a.length-1; i++) {
					s = s + a[i] + " ";
				}
				s = s+a[a.length-1];
			} else {
				System.out.println("String[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
		return s;
	}

	public static String toString(double[] a) {
		String s = "";
		if (a != null) {
			if (a.length > 0) {
				for (int i = 0; i < a.length-1; i++) {
					s = s + a[i] + " ";
				}
				s = s+a[a.length-1];
			} else {
				System.out.println("String[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
		return s;
	}

	public static String toCSVString(double[][] obj, String head) throws Exception {
		String s = head+ "\n";
		for (int i = 0; i < obj.length; i++) {
			for (int j = 0; j < obj[i].length - 1; j++) {
				s = s + obj[i][j] + ",";
			}
			s = s + obj[i][obj[i].length - 1] + "\n";
		}
		return s;
	}

	public void print(Vector<Integer> a) {
		if (a != null) {
			if (a.size() > 0) {

				for (int i = 0; i < a.size(); i++) {
					System.out.print("" + a.get(i).toString() + " ");
				}
				System.out.println("");
			} else {
				System.out.println("null");
			}
		} else {
			System.out.println("-- ERROR: The vector has a null value");
		}
	}

	public void printVectorOfDoubleArrays(Vector<int [][]> a) {
		if (a != null) {
			if (a.size() > 0) {

				for (int i = 0; i < a.size(); i++) {
					System.out.println("-"+i+"-");
					print(a.get(i).clone());
				}
				System.out.println("");
			} else {
				System.out.println("null");
			}
		} else {
			System.out.println("-- ERROR: The vector has a null value");
		}
	}

	public void printDoubleVector(Vector<Vector<String>> a) {

		if (a != null) {
			if (a.size() > 0) {

				for (int i = 0; i < a.size(); i++) {

					if(a.get(i).size() > 0){

						for(int j = 0; j < a.get(i).size(); j++)
							System.out.print("" + a.get(i).get(j).toString() + " \t");
					}

					System.out.println("");
				}
			} else {
				System.out.println("null");
			}
		} else {
			System.out.println("-- ERROR: The vector has a null value");
		}
	}

	public void printNetwork(Vector[] a) {
		if (a != null) {
			if (a.length > 0) {
				//System.out.println("a.length:" + a.length);
				for (int i = 0; i < a.length; i++) {
					System.out.print("Parents of X" + i + " are ");
					if (a[i] != null) {
						if (a[i].size() > 0) {
							for (int j = 0; j < a[i].size(); j++) {
								System.out.print("X" + a[i].get(j).toString() + ", ");
							}
						}
						System.out.println("");
					} else {
						System.out.println("-- ERROR: null vector[" + i + "]");
					}
				}
			} else {
				System.out.println("-- ERROR: int[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
	}



	public String getString(Vector a) {
		String s = "";
		if (a != null) {
			if (a.size() > 0) {
				for (int i = 0; i < a.size() - 1; i++) {
					s = s + a.get(i).toString() + ", ";
				}

				s = s + a.get(a.size() - 1).toString();

			} else {
				System.out.println("int[].length = 0");
			}
		} else {
			System.out.println("-- ERROR: The matrix has a null value");
		}
		return s;
	}

	public void copyAintoB(Vector a, Vector b) {
		if (a != null) {
			if (a.size() > 0) {
				for (int i = 0; i < a.size(); i++) {
					b.add(a.get(i));
				}
			} else {
				//System.out.println("-- ERROR: The vector size is 0");
			}
		} else {
			//System.out.println("-- ERROR: The vector has a null value");
		}

	}

	public void copyAintoB(int[] a, int[] b) {
		if (a != null) {
			if (a.length > 0) {
				for (int i = 0; i < a.length; i++) {
					b[i] = a[i];
				}
			} else {
				//System.out.println("-- ERROR: The vector size is 0");
			}
		} else {
			//System.out.println("-- ERROR: The vector has a null value");
		}

	}



	public void bubbleSort(int[] array) {


		for (int i = 0; i < array.length-1; i++) {
			for(int j = 0; j < array.length-1; j++){

				if (array[j] > array[j+1]) {
					int temp = array[j+1];
					array[j+1] = array[j];
					array[j] = temp;
				}
			}

		}

	}


	public void bubbleSort(String[][] array, int columntoSort) {


		for (int i = 0; i < array.length-1; i++) {
			for(int j = 0; j < array.length-1; j++){

				if (Integer.parseInt(array[j][columntoSort]) < Integer.parseInt(array[j+1][columntoSort])) {
					String[] temp = array[j+1];
					array[j+1] = array[j];
					array[j] = temp;
				}
			}

		}

	}
	public int normalize(int value, int upperOld, int upperNew) {
		int newValue = value/ (upperOld/upperNew);


		return newValue;
	}

/*public static Instances toInstances(double[][] arr, Instances inst) {

		Instances data = new Instances(inst, 0);

		for (int i = 0; i < arr.length; i++) {
			Instance n = new Instance(1, arr[i]);
			data.add(n);
		}
		return data;

	}*/

	public static double[][] toArray(Instances data) {
		double[][] d = new double[data.numInstances()][data.instance(0)
		                                               .numAttributes()];
		for (int i = 0; i < data.numInstances(); i++) {
			for (int j = 0; j < data.instance(i).numAttributes(); j++) {
				d[i][j] = data.instance(i).value(j);
			}
		}
		return d;
	}

	public static String[] parseByLine(String xml) {
		String[] s =xml.split("\n");
		return s;

	}
	public static String innerTrim(String xml) {
		String out = "";
		int index = 0;
		boolean flag = false;

		xml = xml.trim();
		while(index<xml.length()){
			if(xml.charAt(index) != ' '){
				out = out+xml.charAt(index);
				index++;
				flag = false;
			}
			else{
				if(flag == false){
					out = out+xml.charAt(index);
					index++;
					flag = true;
				}
				else{
					index++;
				}
			}
				
		}


		return out;

	}

	public static String[] parseXML(String xml, String start, String ende) {
		Vector<String> v = parseVectorXML(xml, start, ende);
		String[] s = toStringArray(v);
		return s;

	}
	public static Vector<String> parseVectorXML(String xml, String start, String ende) {
		int c = 0;
		xml.length();
		Vector labels = new Vector(0, 1);



		do {
			if (c + start.length() < xml.length()) {
				if (xml.substring(c, c + start.length()).equals(start)) {

					//gets all the chars between labels
					String p = "";
					int end_index = c;

					do {
						end_index++;
					} while (!(xml.substring(end_index, end_index + ende.length()).equals(ende)));
					p = xml.substring(c + start.length(), end_index);
					labels.add(p);

					//actualiza los ���ndices
					c = c + start.length();

				}
			}
			c++;
		} while (xml.length() > c);

		return labels;

	}

}
