# Indexer DynamoDB Design

* Tables
    + Parsed Document
        - docid (url)
        - doc pointer to S3 (manual node hash) / url (if stored in db)
        - total word count
        - page rank score
        - (other general info)
    + KeyWord
        - wordid (* might not be necessary)
        - word (text, probably after)
        - global count
        - idf (log(N/n))
    + KeyWord Document relation
        - wordid
        - docid
        - count
        - tf (freq(i,j) = raw occurance of w_i in d_j)




Incoming query
{k_x0, k_x1, ..., k_xn}
for each keyword k_xi
tf = normalized freq = a + (1-a) * freq(i,j)/max_i(freq(l,j))  (a=0.5)
idf = log(N/n)