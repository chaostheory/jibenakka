package org.jibenakka.sample.untyped;

import org.jibenakka.message.StartWork;
import org.jibenakka.supervisor.BaseSupervisorImpl;

public class Master extends BaseSupervisorImpl {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof StartWork) {

		} else {
			throw new IllegalArgumentException("Unknown message: " + message);
		}
	}
}
