package com.tinysearchengine.indexer.hive;

import java.util.UUID;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class GenerateUUID extends UDF {
	public Text evaluate(final Text word, final Text url) {
		String result = "@Exception";
		
		try
		{
			String a = word.toString() + url.toString();
			result = UUID.nameUUIDFromBytes(a.getBytes()).toString();
			
			if (result.length() > 2000)
			{
				result = "@TooLong";
			}
			
		}catch(Exception e)
		{
			
		}
	    
	    return new Text(result);
	}
}
