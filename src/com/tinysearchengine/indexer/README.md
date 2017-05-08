# Indexer DynamoDB Design

* Tables
    + ParsedDoc Table ( for result display on search engine result page )
        - docid (url)
        - title:string
        - abstract:string


* Keyword Table redesign (if Keyword Table limited by entry size 400kb)
    + WordDocTfTuple
        - columns
            - `id:int` (ddb default primary key)
            - word:string (After stemmed)
            - docid:string (url)
            - tf:double
        - global secondary index
            - word (inverted index)
            - docid (forward index) (no usage for basic requirement)
    + Idf
        - columns
            - `word:string` (After stemmed)
            - idf:double


--------------

# Hadoop batch task for calculating indexer (static)

* mapper
    - input: < `docid`, body:string >
    - tasks:
        - [x] Jsoup parse
        - [x] Select elements (e.g. < p >)
        - [x] Split to keywords
        - [x] porterstemmer
    - emit: < `wordid`, docid, count >
    - emit: < `@totalCount@`, Null, localTotalCount >  (For calculate global word counts used by idf)  (This is not needed..., instead, totalCount of document is needed (can directly get from how many tuples are there in the dynamodb table documenttable) )
* reducer
    - input: < `wordid`, docid, count >
    - tasks:
        - if wordid == `@totalCount@`
            - sum up
            - store without doc hashset
        - else
            - [x] sum up total count of document this wordid is in
            - [x] emit to hdfs (then store in S3)


Another round of simple map reduce to fill in idf for each keyword (if found better for performance)


----------


# Calculation needed when querying

Incoming query
{k_x0, k_x1, ..., k_xn}
for each keyword k_xi
tf = normalized freq = a + (1-a) * freq(i,j)/max_i(freq(l,j))  (a=0.5)
idf ( by look up the ddb table )


* query DB:
    - Query Q = {k_x0, k_x1, ..., k_xn}
        - HashMap < keyword, idf >
    - Related Doc Set D = {}
    - for each k in Q
        - get idf(k)
        - for each d in k.docs
            - add d to D
    - for each d in D
        - for each k in Q:
            - get tf(k, d)
    - for each d in D
        - score = d.pagerank * dot(v_q, v_d)



* vector of query = ( tf*idf_x0, tf*idf_x1, ..., tf*idf_xn)
* vector of doc = ( tf*idf_x0, tf*idf_x1, ..., tf*idf_xn)


* score for each doc: dot( v_q, v_d ) * PageRankScore

--------

EMR custom jar arguments

com.tinysearchengine.indexer.BuildInvertedIndex
s3n://tinysearchengine-mapreduce/document-table
s3n://tinysearchengine-mapreduce/inverted-index/output


Testing purpose

com.tinysearchengine.indexer.BuildInvertedIndex
s3n://tinysearchengine-mapreduce/inverted-index-test-input
s3n://tinysearchengine-mapreduce/inverted-index-test-output/output-a


com.tinysearchengine.indexer.ParseDocMain
s3n://tinysearchengine-mapreduce/document-table
s3n://tinysearchengine-mapreduce/parsed-doc/output

com.tinysearchengine.indexer.ParseDocMain
s3n://tinysearchengine-mapreduce/inverted-index-test-input
s3n://tinysearchengine-mapreduce/parsed-doc-test-output/output-a