# Indexer DynamoDB Design

* Tables
    + ParsedDoc
        - docid (url)
        - doc pointer to S3 (manual node hash) / url (if stored in db)
        - total word count
        - page rank score
        - Map < wordid, tf >
    + KeyWord
        - wordid ( still needed, cuz string for primiary key cost more space) 
        - word (text, probably after Porterstemmer)
        - global count
        - idf (log(N/n))  (probably unavailable)
        - Set ( docid )





------------

> (Since it's NoSQL DB so probably don't need this)        
>    + KeyWord Document relation
>        - wordid
>        - docid
>        - count
>        - tf (freq(i,j) = raw occurance of w_i in d_j)


--------------


# Hadoop batch task for calculating indexer (static)

* mapper
    - input: < `docid`, body:string >
    - tasks:
        - [x] Jsoup parse
        - [x] Select elements (e.g. < p >)
        - [x] Split to keywords
        - [x] porterstemmer
        - [ ] store to dynamoDB table ParsedDoc (or, emit with a special key to label as document tuple)
    - emit: < `wordid`, docid, count >
    - emit: < `@totalCount@`, Null, localTotalCount >  (For calculate global word counts used by idf)
* reducer
    - input: < `wordid`, docid, count >
    - tasks:
        - if wordid == `@totalCount@`
            - sum up
            - store without doc hashset
        - else
            - [x] sum up total count of this wordid
            - [ ] store to dynamoDB table Keyword


Another round of simple map reduce to fill in idf for each keyword (if found better for performance)


## Alternative design (to make sure all write to db happens after output from reducer)

* pass 1 (write forward index)
    * input DdbDocument from dynamoDB, crawled doc from S3
    * mapper emit < word, docid(url), count( freq(word, this doc)  ) > 
    * reducer emit as is
* pass 2 (write forward index)
    * input (intermediate text files from pass 1 output)
        * < word, `docid`, count > (group by docid)
    * mapper emit as is (docid is aggregation key)
    * reducer emit as is ( batch write output to dynamoDB table: Parsed Doc (forward index)  )
* pass3 (write inverted index)
    * input (intermediate text files from pass 1 output)
        * < `word`, docid, count > (group by docid)
    * mapper emit as is
    * reducer emit as is

(Such many emitting as is, is it really efficient or necessary?)


----------


# Calculation needed when querying

Incoming query
{k_x0, k_x1, ..., k_xn}
for each keyword k_xi
tf = normalized freq = a + (1-a) * freq(i,j)/max_i(freq(l,j))  (a=0.5)
idf = log(N/n)


* query DB:
    - Query Q = {k_x0, k_x1, ..., k_xn}
        - HashMap < keyword, idf>
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
