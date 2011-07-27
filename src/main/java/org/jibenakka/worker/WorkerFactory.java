package org.jibenakka.worker;

import org.jibenakka.routing.WorkLoadBalancer;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

public class WorkerFactory implements UntypedActorFactory {
	private final ActorRef[] workers;

	public WorkerFactory(ActorRef[] workers){
		this.workers = workers;
	}

	public UntypedActor create() {
		return new WorkLoadBalancer(workers);
	}
}
