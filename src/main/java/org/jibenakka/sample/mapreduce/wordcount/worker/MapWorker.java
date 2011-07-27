package org.jibenakka.sample.mapreduce.wordcount.worker;

import java.util.HashMap;
import java.util.Map;

import org.jibenakka.sample.mapreduce.wordcount.messages.MapWork;

import org.apache.commons.lang.StringUtils;

import akka.actor.UntypedActor;

/**
 * This class is responsible for mapping words by line.
 *
 * @author brlee
 */
public class MapWorker extends UntypedActor {

	private static final String PUNCTUATION = "[.,'\":;*\\[\\]!?()/\\\\]";

	/*
	 * (non-Javadoc)
	 * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		if (!getContext().getSenderFuture().isDefined()) {
			throw new IllegalArgumentException("no sender defined");
		}

		if (message instanceof MapWork) {
			doWork((MapWork) message);
		} else {
			throw new IllegalArgumentException("Unknown message: " + message);
		}
	}

	/**
	 * This performs some light special character cleaning as well as tokenization.
	 *
	 * @param work A message containing the line of words to map.
	 */
	private void doWork(MapWork work) {
		String data = work.getData().toLowerCase()
				.replaceAll("\\B" + PUNCTUATION, StringUtils.EMPTY)
				.replaceAll(PUNCTUATION + "\\B", StringUtils.EMPTY).trim();
		mapAndCombine(work.getKey(), data.split(" "));
	}

	/**
	 * This method performs both mapping and combining. Combining is just
	 * reducing this current map, while normal reduction involves reducing
	 * several maps together.
	 *
	 * @param key The file name. May be deprecated.
	 * @param wordsToCount A single line from a file to examine.
	 */
	protected void mapAndCombine(String key, String[] wordsToCount) {
		Map<String, Integer> countResult = new HashMap<String, Integer>();

		for (int i = 0; i < wordsToCount.length; i++) {
			if (StringUtils.isBlank(wordsToCount[i])) {
				continue;
			}

			Integer count = countResult.get(wordsToCount[i]);

			if (count != null) {
				// Combining
				countResult.put(wordsToCount[i], count + 1);
			} else {
				// Mapping
				countResult.put(wordsToCount[i], 1);
			}
		}
		getContext().channel().sendOneWay(countResult);
	}
}
