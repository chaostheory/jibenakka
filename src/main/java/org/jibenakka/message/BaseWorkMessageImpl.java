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
