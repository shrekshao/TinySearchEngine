# Indexer DynamoDB Design

* Tables
    + Parsed Document
        - docid (url)
        - doc pointer to S3 (manual node hash) / url (if stored in db)
        - total word count
        - page rank score
        - HashSet ( p: wordid, tf )
    + KeyWord
        - wordid ( still needed, cuz string for primiary key cost more space) 
        - word (text, probably after Porterstemmer)
        - global count
        - idf (log(N/n))
        - HashSet ( p: docid, count, (tf) )





------------

> (Since it's NoSQL DB so probably don't need this)        
>    + KeyWord Document relation
>        - wordid
>        - docid
>        - count
>        - tf (freq(i,j) = raw occurance of w_i in d_j)


--------------

# Calulation needed when querying

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
