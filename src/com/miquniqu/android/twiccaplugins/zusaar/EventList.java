package com.miquniqu.android.twiccaplugins.zusaar;

import java.util.List;

public class EventList {

	private int results_returned;
	private int results_start;

	private List<Event> event;

	public int getResults_returned() {
		return results_returned;
	}

	public void setResults_returned(int results_returned) {
		this.results_returned = results_returned;
	}

	public int getResults_start() {
		return results_start;
	}

	public void setResults_start(int results_start) {
		this.results_start = results_start;
	}

	public List<Event> getEvent() {
		return event;
	}

	public void setEvent(List<Event> event) {
		this.event = event;
	}

}
