package org.jibenakka.supervisor;

import static akka.actor.Actors.actorOf;

import java.util.List;
import java.util.Vector;

import org.jibenakka.worker.WorkerFactory;

import akka.actor.ActorRef;
import akka.routing.UntypedDispatcher;

/**
 *
 * This assumes only one set of workers.
 *
 * Non-abstract children must call the setupWorkers method during initialization.
 *
 * @author brlee
 *
 */
public abstract class BaseRootSupervisorImpl extends UntypedDispatcher implements BaseSupervisor {
	protected ActorRef workRouter = null;
	protected ActorRef supervisor = null;
	/**
	 * subordinate supervisors
	 */
	protected List<ActorRef> subSupervisors;

	public BaseRootSupervisorImpl(){
		this.subSupervisors = new Vector<ActorRef>();
	}

	/* (non-Javadoc)
	 * @see org.actorbureau.supervisor.BaseSupervisor#getWorkRouter()
	 */
	public ActorRef getWorkRouter() {
		return workRouter;
	}
	/* (non-Javadoc)
	 * @see org.actorbureau.supervisor.BaseSupervisor#setWorkRouter(akka.actor.ActorRef)
	 */
	public void setWorkRouter(ActorRef workRouter) {
		this.workRouter = workRouter;
	}
	/* (non-Javadoc)
	 * @see org.actorbureau.supervisor.BaseSupervisor#getSupervisor()
	 */
	public ActorRef getSupervisor() {
		return supervisor;
	}
	/* (non-Javadoc)
	 * @see org.actorbureau.supervisor.BaseSupervisor#setSupervisor(akka.actor.ActorRef)
	 */
	public void setSupervisor(ActorRef supervisor) {
		this.supervisor = supervisor;
	}

	@SuppressWarnings("unchecked")
	protected void setupWorkers(int numOfWantedWorkers,
			@SuppressWarnings("rawtypes") Class aClass) {
		final ActorRef[] workers = new ActorRef[numOfWantedWorkers];
		for (int i = 0; i < numOfWantedWorkers; i++) {
			workers[i] = actorOf(aClass).start();
			getContext().startLink(workers[i]);
		}

		this.workRouter = actorOf(new WorkerFactory(workers));
		this.workRouter.start();
	}

	/* (non-Javadoc)
	 * @see org.actorbureau.supervisor.BaseSupervisor#addSubSupervisor()
	 */
	public void addSubSupervisor(ActorRef supervisor){
		this.subSupervisors.add(supervisor);
		getContext().startLink(supervisor);
	}
}
