/**
 * Copyright (c) 2011 Brian Lee
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons 
 * to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
		Function<Iterable<Map<String, Integer>>, Future<Iterable<Map<String, Integer>>>> {

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
	public Future<Iterable<Map<String, Integer>>> apply(
			LinkedList<Map<String, Integer>> mapsToReduce) {

		LinkedList<Future<Map<String, Integer>>> reduceWorkList = partitionIntialMap(mapsToReduce);
		LinkedList<Future<Map<String, Integer>>> result = partitionMap(reduceWorkList);
		//Future<LinkedList<Map<String, Integer>>> finalResult = sequence(result);
		Future<Iterable<Map<String, Integer>>> finalResult = sequence(result);

		return finalResult;
	}

	/**
	 *
	 * @param mapsToReduce
	 * @return
	 */
	protected LinkedList<Future<Object>> partitionIntialMap(
			LinkedList<Object> mapsToReduce) {
		LinkedList<Future<Object>> reduceWorkList = new LinkedList<Future<Object>>();
		LinkedList<Object> chunkWorkList = new LinkedList<Object>();

		while (!mapsToReduce.isEmpty()
				|| (mapsToReduce.isEmpty() && !chunkWorkList.isEmpty())) {
			// divide the work into chunks
			if ((chunkWorkList.size() >= this.chunkSize)
					|| mapsToReduce.isEmpty()) {
				// the work router is essentially a partitioner
				Future<Object> futureReduceResult = this.workRouter
						.ask(new ReduceMapSetWork(
								chunkWorkList), 30000, this.owner);

				reduceWorkList.add(futureReduceResult);
				chunkWorkList = new LinkedList<Object>();
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
	private LinkedList<Future<Object>> partitionMap(
			LinkedList<Future<Object>> futureMapsList) {
		if (futureMapsList.size() < 2) {
			return futureMapsList;
		}

		LinkedList<Future<Object>> results = new LinkedList<Future<Object>>();
		LinkedList<Future<Object>> chunkWorkList = new LinkedList<Future<Object>>();

		while (!futureMapsList.isEmpty()
				|| (futureMapsList.isEmpty() && !chunkWorkList.isEmpty())) {

			if ((chunkWorkList.size() >= this.chunkSize)
					|| (futureMapsList.isEmpty())) {
				Future<Object> newFuture = this.workRouter
						.ask(new ReduceSetWork(chunkWorkList), 30000,
								this.owner);
				results.add(newFuture);
				chunkWorkList = new LinkedList<Future<Object>>();
			}

			if (!futureMapsList.isEmpty()) {
				chunkWorkList.add(futureMapsList.poll());
			}
		}

		return partitionMap(results);
	}
}