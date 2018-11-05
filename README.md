# file-converter

## Steps to run:

- run sbt:
```
sbt assembly
```
- download destinations.csv and test.csv from https://www.kaggle.com/c/expedia-hotel-recommendations/data
- put these files into hdfs. For example: 
```
hdfs dfs -put /path/to/destinations.csv /tmp/destinations.csv
```
- ```./run.sh```
- to be able to view parquet file content, go to the Hive UI and execute the following script:
```haskell
CREATE EXTERNAL TABLE test (
	id STRING,
	date_time STRING,
	site_name STRING,
	posa_continent STRING,
	user_location_country STRING,
	user_location_region STRING,
	user_location_city STRING,
	orig_destination_distance STRING,
	user_id STRING,
	is_mobile STRING,
	is_package STRING,
	channel STRING,
	srch_ci STRING,
	srch_co STRING,
	srch_adults_cnt STRING,
	srch_children_cnt STRING,
	srch_rm_cnt STRING,
	srch_destination_id STRING,
	srch_destination_type_id STRING,
	hotel_continent STRING,
	hotel_country STRING,
	hotel_market STRING)
STORED AS PARQUET
LOCATION '/tmp/test/'; 
```
- now you can query the parquet file for some data, for example: ```select * from test```

## Application description

The application is a command line tool that provides a mr job to convert a csv file to avro/parquet files.

Args:
```[avro|parquet] -i(--in) -o(--out) -s(--schema) -h(--header)```

> *-i or --in* - input file location in the hdfs (should exist)<br>
*-o or --out* - output file directory in the hdfs (should not exist)<br>
*-s or --schema* - schema file in the local file system (should exist)<br>
*-h or --header* - a flag, if present, the header of the input file will not be ignored<br>

Depending on command specified, the job consist of either AvroMapper or ParquetMapper task and with no reduce tasks.
