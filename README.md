# formatConverter
A java project to convert datasets between different formats (arff, eav, nam/cas)


Description
===================
A tool for converting between different file formats



Examples
===================
<b>Example BIF to XDSL:</b>
java -jar formatConverter.jar -input exampleBIF.xml -inputFormat bif -outputFormat xdsl -hashfile umlscodes.xml
java -jar formatConverter.jar -input exampleBIF.xml -inputFormat bif -outputFormat xdsl -target BC0021400

<b>Example ARFF to EBMC:</b>
java -jar formatConverter.jar -input DS2.arff -inputFormat arff -outputFormat ebmc

<b>Example EAV to ARFF:</b>
java -jar formatConverter.jar -input DS2-eav.csv -inputFormat eav -outputFormat arff

<b>Example CSV to ARFF:</b>
java -jar formatConverter.jar -input DS2.csv -inputFormat csv -outputFormat arff



Configuration
===================
Expected commands format: -input data.arff [-target class] [-inputFormat eav] [-output Desktop/results/] [-outputFormat ebmc|arff]
   -input data.txt 	 			data.arff is the input file, or data.csv
   -target class 	 			the name of the target attribute. Default: class
   -inputFormat eav	 			the format of the input data. Default: file extension
   -output Desktop/results/  	The output Path where the new files are going to be created. Default is the same as input
   -outputFormat ebmc  			Create the file(s) speficied by the new format. Default is arff
   -discretize 0.5  			Discretize all columns using the cutoff point given. Default is not discretized
   -transpose true  			Transpose the values, rows to columns and columns to rows. Default is false
   -hashTable filename.csv		A .csv file with the codes and descriptions



EAV.CSV File Format
===================
Column 1: the ID of the entity
Column 2: the Attribute CODE
Column 3: the Attribute NAME
Column 4: the value recorded

