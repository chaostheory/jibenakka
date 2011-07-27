package org.jibenakka.sample.mapreduce.wordcount.messages;

import java.util.LinkedList;
import java.util.Map;

import akka.dispatch.Future;

/**
 * A set of reduction results. The size will depend on the chosen chunkSize.
 *
 * @author brlee
 *
 */
public class ReduceSetWork {
	private final LinkedList<Future<Map<String, Integer>>> reduceWorkList;

	public ReduceSetWork(LinkedList<Future<Map<String, Integer>>> reduceWorkList){
		this.reduceWorkList = reduceWorkList;
	}

	public LinkedList<Future<Map<String, Integer>>> getReduceList() {
		return reduceWorkList;
	}
}