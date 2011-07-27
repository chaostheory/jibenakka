package org.jibenakka.message;

public class MapWorkMessageImpl {
	protected final Object key;

	public MapWorkMessageImpl(Object key, Object dat){
		this.key = key;
	}

	public Object getKey() {
		return key;
	}
}