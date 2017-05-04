CREATE TEMPORARY FUNCTION row_sequence as 'org.apache.hadoop.hive.contrib.udf.UDFRowSequence';

CREATE FUNCTION row_counter as 'org.apache.hadoop.hive.contrib.udf.UDFRowSequence';

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

CREATE EXTERNAL TABLE s3_worddoctftuple
    (word      STRING,
    idf     DOUBLE,
    url     STRING,
    tf      DOUBLE)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "^([^\\t\\s]*)\\t([^\\t\\s]*)\\s([^\\t\\s]*)\\s([^\\t\\s]*)$"
)
LOCATION 's3://tinysearchengine-mapreduce/inverted-index/output';

CREATE EXTERNAL TABLE s3_worddoctftuple_fix
    (word      STRING,
    idf     DOUBLE,
    url     STRING,
    tf      DOUBLE)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "^([^\\t\\s]*)\\t([^\\t\\s]*)\\s(.*)\\s([^\\t\\s]*)$"
)
LOCATION 's3://tinysearchengine-mapreduce/inverted-index/output';


INSERT OVERWRITE TABLE ddb_worddoctftuple
SELECT row_counter(),word,url,tf FROM s3_worddoctftuple;

INSERT OVERWRITE TABLE ddb_worddoctftuple
SELECT row_idx(),word,url,tf FROM s3_worddoctftuple_fix;


# test

CREATE EXTERNAL TABLE s3_worddoctftuple_test
    (word      STRING,
    idf     DOUBLE,
    url     STRING,
    tf      DOUBLE)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "^([^\\t\\s]*)\\t([^\\t\\s]+)\\s(.*)\\s([^\\t\\s]*)$"
)
LOCATION 's3://tinysearchengine-mapreduce/inverted-index/output-test';

CREATE EXTERNAL TABLE s3_worddoctftuple_test_fix
    (word      STRING,
    idf     DOUBLE,
    url     STRING,
    tf      DOUBLE)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "^([^\\t\\s]*)\\t([^\\t\\s]+)\\s(.*)\\s([^\\t\\s]*)$"
)
LOCATION 's3://tinysearchengine-mapreduce/inverted-index/output-test';


SELECT row_sequence(),* FROM s3_worddoctftuple_test;
SELECT row_counter(),* FROM s3_worddoctftuple_test;

SELECT DISTINCT word,idf FROM s3_worddoctftuple_test;

SELECT * FROM s3_worddoctftuple_fix WHERE word="00000000000000e" LIMIT 10;

SELECT * FROM s3_worddoctftuple_fix WHERE word is null OR idf is null OR url is null OR tf is null LIMIT 20;

# idf table

CREATE EXTERNAL TABLE ddb_wordidf
    (
    word  STRING,
    idf DOUBLE)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES(
    "dynamodb.table.name" = "WordIdf",
    "dynamodb.throughput.read.percent" = "100",
    "dynamodb.throughput.write.percent" = "100",
    "dynamodb.column.mapping"="word:word,idf:idf"
);


INSERT OVERWRITE TABLE ddb_wordidf
SELECT DISTINCT word,idf FROM s3_worddoctftuple;

INSERT OVERWRITE TABLE ddb_wordidf
SELECT DISTINCT word,idf FROM s3_worddoctftuple_fix WHERE idf is NOT NULL;

INSERT OVERWRITE TABLE ddb_wordidf
SELECT DISTINCT word,idf FROM s3_worddoctftuple_fix WHERE word <> "00000000000000e";

add jar s3://tinysearchengine-mapreduce/program/hive-contrib-2.1.1.jar;

CREATE FUNCTION row_idx AS 'org.apache.hadoop.hive.contrib.udf.UDFRowSequence'; 

set hive.execution.engine to mr


