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





ALTER TABLE hive_wordidf SET SERDEPROPERTIES ('serialization.encoding'='UTF-8');


INSERT OVERWRITE TABLE ddb_wordidf
SELECT * FROM hive_wordidf WHERE word<>'';

INSERT OVERWRITE TABLE ddb_wordidf
SELECT * FROM hive_wordidf WHERE word='qauo7upiifv90aghrapcjwmjxtj1ifmcgl';