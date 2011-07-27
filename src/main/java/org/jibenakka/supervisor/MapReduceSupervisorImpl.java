package org.jibenakka.supervisor;

import static akka.actor.Actors.actorOf;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.List;

import org.jibenakka.worker.WorkerFactory;

/**
 *
 * Things like partion logic has to be user implemented.
 *
 * @author brlee
 *
 */
public abstract class MapReduceSupervisorImpl extends UntypedActor implements MapReduceSupervisor {

	protected ActorRef mapWorkRouter = null;
	protected ActorRef reduceWorkRouter = null;

	protected ActorRef supervisor = null;


	/**
	 * subordinate supervisors
	 */
	protected List<ActorRef> subMapSupervisors;

	/**
	 * subordinate supervisors
	 */
	protected List<ActorRef> subReduceSupervisors;

	@SuppressWarnings("unchecked")
	protected void setupMapWorkers(int numOfWantedWorkers,
			@SuppressWarnings("rawtypes") Class aClass) {
		final ActorRef[] workers = new ActorRef[numOfWantedWorkers];
		for (int i = 0; i < numOfWantedWorkers; i++) {
			workers[i] = actorOf(aClass).start();
			getContext().startLink(workers[i]);
		}

		this.mapWorkRouter = actorOf(new WorkerFactory(workers));
		this.mapWorkRouter.start();
	}

	@SuppressWarnings("unchecked")
	protected void setupReduceWorkers(int numOfWantedWorkers,
			@SuppressWarnings("rawtypes") Class aClass) {
		final ActorRef[] workers = new ActorRef[numOfWantedWorkers];
		for (int i = 0; i < numOfWantedWorkers; i++) {
			workers[i] = actorOf(aClass).start();
			getContext().startLink(workers[i]);
		}

		this.reduceWorkRouter = actorOf(new WorkerFactory(workers));
		this.reduceWorkRouter.start();
	}

	/* (non-Javadoc)
	 * @see org.actorbureau.supervisor.MapReduceSupervisor#addSubMapSupervisor(akka.actor.ActorRef)
	 */
	public void addSubMapSupervisor(ActorRef supervisor){
		this.subMapSupervisors.add(supervisor);
		getContext().startLink(supervisor);
	}

	/* (non-Javadoc)
	 * @see org.actorbureau.supervisor.MapReduceSupervisor#addSubReduceSupervisor(akka.actor.ActorRef)
	 */
	public void addSubReduceSupervisor(ActorRef supervisor){
		this.subReduceSupervisors.add(supervisor);
		getContext().startLink(supervisor);
	}
}