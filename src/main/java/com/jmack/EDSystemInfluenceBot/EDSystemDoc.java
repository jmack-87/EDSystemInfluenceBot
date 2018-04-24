package com.jmack.EDSystemInfluenceBot;

import java.util.ArrayList;

import net.dv8tion.jda.core.EmbedBuilder;
import java.awt.Color;
import java.time.Instant;

public class EDSystemDoc {
	
	protected String id;
	protected String name_lower;
	protected float x;
	protected float y;
	protected float z;
	protected int __v;
	protected String name;
	protected String government;
	protected String allegiance;
	protected String state;
	protected String security;
	protected long population;
	protected String primary_economy;
	protected String controlling_minor_faction;
	protected String updated_at;
	protected int eddb_id;
	protected ArrayList<EDSystemFaction> factions;
	
	private final String eddb = "https://eddb.io/system/";
	
	
	protected EmbedBuilder toDiscordMessage(EmbedBuilder eb) {
	
		eb.setColor(new Color(15, 27, 147));
		eb.setTitle(":sunny:"+name, eddb+eddb_id);
		eb.setDescription(String.format("@%s", updated_at.replace("T", " ").replace(".000Z","")));
		eb.setTimestamp(Instant.now());
		eb.setFooter("@incizhi0n#8241", "https://cdn.discordapp.com/avatars/412001635259383820/35e38943ed2766595219fa6753e36b4f.png?size=32");

		return eb;
	}
	

}
