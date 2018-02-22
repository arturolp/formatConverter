# formatConverter
A java project to convert datasets between different formats (arff, eav, nam/cas)


## Examples
<b>Example BIF to XDSL:</b>
```
java -jar formatConverter.jar -input exampleBIF.xml -inputFormat bif -outputFormat xdsl -hashfile umlscodes.xml
java -jar formatConverter.jar -input exampleBIF.xml -inputFormat bif -outputFormat xdsl -target BC0021400
```
<b>Example Naïve Bayes Weka to XDSL:</b>
```
java -jar formatConverter.jar -input modelMedLee.model -inputFormat NBweka -outputFormat xdsl
```
<b>Example ARFF to EBMC:</b>
```
java -jar formatConverter.jar -input DS2.arff -inputFormat arff -outputFormat ebmc
```
<b>Example EAV to ARFF:</b>
```
java -jar formatConverter.jar -input DS2-eav.csv -inputFormat eav -outputFormat arff
```

<b>Example CSV to ARFF:</b>
```
java -jar formatConverter.jar -input DS2.csv -inputFormat csv -outputFormat arff
```


## Configuration
Expected commands format: 
```
-input data.arff [-target class] [-inputFormat eav] [-output Desktop/results/] [-outputFormat ebmc|arff] [-discretize 0.5] [-transpose false] [-hashtable table.csv]
```

|Parameter|Example|Description|
|-------------|-------------|-------------|
| -input | data.txt | data.arff is the input file, or data.csv |
| -target | class | the name of the target attribute. Default: class |
| -inputFormat | eav | the format of the input data. Default: file extension |
| -output | Desktop/results/ | The output Path where the new files are going to be created. Default is the same as input |
| -outputFormat |  ebmc | Create the file(s) speficied by the new format. Default is arff |
| -discretize | 0.5 | Discretize all columns using the cutoff point given. Default is not discretized |
| -transpose | true | Transpose the values, rows to columns and columns to rows. Default is false |
| -hashTable | filename.csv | A .csv file with the codes and descriptions |
