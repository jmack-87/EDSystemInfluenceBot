package com.jmack.EDSystemInfluenceBot;

//import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;

import net.dv8tion.jda.core.EmbedBuilder;


/**
 * 
 * @author Jerimiah Mack
 *
 */
public class SystemFactionInfluence {
	
	
	private static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
			+" AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
	private static String system = "eotienses";
	private static String systemsUrl = "https://elitebgs.kodeblox.com/api/ebgs/v4/systems?name=";
	private static String factionsUrl = "https://elitebgs.kodeblox.com/api/ebgs/v4/factions?name=";
	
	
	private static EmbedBuilder eb = new EmbedBuilder();
	
	
	/**
	 * Given a system name, uses /systems API to get (most) current Faction list, then calls /factions API
	 * to retrieve faction influence level per given system
	 * 
	 * @param args allows for passing in a system name
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static EmbedBuilder main(String sys) throws ClientProtocolException, IOException {

		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
		String appendix = null;
		EDSystemDoc sysDoc = null;
		ArrayList<EDFaction> factionArray = new ArrayList<EDFaction>();
		EDSystem systemObject = null;
		HttpResponse response = null;
		EDSystemFaction faction = null;
		EDFactionDoc factionDoc = null;
		ArrayList<EDFactionDoc> factionDocsArray = new ArrayList<EDFactionDoc>();
		
		system = sys.trim().toLowerCase();
		
		
		// pass /systems API url and system, return response (JSON)
		response = getResponse(systemsUrl, system);
		
		if (response.getStatusLine().getStatusCode() != 200) {
			appendix = String.format("```[%s] data unavailable. Try again later.```", system);
			sb.append(appendix);
			//return sb.toString();
		}
		
		
		systemObject = gson.fromJson(new InputStreamReader(response.getEntity().getContent()), EDSystem.class);
		
		Iterator<EDSystemDoc> sysDocsIterator = systemObject.docs.iterator(); // should only ever be 1
		while (sysDocsIterator.hasNext()) {
			
			sysDoc = sysDocsIterator.next();
			
			eb = sysDoc.toDiscordMessage(eb);
			
			Iterator<EDSystemFaction> sysFactions = sysDoc.factions.iterator();
			while (sysFactions.hasNext()) {
				
				faction = sysFactions.next();
				response = getResponse(factionsUrl, faction.name_lower);
				
				if (response.getStatusLine().getStatusCode() != 200) {
					appendix = String.format("```[%s] data unavailable. Try again later.```", faction.name_lower);
					sb.append(appendix);
				}
				
				factionArray.add(gson.fromJson(new InputStreamReader(response.getEntity().getContent()), EDFaction.class));
			}
		}
		
		Iterator<EDFaction> factions = factionArray.iterator();
		while (factions.hasNext()) {
			factionDocsArray = factions.next().docs;
			
			Iterator<EDFactionDoc> factionDocs = factionDocsArray.iterator();
			while (factionDocs.hasNext()) { // should only be one (1) docs per faction
				factionDoc = factionDocs.next();
				
				eb = factionDoc.toDiscordMessage(eb, system);
				
			}
		}
		
		return eb;
	}
	
	
	/**
	 * Given an PAI endpoint url and corresponding query parameter, make the API call
	 * 
	 * @param url String API endpoint URL
	 * @param param	String parameter for API query
	 * @return HttpResponse response (JSON) from API call
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static HttpResponse getResponse(String url, String param) throws ClientProtocolException, IOException {
		
		HttpClient client = HttpClientBuilder.create()
				.disableCookieManagement() // stops some console warnings which interrupt console table output
				.build();
		
		
		//System.out.format("[DEBUG]: <url %s%s>%n", url, URLEncoder.encode(param, "UTF-8").replace("+","%20"));
		// URLEncoder leaves whitespace as '+', we need '%20'
		HttpGet request = new HttpGet(url + URLEncoder.encode(param, "UTF-8").replace("+","%20"));
		
		// add request headers (probably not necessary, but benign)
		request.addHeader("User-Agent", USER_AGENT);
		request.addHeader("Accept", "application/json");

		return client.execute(request);	
	}
	
	/**
	 * Clear the message buffer
	 */
	public static void purgeEmbed() {
		eb = new EmbedBuilder();
	}
	
	
}
