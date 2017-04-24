package com.tinysearchengine.indexer.test;

import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;



public class TestPorterStemmer
{

    static String readFile(String path, Charset encoding) 
        throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args)
    {
    	String html = "";
        try
        {
//            String html = new Scanner(new File("test.html")).useDelimiter("\\Z").next();
        	html = readFile("filesForTest/test.html", StandardCharsets.UTF_8);
        	
//            System.out.println(html);
        	
        }
        catch( Exception e)
        {
        	e.printStackTrace();
        }
        
        Document doc = Jsoup.parse(html);
    	Elements e = doc.select("p");
       
        System.out.println(e.text());
        
        String[] words = (e.text()).split("(\\s)+|'|\"|,|\\.|\\?|\\!|\\(|\\)");
        
        SnowballStemmer stemmer = new englishStemmer();
        
        for (String word : words)
        {
        	stemmer.setCurrent(word);
        	stemmer.stem();
        	String stemmed = stemmer.getCurrent();
        	
        	System.out.println(word + " - " + stemmed);
        }
        
        
        
    }
}