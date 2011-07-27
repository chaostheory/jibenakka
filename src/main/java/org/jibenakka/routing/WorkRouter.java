package org.jibenakka.routing;

import static java.util.Arrays.asList;

import akka.actor.ActorRef;
import akka.routing.CyclicIterator;
import akka.routing.InfiniteIterator;
import akka.routing.UntypedLoadBalancer;

public class WorkRouter extends UntypedLoadBalancer {
	private final InfiniteIterator<ActorRef> workers;

	public WorkRouter(ActorRef[] workers){
		this.workers = new CyclicIterator<ActorRef>(asList(workers));
	}

	@Override
	public InfiniteIterator<ActorRef> seq() {
		return this.workers;
	}
}