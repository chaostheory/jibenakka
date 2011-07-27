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

package org.jibenakka.message;

public abstract class BaseWorkMessageImpl extends BaseMessageImpl {

	/**
	 * The id of the Actor instance who will recipient of the result derived
	 * from this work message. i.e. Once a worker finishes processing the work,
	 * it will send a ResultMessage instance to an Actor with this ID.
	 */
	protected final String actorRecipientId;

	public BaseWorkMessageImpl(String actorRecipientId) {
		this.actorRecipientId = actorRecipientId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.actorbureau.message.BaseMessage#getActorRecipientId()
	 */
	public String getActorRecipientId() {
		return actorRecipientId;
	}
}
