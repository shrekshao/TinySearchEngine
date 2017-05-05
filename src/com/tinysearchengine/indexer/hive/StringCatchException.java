package com.tinysearchengine.indexer.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class StringCatchException extends UDF {
	public Text evaluate(final Text s) {
		String result = "@";
		
		try
		{
			String input = s.toString();
			
			if (input == null)
			{
				result = "@Null";
			}
			else if (input.equals(""))
			{
				result = "@Empty";
			}
			else if (input.length() > 2000)
			{
				result = "@TooLong";
			}
			else
			{
				result = input;
			}
			
		}catch(Exception e)
		{
			result = "@Exception";
		}
		
	    
	    
	    return new Text(result);
	}
}
