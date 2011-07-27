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

package org.jibenakka.sample.mapreduce.wordcount.worker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jibenakka.sample.mapreduce.wordcount.messages.ReduceMapSetWork;
import org.jibenakka.sample.mapreduce.wordcount.messages.ReduceSetWork;

import akka.actor.UntypedActor;
import akka.dispatch.Future;

public class ReduceWorker extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (!getContext().getSenderFuture().isDefined()) {
			throw new IllegalArgumentException("no sender defined");
		}

		if (message instanceof ReduceSetWork) {
			doReduceWork((ReduceSetWork) message);
		} else if (message instanceof ReduceMapSetWork) {
			doInitialMapReduceWork((ReduceMapSetWork) message);
		} else {
			throw new IllegalArgumentException("Unknown message: " + message);
		}
	}

	/**
	 *
	 * @param work
	 */
	private void doReduceWork(ReduceSetWork work) {
		List<Future<Map<String, Integer>>> futureReduceWork = work
				.getReduceList();
		Map<String, Integer> mapResult = new HashMap<String, Integer>();

		for (Future<Map<String, Integer>> future : futureReduceWork) {
			future.await();

			if (future.isCompleted()) {
				Map<String, Integer> mapToReduce = future.get();
				Set<String> keys = mapToReduce.keySet();

				for (String key : keys) {
					Integer resultVal = mapResult.get(key);

					if (resultVal != null) {
						mapResult.put(key, resultVal + mapToReduce.get(key));
					} else {
						mapResult.put(key, mapToReduce.get(key));
					}
				}
			}
		}

		getContext().channel().sendOneWay(mapResult);
	}

	private void doInitialMapReduceWork(ReduceMapSetWork work) {
		LinkedList<Map<String, Integer>> mapResults = work.getMapResults();
		Map<String, Integer> countResultToReturn = new HashMap<String, Integer>();

		for (Map<String, Integer> counts : mapResults) {
			Set<String> keys = counts.keySet();

			for (String word : keys) {
				Integer existingVal = countResultToReturn.get(word);

				if (existingVal != null) {
					countResultToReturn.put(word, counts.get(word)
							+ existingVal);
				} else {
					countResultToReturn.put(word, counts.get(word));
				}
			}
		}

		getContext().channel().sendOneWay(countResultToReturn);
	}

}