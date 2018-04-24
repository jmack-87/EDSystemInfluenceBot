package com.jmack.EDSystemInfluenceBot;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import net.dv8tion.jda.core.EmbedBuilder;

public class EDFactionDoc {
	
	protected String id;
	protected String name;
	protected int __v;
	protected String name_lower;
	protected String updated_at;
	protected String government;
	protected String allegiance;
	protected int eddb_id;
	protected ArrayList<EDFactionPresence> faction_presence;
		
	private static final String eddb = "http://eddb.io/faction/";
	

	public EmbedBuilder toDiscordMessage(EmbedBuilder eb, String system) {
		
		Iterator<EDFactionPresence> factionPresences = faction_presence.iterator();
		while (factionPresences.hasNext()) {
			EDFactionPresence presence = factionPresences.next();
			
			if (presence.system_name_lower.equals(system)) {

				eb.addField(String.format("%5.2f%%%n", presence.influence*100),				//influence [Title]
						String.format("**[%-28.28s](%s%d)**%n@%s%n*Government:* **%s**%n*State:* **%s**%n*Pending:* %s%n*Recovering:* %s%n-\t-\t-",						
								name, eddb, eddb_id,										//link [Value]
								updated_at.replace("T", " ").replaceAll(".000Z", ""),		//updated [Value]
								StringUtils.capitalize(government),							//gonvmt [Value]
								StringUtils.capitalize(presence.state),						//state [Value]
								presence.getPendingStates(),								//pending [Value(s)]
								presence.getRecoveringStates()),							//recovering [Value(s)]
						true);
			}
				
		}
		return eb;
	}
}
