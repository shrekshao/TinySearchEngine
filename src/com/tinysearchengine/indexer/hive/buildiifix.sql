SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;

SET dynamodb.throughput.read.percent=1.0;
SET dynamodb.throughput.write.percent=1.0;

-- CREATE EXTERNAL TABLE s3_worddoctftuple
--     (word      STRING,
--     idf     DOUBLE,
--     url     STRING,
--     tf      DOUBLE)
-- ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
-- WITH SERDEPROPERTIES (
--   "input.regex" = "^([^\\t\\s]*)\\t([^\\t\\s]*)\\s(.*)\\s([^\\t\\s]*)$"
-- )
-- LOCATION 's3://tinysearchengine-mapreduce/inverted-index/output';

CREATE EXTERNAL TABLE s3_worddoctftuple
    (word      STRING,
    idf     DOUBLE,
    url     STRING,
    tf      DOUBLE)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "^(.*)\\t([^\\t\\s]+)\\s(.*)\\s([^\\t\\s]+)$"
)
LOCATION 's3://tinysearchengine-mapreduce/inverted-index/output';

CREATE EXTERNAL TABLE s3_wordidf
    (word      STRING,
    idf     DOUBLE)
LOCATION 's3://tinysearchengine-mapreduce/wordidf';


INSERT OVERWRITE TABLE s3_wordidf
SELECT DISTINCT word,idf FROM s3_worddoctftuple WHERE word<>"" AND word iS NOT NULL AND idf IS NOT NULL;


SELECT * FROM s3_wordidf LIMIT 10;


SELECT catch_string_e(word),catch_double_e(idf) FROM s3_wordidf LIMIT 10;



CREATE EXTERNAL TABLE s3_wordtfurl
    (
    word      STRING,
    tf      DOUBLE,
    url     STRING)
LOCATION 's3://tinysearchengine-mapreduce/wordtfurl';

INSERT OVERWRITE TABLE s3_wordtfurl
SELECT word,tf,url FROM s3_worddoctftuple WHERE word<>"" AND word iS NOT NULL AND tf IS NOT NULL AND url<>"" AND url iS NOT NULL;

INSERT OVERWRITE TABLE ddb_wordidf
SELECT word,idf FROM s3_wordidf WHERE word<>'' AND word iS NOT NULL AND idf IS NOT NULL;


INSERT OVERWRITE TABLE ddb_wordidf
SELECT catch_string_e(word),idf FROM s3_wordidf;

INSERT OVERWRITE TABLE ddb_wordidf
SELECT catch_string_e(word),catch_double_e(idf) FROM s3_wordidf;


---------------------

CREATE EXTERNAL TABLE ddb_wordtfurl
    (id   BIGINT,
    word  STRING,
    tf DOUBLE,
    url   STRING
    )
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES(
    "dynamodb.table.name" = "WordDocTfTuple",
    "dynamodb.column.mapping"="id:id,word:word,tf:tf,url:url"
);

INSERT OVERWRITE TABLE ddb_wordtfurl
SELECT row_idx(),catch_string_e(word),tf,catch_string_e(url) FROM s3_wordtfurl;


# -----------

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


INSERT OVERWRITE TABLE hive_wordidf
SELECT DISTINCT word,idf FROM s3_worddoctftuple_a WHERE word<>"";

INSERT OVERWRITE TABLE ddb_wordidf
SELECT * FROM hive_wordidf WHERE word<>'';



INSERT OVERWRITE TABLE ddb_wordidf
SELECT * FROM hive_wordidf WHERE word='qauo7upiifv90aghrapcjwmjxtj1ifmcgl';




ALTER TABLE ddb_wordidf SET TBLPROPERTIES ('dynamodb.throughput.write.percent'='85');


INSERT OVERWRITE TABLE ddb_wordidf
SELECT DISTINCT word,idf FROM s3_worddoctftuple_a WHERE word<>"";

#-------------------

add jar s3://tinysearchengine-mapreduce/program/hive-contrib-2.1.1.jar;

CREATE FUNCTION row_idx AS 'org.apache.hadoop.hive.contrib.udf.UDFRowSequence';


CREATE TABLE hive_worddoctftuple
    (id   BIGINT,
    word  STRING,url
    url   STRING,
    tf DOUBLE);

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


-- INSERT OVERWRITE TABLE ddb_worddoctftuple
-- SELECT * FROM hive_worddoctftuple;

add jar s3://tinysearchengine-mapreduce/program/hive-contrib-2.1.1.jar;

CREATE FUNCTION row_idx AS 'org.apache.hadoop.hive.contrib.udf.UDFRowSequence'; 

INSERT OVERWRITE TABLE ddb_worddoctftuple
SELECT row_idx(),word,url,tf FROM s3_worddoctftuple_a WHERE word<>"" AND url<>"";


# --------------------------

add jar s3://tinysearchengine-mapreduce/program/CatchExceptionUDF.jar;

CREATE FUNCTION catch_string_e AS 'com.tinysearchengine.indexer.hive.StringCatchException'; 
CREATE FUNCTION catch_double_e AS 'com.tinysearchengine.indexer.hive.DoubleCatchException';





#-----------------------------


 aws dynamodb scan --table-name WordIdf --select COUNT