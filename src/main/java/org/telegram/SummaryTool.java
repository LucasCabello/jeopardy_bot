package org.telegram;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SummaryTool {

	public String[] splitToSentences(String content) {
		content = content.replaceAll("\n", "\\. ");
		return content.split("\\. ");
	}
	
	public String[] splitToParagraphs(String content) {
		return content.split("\n\n");
	}
	
	public Double sentencesIntersection(String s1, String s2) {
		String[] s1Set = s1.split(" ");
		String[] s2Set = s2.split(" ");
		Set<String> setOne = new HashSet<String>(Arrays.asList(s1Set));
		Set<String> setTwo = new HashSet<String>(Arrays.asList(s2Set));
		int sizeSum = setOne.size() + setTwo.size();
		if(sizeSum == 0) {
			return 0d;
		}
		Set<String> intersection = setOne;
		intersection.retainAll(setTwo);
//		System.out.println(((double)intersection.size())/(sizeSum/2.0));
		
		return (((double)intersection.size())/(sizeSum/2.0));
	}
	
	public String formatSentence(String sent) {
		String nSent = sent.replaceAll("\\W+", "");
		return nSent;
	}
	
	public HashMap<String, Double> sentencesRank(String content) {
		String[] sentences = content.split("\\. ");
		int n = sentences.length;
//		System.out.println(n);
		Double[][] values = new Double[n][n];
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				values[i][j] = sentencesIntersection(sentences[i], sentences[j]);
		
		HashMap<String, Double> dic = new HashMap<String, Double>();
		for(int i = 0; i < n; i++) {
			double score = 0;
			for(int j = 0; j < n; j++) {
				if (i == j){
					continue;
				}
				score += values[i][j];
			}
//			System.out.println(sentences[i]+"; ;"+score);
			dic.put(formatSentence(sentences[i]), score);
		}
		return dic;
	}
	
	public String bestSentence(String paragraph, HashMap<String, Double> dic) {
		String[] sentences = paragraph.split("\\. ");
		if (sentences.length < 2)
			return sentences[0];
		
		String bestSentence = "";
		String secondBest = "";
		Double max_value = 0d;
		
		for(String s : sentences) {
			//System.out.println(s);
			String strip_s = formatSentence(s);
			if (!strip_s.isEmpty()){
				if(dic.get(strip_s) - max_value >= 0.0) {
					max_value = dic.get(strip_s);
					bestSentence = s;
				} else {
					if (secondBest.equals("")){
						secondBest = s;
					}
					else 
						if (dic.get(strip_s) > dic.get(formatSentence(secondBest)))
							secondBest = s;
				}
			}
		}
		bestSentence = bestSentence.length() > 10? bestSentence : "";
		secondBest = secondBest.length() > 10? secondBest : "";
		String result = bestSentence.isEmpty()? secondBest :
			(secondBest.isEmpty()? bestSentence:(bestSentence+"\n\t"+secondBest));
		return result;
		
	}
}
