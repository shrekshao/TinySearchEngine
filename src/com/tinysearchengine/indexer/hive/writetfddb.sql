SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;

add jar s3://tinysearchengine-mapreduce/program/MyUDF.jar;

CREATE FUNCTION catch_string_e AS 'com.tinysearchengine.indexer.hive.StringCatchException'; 
CREATE FUNCTION catch_double_e AS 'com.tinysearchengine.indexer.hive.DoubleCatchException';
CREATE FUNCTION my_long_add AS 'com.tinysearchengine.indexer.hive.LongAdd';

CREATE EXTERNAL TABLE s3_wordtfurl
    (
    word      STRING,
    tf      DOUBLE,
    url     STRING)
LOCATION 's3://tinysearchengine-mapreduce/wordtfurl';
