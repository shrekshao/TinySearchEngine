package com.tinysearchengine.searchengine.servlet;

public class WordDistance {
	    public int minDistance(String word1, String word2) {
	        int m = word1.length();
	        int n = word2.length();
	        
	        int[][] distance = new int[m + 1][n + 1];
	        for(int i = 0; i <= m; i++) { distance[i][0] = i; }
	        for(int i = 1; i <= n; i++) { distance[0][i] = i; }
	        
	        for(int i = 0; i < m; i++) {
	            for(int j = 0; j < n; j++) {
	                if(word1.charAt(i) == word2.charAt(j))
	                    distance[i + 1][j + 1] = distance[i][j];
	                else {
	                    int a = distance[i][j];
	                    int b = distance[i][j + 1];
	                    int c = distance[i + 1][j];
	                    distance[i + 1][j + 1] = a < b ? (a < c ? a : c) : (b < c ? b : c);
	                    distance[i + 1][j + 1]++;
	                }
	            }
	        }
	        return distance[m][n];
	    }
}
