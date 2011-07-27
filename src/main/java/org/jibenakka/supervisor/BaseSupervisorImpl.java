/**
 * Copyright (c) 2011 Brian Lee
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons 
 * to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jibenakka.supervisor;

import static akka.actor.Actors.actorOf;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.List;
import java.util.Vector;

import org.jibenakka.worker.WorkerFactory;

/**
 *
 * This assumes only one set of workers.
 *
 * Non-abstract children must call the setupWorkers method during initialization.
 *
 * @author blee
 *
 */
public abstract class BaseSupervisorImpl extends UntypedActor implements BaseSupervisor {
	protected ActorRef workRouter = null;
	protected ActorRef supervisor = null;
	/**
	 * subordinate supervisors
	 */
	protected List<ActorRef> subSupervisors;

	public BaseSupervisorImpl(){
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
