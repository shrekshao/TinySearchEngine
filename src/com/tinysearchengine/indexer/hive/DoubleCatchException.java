package com.tinysearchengine.indexer.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.DoubleWritable;

public class DoubleCatchException extends UDF {
	public DoubleWritable evaluate(final DoubleWritable input) {
		DoubleWritable result = new DoubleWritable(-1);
		
		try
		{
			
			if (input != null)
			{
				result = new DoubleWritable(input.get());
			}
			
		}catch(Exception e)
		{
		}
		
	    
	    
	    return result;
	}
}
