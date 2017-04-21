# Indexer DynamoDB Design

* Tables
    + Parsed Document
        - docid
        - doc pointer to S3 (manual node hash)
        - total word count
        - (other general info)
    + KeyWord
        - wordid
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
tf = normalized freq = a + (1-a) * freq(i,j)/max_i(freq(l,j))
idf = log(N/n)