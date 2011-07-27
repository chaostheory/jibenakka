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

import java.util.List;

import org.jibenakka.worker.WorkerFactory;

import akka.actor.ActorRef;
import akka.routing.UntypedDispatcher;


/**
 *
 * Things like partion logic has to be user implemented.
 *
 * @author blee
 *
 */
public abstract class MapReduceRootSupervisorImpl extends UntypedDispatcher
		implements MapReduceSupervisor {

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

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.actorbureau.supervisor.MapReduceSupervisor#addSubMapSupervisor(akka
	 * .actor.ActorRef)
	 */
	public void addSubMapSupervisor(ActorRef supervisor) {
		this.subMapSupervisors.add(supervisor);
		getContext().startLink(supervisor);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.actorbureau.supervisor.MapReduceSupervisor#addSubReduceSupervisor
	 * (akka.actor.ActorRef)
	 */
	public void addSubReduceSupervisor(ActorRef supervisor) {
		this.subReduceSupervisors.add(supervisor);
		getContext().startLink(supervisor);
	}
}