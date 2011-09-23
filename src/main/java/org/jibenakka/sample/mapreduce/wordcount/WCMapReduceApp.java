package org.jibenakka.sample.mapreduce.wordcount;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.registry;
import static akka.dispatch.Futures.sequence;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.Future;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.jibenakka.message.StartWork;
import org.jibenakka.sample.mapreduce.wordcount.messages.InitialMapWork;
import org.jibenakka.sample.mapreduce.wordcount.worker.Mapper;
import org.jibenakka.sample.mapreduce.wordcount.worker.ReduceWorker;
import org.jibenakka.supervisor.MapReduceSupervisorImpl;

import org.apache.commons.io.FileUtils;


/**
 *
 *
 *
 * @author blee
 *
 */
public class WCMapReduceApp extends MapReduceSupervisorImpl {

	private String directoryPath;
	private Integer chunkSize;

	public WCMapReduceApp(int numOfWantedWorkers, String directoryPath,
			Integer chunkSize) {
		setupMapWorkers(numOfWantedWorkers, Mapper.class);
		setupReduceWorkers(numOfWantedWorkers, ReduceWorker.class);

		this.directoryPath = directoryPath;
		this.chunkSize = chunkSize;
	}

	public void start() {
		LinkedList<Future<Map<String, Integer>>> mappedFuturesList = readFiles();
		Future<LinkedList<Map<String, Integer>>> futureMapList = sequence(mappedFuturesList);
		Future<LinkedList<Map<String, Integer>>> reducedMaps = futureMapList
				.flatMap(new ReduceMap(getContext(), this.reduceWorkRouter,
						this.chunkSize));
		printResults(reducedMaps);
		registry().shutdownAll();
	}

	/**
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected LinkedList<Future<Map<String, Integer>>> readFiles() {
		File directoryOfFiles = new File(this.directoryPath);
		Iterator<File> fileIterator = FileUtils.iterateFiles(directoryOfFiles,
				new String[] { "txt" }, false);
		LinkedList<Future<Map<String, Integer>>> mappedFuturesList = new LinkedList<Future<Map<String, Integer>>>();

		while (fileIterator.hasNext()) {
			// Start the futures here. Pair mapping futures with Partition /
			// Reduce futures
			InitialMapWork work = new InitialMapWork((File) fileIterator.next());
			LinkedList<Future<Map<String, Integer>>> futureMaps = (LinkedList<Future<Map<String, Integer>>>) this.mapWorkRouter
					.sendRequestReply(work, getContext());
			mappedFuturesList.addAll(futureMaps);
		}
		return mappedFuturesList;
	}

	protected void printResults(
			Future<LinkedList<Map<String, Integer>>> futureListOfMaps) {
		futureListOfMaps.await();

		if (futureListOfMaps.isCompleted()) {
			LinkedList<Map<String, Integer>> finalResultList = futureListOfMaps
					.get();

			Map<String, Integer> finalResult = finalResultList.get(0);
			System.out
					.println("=================MAP REDUCE WORD COUNT RESULTS===============");

			Set<String> keys = finalResult.keySet();
			Integer count = 0;
			for (String word : keys) {
				System.out.print(String.format("[%s][%s] ", word,
						finalResult.get(word)));

				if (count > 5) {
					System.out.println();
					count = 0;
				} else {
					count = count + 1;
				}
			}
		}
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof StartWork) {
			start();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ActorRef actor = actorOf(new UntypedActorFactory() {
			public UntypedActor create() {
				return new WCMapReduceApp(10,
						"/Users/brlee/Workspace/WordCountFiles", 3);
			}
		});
		actor.start();
		actor.sendOneWay(new StartWork());
	}
}
