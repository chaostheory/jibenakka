package org.jibenakka.sample.mapreduce.wordcount.messages;


public class MapWork {
	private final String key;

	private final String data;

	/**
	 *
	 * @param key
	 * @param data
	 */
	public MapWork(String key, String data){
		this.key = key;
		this.data = data;
	}

	public String getKey() {
		return key;
	}

	public String getData() {
		return data;
	}
}
