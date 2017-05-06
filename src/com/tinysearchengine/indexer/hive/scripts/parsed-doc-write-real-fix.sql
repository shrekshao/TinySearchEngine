SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;



INSERT OVERWRITE TABLE ddb_parseddoc
SELECT * FROM s3_parseddoc_test
WHERE url<>"" AND LENGTH(url)<2000 AND url IS NOT NULL AND title<>"" AND title IS NOT NULL AND abstract<>"" AND abstract IS NOT NULL;
