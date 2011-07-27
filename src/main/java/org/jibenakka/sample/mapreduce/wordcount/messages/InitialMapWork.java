package org.jibenakka.sample.mapreduce.wordcount.messages;

import java.io.File;


public class InitialMapWork {
	private final File fileToCount;

	/**
	 *
	 * @param key
	 * @param data
	 */
	public InitialMapWork(File fileToCount){
		this.fileToCount = fileToCount;
	}

	public File getFileToCount() {
		return fileToCount;
	}
}