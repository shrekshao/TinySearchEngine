//package com.tinysearchengine.searchengine.servlet;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.PriorityQueue;
//
//import org.tartarus.snowball.SnowballStemmer;
//import org.tartarus.snowball.ext.englishStemmer;
//
//import com.tinysearchengine.database.DdbWordDocTfTuple;
//import com.tinysearchengine.indexer.StopWordList;
//import com.tinysearchengine.searchengine.servlet.SearchServlet.UrlScorePair;
//import com.tinysearchengine.searchengine.servlet.SearchServlet.UrlWordPair;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.Scanner;
//
//public class WordDistance {
//	    public int wordEditDistance(String word1, String word2) {
//	        int m = word1.length();
//	        int n = word2.length();
//	        
//	        int[][] distance = new int[m + 1][n + 1];
//	        for(int i = 0; i <= m; i++) { distance[i][0] = i; }
//	        for(int i = 1; i <= n; i++) { distance[0][i] = i; }
//	        
//	        for(int i = 0; i < m; i++) {
//	            for(int j = 0; j < n; j++) {
//	                if(word1.charAt(i) == word2.charAt(j)) { distance[i + 1][j + 1] = distance[i][j]; }
//	                else {
//	                    int distanceIJ = distance[i][j];
//	                    int distanceJplus1 = distance[i][j + 1];
//	                    int distanceIplus1 = distance[i + 1][j];
//	                    distance[i + 1][j + 1] = distanceIJ < distanceJplus1 ? (distanceIJ < distanceIplus1 ? distanceIJ : distanceIplus1) : (distanceJplus1 < distanceIplus1 ? distanceJplus1 : distanceIplus1);
//	                    distance[i + 1][j + 1]++;
//	                }
//	            }
//	        }
//	        return distance[m][n];
//	    }
//	    
//	    public List<String> findTheClosest(String queryTerm) {
//	    	SnowballStemmer d_stemmer = new englishStemmer();
//			String[] terms = queryTerm.split("\\s+"); //get each term in the query string
//			List<String> stemmedTerms = new LinkedList<String>();
//			for (String term : terms) { //traverse through whole terms
//				if (StopWordList.stopwords.contains(term)) {
//					continue;
//				}
//				d_stemmer.setCurrent(term);
//				d_stemmer.stem();
//				stemmedTerms.add(d_stemmer.getCurrent());
//			}
//			
//			for(String curStemmedTerm : stemmedTerms) {
//			    if(isInDictionary(curStemmedTerm, new Scanner(new File("/Users/owner/TinySearchEngine/pagerankinput/macinput.txt"))) {
//			    	continue;
//			    } else {
//			    }
//			    	
//			}
//			
//		    String word = null;
//		    Scanner scan = new Scanner(System.in);
//		    word = scan.nextLine();
//
//		    try {
//		        if(isInDictionary(word, new Scanner(new File("dictionary.txt")))){
//		            System.out.println(word + " is in the dictionary");
//		        } else System.out.println(word + " is NOT in the dictionary");
//		    } catch (FileNotFoundException e) {
//		        // TODO Auto-generated catch block
//		        e.printStackTrace();
//		    }
//		}
//
//		public static boolean isInDictionary(String word, Scanner dictionary){
//
//		    List<String> dictionaryList = new ArrayList<String>();
//		    for(int i = 0; dictionary.hasNextLine() != false; i++){
//		        ++i;
//		        dictionaryList.add(dictionary.nextLine());
//		        if(dictionaryList.get(i) == word){
//		            return true;
//		        }
//		    }
//
//		    return false;
//
//		}
//			
//		
//			
//			
//			
//			
////			dataModel.put("stemmedTerms", stemmedTerms.toString());
//
////			Map<String, Integer> queryTermCounts = new HashMap<>();
////			for (String stemmedTerm : stemmedTerms) {
////				if (queryTermCounts.containsKey(stemmedTerm)) {
////					int c = queryTermCounts.get(stemmedTerm);
////					queryTermCounts.put(stemmedTerm, c + 1);
////				} else {
////					queryTermCounts.put(stemmedTerm, 1);
////				}
////			}
////
////			Map<UrlWordPair, Double> docWordTable = new HashMap<>();
////			for (String stemmedTerm : stemmedTerms) {
////				List<DdbWordDocTfTuple> tuples =
////					d_connector.getWordDocTfTuplesForWord(stemmedTerm);
////
////				for (DdbWordDocTfTuple t : tuples) {
////					UrlWordPair p = new UrlWordPair();
////					p.url = t.getUrl();
////					p.word = t.getWord();
////
////					if (docWordTable.containsKey(p)) {
////						double tf = docWordTable.get(p);
////						docWordTable.put(p, tf + t.getTf());
////					} else {
////						docWordTable.put(p, t.getTf());
////					}
////				}
////			}
////
////			Map<String, Double> docScoreTable = new HashMap<>();
////			for (UrlWordPair p : docWordTable.keySet()) {
////				p.tf = docWordTable.get(p);
////				int wordCountInQuery = queryTermCounts.get(p.word);
////				if (docScoreTable.containsKey(p.url)) {
////					double score = docScoreTable.get(p.url);
////					docScoreTable.put(p.url, score + p.tf * wordCountInQuery);
////				} else {
////					docScoreTable.put(p.url, p.tf * wordCountInQuery);
////				}
////			}
////
////			PriorityQueue<UrlScorePair> queue =
////				new PriorityQueue<>(10, (lhs, rhs) -> {
////					return lhs.score > rhs.score ? -1 : 1;
////				});
////
////			for (String url : docScoreTable.keySet()) {
////				UrlScorePair p = new UrlScorePair();
////				p.url = url;
////				p.score = docScoreTable.get(url);
////				queue.offer(p);
////			}
////
////			return queue.toArray(new UrlScorePair[0]);
////	    }
//	    }
//}
