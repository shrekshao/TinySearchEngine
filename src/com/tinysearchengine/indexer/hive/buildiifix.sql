SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;

CREATE EXTERNAL TABLE s3_worddoctftuple
    (word      STRING,
    idf     DOUBLE,
    url     STRING,
    tf      DOUBLE)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "^([^\\t\\s]*)\\t([^\\t\\s]*)\\s(.*)\\s([^\\t\\s]*)$"
)
LOCATION 's3://tinysearchengine-mapreduce/inverted-index/output';

CREATE EXTERNAL TABLE ddb_wordidf
    (
    word  STRING,
    idf DOUBLE)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES(
    "dynamodb.table.name" = "WordIdf",
    "dynamodb.column.mapping"="word:word,idf:idf"
);


CREATE TABLE hive_wordidf
    (
    word  STRING,
    idf DOUBLE);


ALTER TABLE hive_wordidf SET SERDEPROPERTIES ('serialization.encoding'='UTF-8');


INSERT OVERWRITE TABLE ddb_wordidf
SELECT * FROM hive_wordidf WHERE word<>'';

INSERT OVERWRITE TABLE ddb_wordidf
SELECT * FROM hive_wordidf WHERE word='qauo7upiifv90aghrapcjwmjxtj1ifmcgl';




#-------------------

add jar s3://tinysearchengine-mapreduce/program/hive-contrib-2.1.1.jar;

CREATE FUNCTION row_idx AS 'org.apache.hadoop.hive.contrib.udf.UDFRowSequence';


CREATE TABLE hive_worddoctftuple
    (id   BIGINT,
    word  STRING,url
    url   STRING,
    tf DOUBLE)

INSERT OVERWRITE TABLE hive_worddoctftuple
SELECT row_idx(),word,url,tf FROM s3_worddoctftuple WHERE word<>"" AND url<>"";


CREATE EXTERNAL TABLE ddb_worddoctftuple
    (id   BIGINT,
    word  STRING,
    url   STRING,
    tf DOUBLE)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES(
    "dynamodb.table.name" = "WordDocTfTuple",
    "dynamodb.column.mapping"="id:id,word:word,url:url,tf:tf"
);


INSERT OVERWRITE TABLE ddb_worddoctftuple
SELECT * FROM hive_worddoctftuple;