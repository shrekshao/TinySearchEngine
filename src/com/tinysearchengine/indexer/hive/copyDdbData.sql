CREATE EXTERNAL TABLE ddb_document
    (url   STRING,
    content_link STRING,
    links ARRAY<STRING>)
STORED BY 'org.apache.hadoop.hive.dynamodb.DynamoDBStorageHandler'
TBLPROPERTIES(
    "dynamodb.table.name" = "DocumentTable",
    "dynamodb.column.mapping"="url:url,content_link:contentLink,links:links"
);

INSERT OVERWRITE DIRECTORY 's3://tinysearchengine-mapreduce/document-table/' 
SELECT url, content_link, links FROM ddb_document;

