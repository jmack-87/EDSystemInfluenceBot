package com.jmack.EDSystemInfluenceBot;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Jerimiah Mack
 *
 */
public class EDFactionPresence {
	
	protected float influence;
	protected String state;
	protected String system_name_lower;
	protected String system_name;
	protected ArrayList<EDFactionRecoveringState> recovering_states;
	protected ArrayList<EDFactionPendingState> pending_states;
	
	
	/**
	 * Build and return a string composed of all recovering state and corresponding trend
	 * @return String 
	 */
	protected String getRecoveringStates() {
		
		String trendReplace = null;
		StringBuilder s = new StringBuilder();
		
		if (recovering_states.size() < 1) {
			return "---";
		}
		
		Iterator<EDFactionRecoveringState> recoveringStates = recovering_states.iterator();
		while (recoveringStates.hasNext()) {
			EDFactionRecoveringState recState = recoveringStates.next();
			
			switch (recState.trend) {
				case 1: trendReplace = "+";
					break;
				case 0: trendReplace = "0";
					break;
				case -1: trendReplace = "-";
					break;
			}	
			s.append(String.format("**%s**[%s] ", StringUtils.capitalize(recState.state), trendReplace));
		}
		return s.toString();
	}

	
	/**
	 * Build and return a string composed of all pending state and corresponding trend
	 * @return String
	 */
	protected String getPendingStates() {
		
		String trendReplace = null;
		StringBuilder s = new StringBuilder();
		
		if (pending_states.size() < 1) {
			return "---";
		}
		
		Iterator<EDFactionPendingState> pendingStates = pending_states.iterator();
		while (pendingStates.hasNext()) {
			EDFactionPendingState pendState = pendingStates.next();
			
			switch (pendState.trend) {
			case 1: trendReplace = "+";
				break;
			case 0: trendReplace = "0";
				break;
			case -1: trendReplace = "-";
				break;
			}
			s.append(String.format("**%s**[%s] ", StringUtils.capitalize(pendState.state), trendReplace));
		}
		return s.toString();
	}
	
}
