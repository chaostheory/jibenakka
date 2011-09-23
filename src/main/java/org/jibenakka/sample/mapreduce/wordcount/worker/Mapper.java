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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import org.jibenakka.sample.mapreduce.wordcount.messages.InitialMapWork;
import org.jibenakka.sample.mapreduce.wordcount.messages.MapWork;
import org.jibenakka.supervisor.BaseSupervisorImpl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import akka.dispatch.Future;

/**
 * This splits each file it receives into lines, which it feeds into its
 * MapWorkers to map the words and the count as the key and value respectively.
 *
 * @author blee
 */
public class Mapper extends BaseSupervisorImpl {

	private Integer chunkSize;

	public Mapper() {
		this(10, 3);
	}

	/**
	 *
	 * @param numOfWorkers
	 */
	public Mapper(int numOfWorkers, int chunkSize) {
		setupWorkers(numOfWorkers, MapWorker.class);
		this.chunkSize = chunkSize;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (!getContext().getSenderFuture().isDefined()) {
			throw new IllegalArgumentException("no sender defined");
		}

		if (message instanceof InitialMapWork) {
			doWork(message);
		} else {
			throw new IllegalArgumentException("Unknown message: " + message);
		}
	}

	/**
	 *
	 * @param message
	 */
	protected void doWork(Object message) {
		InitialMapWork work = (InitialMapWork) message;
		// Start dividing the work into portions of the file
		extractFileMapResult(work);
	}

	/**
	 *
	 * @param work
	 */
	protected void extractFileMapResult(InitialMapWork work) {
		LinkedList<Future<Map<String, Integer>>> futureResults = new LinkedList<Future<Map<String, Integer>>>();
		LineIterator lineIterator = null;
		try {
			File fileToCount = work.getFileToCount();
			lineIterator = FileUtils.lineIterator(fileToCount);

			while (lineIterator.hasNext()) {
				// All work including special character handling done at worker
				// level
				String line = lineIterator.nextLine();
				// key is just the file name - initial mapping is easy
				// hard part comes with partitioning and reduction
				// we assume that we have unique file names in the dir
				MapWork newWork = new MapWork(fileToCount.getName(), line);

				Future<Map<String, Integer>> future = this.workRouter
						.sendRequestReplyFuture(newWork, 30000, getContext());
				future.await();
				futureResults.add(future);
			}

			// FinalMapResult result = new FinalMapResult(
			// (LinkedList<Future<Map<String, Integer>>>) futureResults);
			getContext().channel().sendOneWay(futureResults);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (lineIterator != null) {
				lineIterator.close();
			}
		}
	}
}