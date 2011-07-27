package org.jibenakka.supervisor;

import akka.actor.ActorRef;

public interface BaseSupervisor {

	public ActorRef getWorkRouter();

	public void setWorkRouter(ActorRef workRouter);

	public ActorRef getSupervisor();

	public void setSupervisor(ActorRef supervisor);

	public void addSubSupervisor(ActorRef supervisor);

}