package org.jibenakka.sample.mapreduce.wordcount.messages;

import java.util.LinkedList;
import java.util.Map;

public class ReduceMapSetWork {
	private final LinkedList<Map<String, Integer>> mapResults;

	public ReduceMapSetWork(LinkedList<Map<String, Integer>> mapResults){
		this.mapResults = mapResults;
	}

	public LinkedList<Map<String, Integer>> getMapResults() {
		return mapResults;
	}
}
