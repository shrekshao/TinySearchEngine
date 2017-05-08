SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;

add jar s3://tinysearchengine-mapreduce/program/hive-contrib-2.1.1.jar;

add jar s3://tinysearchengine-mapreduce/program/MyUDF.jar;

CREATE FUNCTION catch_string_e AS 'com.tinysearchengine.indexer.hive.StringCatchException'; 
CREATE FUNCTION catch_double_e AS 'com.tinysearchengine.indexer.hive.DoubleCatchException';
CREATE FUNCTION my_long_add AS 'com.tinysearchengine.indexer.hive.LongAdd';

CREATE EXTERNAL TABLE s3_parseddoc_test
    (
    url      STRING,
    title      STRING,
    abstract     STRING)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "^(.*)\\s~~\\s(.*)\\s~~\\s(.*)$"
)
LOCATION 's3://tinysearchengine-mapreduce/parsed-doc-test';


CREATE EXTERNAL TABLE s3_parseddoc_test
    (
    url      STRING,
    title      STRING,
    abstract     STRING)
ROW FORMAT SERDE 'org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe'
WITH SERDEPROPERTIES ("field.delim"=" ~~ ")
LOCATION 's3://tinysearchengine-mapreduce/parsed-doc-test';



CREATE EXTERNAL TABLE ddb_parseddoc
    (url      STRING,
    title      STRING,
    abstract     STRING
    )
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES(
    "dynamodb.table.name" = "ParsedDoc",
    "dynamodb.column.mapping"="url:url,title:title,abstract:abstract"
);


INSERT OVERWRITE TABLE ddb_parseddoc
SELECT * FROM s3_parseddoc_test
WHERE url<>"" AND LENGTH(url)<2000 AND url IS NOT NULL AND title<>"" AND title IS NOT NULL AND abstract<>"" AND abstract IS NOT NULL;

INSERT OVERWRITE TABLE ddb_parseddoc
SELECT url,catch_string_e(title),catch_string_e(abstract) FROM s3_parseddoc_test
WHERE url IS NOT NULL AND url<>"" AND LENGTH(url)<2000;