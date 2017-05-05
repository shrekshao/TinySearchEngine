SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;

add jar s3://tinysearchengine-mapreduce/program/MyUDF.jar;

CREATE FUNCTION catch_string_e AS 'com.tinysearchengine.indexer.hive.StringCatchException'; 
CREATE FUNCTION catch_double_e AS 'com.tinysearchengine.indexer.hive.DoubleCatchException';
CREATE FUNCTION my_long_add AS 'com.tinysearchengine.indexer.hive.LongAdd';

add jar s3://tinysearchengine-mapreduce/program/hive-contrib-2.1.1.jar;

CREATE FUNCTION row_idx AS 'org.apache.hadoop.hive.contrib.udf.UDFRowSequence';

CREATE EXTERNAL TABLE s3_wordtfurl_test
    (
    word      STRING,
    tf      DOUBLE,
    url     STRING)
LOCATION 's3://tinysearchengine-mapreduce/wordtfurl-test';

CREATE EXTERNAL TABLE ddb_wordtfurl
    (id   BIGINT,
    word  STRING,
    tf DOUBLE,
    url   STRING
    )
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES(
    "dynamodb.table.name" = "WordDocTfTupleNew",
    "dynamodb.column.mapping"="id:id,word:word,tf:tf,url:url"
);