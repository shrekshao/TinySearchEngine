SET dynamodb.throughput.read.percent=10;
SET dynamodb.throughput.write.percent=10;



INSERT OVERWRITE TABLE ddb_wordtfurl
SELECT row_idx(),word,tf,url FROM s3_wordtfurl_test
WHERE word<>"" AND LENGTH(word)<2000 AND word IS NOT NULL AND url<>"" AND url IS NOT NULL;