package org.jibenakka.supervisor;

import akka.actor.ActorRef;

public interface MapReduceSupervisor {

	public abstract void addSubMapSupervisor(ActorRef supervisor);

	public abstract void addSubReduceSupervisor(ActorRef supervisor);

}