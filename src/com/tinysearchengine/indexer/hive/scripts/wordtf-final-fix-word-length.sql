SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;

add jar s3://tinysearchengine-mapreduce/program/UDFWithUUID.jar;

CREATE FUNCTION uuid AS 'com.tinysearchengine.indexer.hive.GenerateUUID'; 


CREATE EXTERNAL TABLE s3_wordtfurl
    (
    word      STRING,
    tf      DOUBLE,
    url     STRING)
LOCATION 's3://tinysearchengine-mapreduce/wordtfurl';

CREATE EXTERNAL TABLE ddb_wordtfurl
    (id   STRING,
    word  STRING,
    tf DOUBLE,
    url   STRING
    )
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES(
    "dynamodb.table.name" = "WordDocTfTupleUUID",
    "dynamodb.column.mapping"="id:id,word:word,tf:tf,url:url"
);

INSERT OVERWRITE TABLE ddb_wordtfurl
SELECT uuid(word,url),word,tf,url FROM s3_wordtfurl
WHERE word<>"" AND LENGTH(word)<2000 AND word IS NOT NULL AND url<>"" AND url IS NOT NULL;
