package org.jibenakka.sample.mapreduce.wordcount;

import static akka.dispatch.Futures.sequence;
import akka.actor.ActorRef;
import akka.dispatch.Future;
import akka.japi.Function;

import java.util.LinkedList;
import java.util.Map;

import org.jibenakka.sample.mapreduce.wordcount.messages.ReduceMapSetWork;
import org.jibenakka.sample.mapreduce.wordcount.messages.ReduceSetWork;

/**
 * The main purpose of this class is the manage AKKA futures. Incidentally it also
 * helps partition the work.
 *
 * @author blee
 */
public class ReduceMap
		implements
		Function<LinkedList<Map<String, Integer>>, Future<LinkedList<Map<String, Integer>>>> {

	private ActorRef workRouter;
	private ActorRef owner;
	private final Integer chunkSize;

	/**
	 *
	 * @param owner
	 *            The actor that is both the owner of the workers and the sender
	 *            of the work.
	 * @param workRouter This instance essentially partitions the work to workers.
	 * @param chunkSize The size of the work in lines.
	 */
	public ReduceMap(ActorRef owner, ActorRef workRouter, Integer chunkSize) {
		this.workRouter = workRouter;
		this.owner = owner;
		this.chunkSize = chunkSize;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see akka.japi.Function#apply(java.lang.Object)
	 */
	public Future<LinkedList<Map<String, Integer>>> apply(
			LinkedList<Map<String, Integer>> mapsToReduce) {

		LinkedList<Future<Map<String, Integer>>> reduceWorkList = partitionIntialMap(mapsToReduce);
		LinkedList<Future<Map<String, Integer>>> result = partitionMap(reduceWorkList);
		Future<LinkedList<Map<String, Integer>>> finalResult = sequence(result);

		return finalResult;
	}

	/**
	 *
	 * @param mapsToReduce
	 * @return
	 */
	protected LinkedList<Future<Map<String, Integer>>> partitionIntialMap(
			LinkedList<Map<String, Integer>> mapsToReduce) {
		LinkedList<Future<Map<String, Integer>>> reduceWorkList = new LinkedList<Future<Map<String, Integer>>>();
		LinkedList<Map<String, Integer>> chunkWorkList = new LinkedList<Map<String, Integer>>();

		while (!mapsToReduce.isEmpty()
				|| (mapsToReduce.isEmpty() && !chunkWorkList.isEmpty())) {
			// divide the work into chunks
			if ((chunkWorkList.size() >= this.chunkSize)
					|| mapsToReduce.isEmpty()) {
				// the work router is essentially a partitioner
				Future<Map<String, Integer>> futureReduceResult = this.workRouter
						.sendRequestReplyFuture(new ReduceMapSetWork(
								chunkWorkList), 30000, this.owner);

				reduceWorkList.add(futureReduceResult);
				chunkWorkList = new LinkedList<Map<String, Integer>>();
			}

			if (!mapsToReduce.isEmpty()) {
				chunkWorkList.add(mapsToReduce.poll());
			}
		}
		return reduceWorkList;
	}

	/**
	 *
	 * @param futureMapsList
	 * @return
	 */
	private LinkedList<Future<Map<String, Integer>>> partitionMap(
			LinkedList<Future<Map<String, Integer>>> futureMapsList) {
		if (futureMapsList.size() < 2) {
			return futureMapsList;
		}

		LinkedList<Future<Map<String, Integer>>> results = new LinkedList<Future<Map<String, Integer>>>();
		LinkedList<Future<Map<String, Integer>>> chunkWorkList = new LinkedList<Future<Map<String, Integer>>>();

		while (!futureMapsList.isEmpty()
				|| (futureMapsList.isEmpty() && !chunkWorkList.isEmpty())) {

			if ((chunkWorkList.size() >= this.chunkSize)
					|| (futureMapsList.isEmpty())) {
				Future<Map<String, Integer>> newFuture = this.workRouter
						.sendRequestReplyFuture(
								new ReduceSetWork(chunkWorkList), 30000,
								this.owner);
				results.add(newFuture);
				chunkWorkList = new LinkedList<Future<Map<String, Integer>>>();
			}

			if (!futureMapsList.isEmpty()) {
				chunkWorkList.add(futureMapsList.poll());
			}
		}

		return partitionMap(results);
	}
}