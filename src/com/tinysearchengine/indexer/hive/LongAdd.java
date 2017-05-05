package com.tinysearchengine.indexer.hive;

import org.apache.hadoop.hive.ql.exec.UDF;

public class LongAdd extends UDF {
	public long evaluate(final long input, final long add) {
		long result = 0;
		try
		{
			
			result = input + add;
			
		}catch(Exception e)
		{
		}
		
	    
	    
	    return result;
	}
}
