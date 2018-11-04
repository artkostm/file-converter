# file-converter
Steps to run:

- run sbt:
```
sbt assembly
```
- download destinations.csv and test.csv from ...
- put these files into hdfs. For example: 
```
hdfs dfs -put /path/to/destinations.csv /hdfs/path/destinations.csv
```
- ```./run.sh```
- to be able to view parquet file content, go to the Hue and execute the following script:
```
Create table
from parquet
Location ...
```
- now you can query the parquet file for some data, for example: ```select * from test```