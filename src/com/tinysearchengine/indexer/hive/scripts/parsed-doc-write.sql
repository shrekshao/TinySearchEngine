SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;

CREATE EXTERNAL TABLE s3_parseddoc
    (
    url      STRING,
    title      STRING,
    abstract     STRING)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
WITH SERDEPROPERTIES (
  "input.regex" = "^(.*)\\s~~\\s(.*)\\s~~\\s(.*)$"
)
LOCATION 's3://tinysearchengine-mapreduce/parsed-doc';

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
