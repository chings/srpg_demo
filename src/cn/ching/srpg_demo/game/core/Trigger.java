package cn.ching.srpg_demo.game.core;

abstract public class Trigger {

	protected String[] event;

	public Trigger() {

	}

	public String getTag() {
		return event[0];
	}

	public void setEvent(String[] event) {
		this.event = event;
	}

	public boolean matches(Object[] args) {
		int i = 0;
		for(Object arg : args) {
			if(!event[i++].equals(arg.toString())) {
				return false;
			}
		}
		return true;
	}

	abstract public void handle(Object[] event, Object context);

}
