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

package org.jibenakka.sample.fault;

import static akka.actor.Actors.actorOf;
import static akka.actor.Actors.registry;

import org.jibenakka.message.StartWork;
import org.jibenakka.supervisor.BaseRootSupervisorImpl;

import akka.actor.ActorRef;

/**
 * Hello world!
 *
 */
public class FaultToleranceApp extends BaseRootSupervisorImpl
{
    public static void main( String[] args )
    {
        ActorRef master = actorOf(FaultToleranceApp.class);
        master.sendOneWay(new StartWork());
    }
    
    public FaultToleranceApp(){
    	setupWorkers(10, aClass);
    }

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof StartWork) {

		} else {
			throw new IllegalArgumentException("Unknown message: " + message);
		}
	}

	@Override
	public ActorRef route(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
