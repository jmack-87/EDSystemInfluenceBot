package com.jmack.EDSystemInfluenceBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.HttpClientBuilder;
import org.omg.CORBA.Environment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Jerimiah Mack
 *
 */
public class SystemFactionInfluence {
	
	
	private static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)"
			+" Chrome/65.0.3325.181 Safari/537.36";
	private static String system = "eotienses";
	private static String systemsUrl = "https://elitebgs.kodeblox.com/api/ebgs/v4/systems?name=";
	private static String factionsUrl = "https://elitebgs.kodeblox.com/api/ebgs/v4/factions?name=";
	
	private static JsonParser jsonParser = new JsonParser();
	private static String factionName = "";
	
	private static StringBuilder sb = new StringBuilder();
	private static String appendix = "";
	

	/**
	 * Given a system name, uses /systems API to get (most) current Faction list, then calls /factions API
	 * to retrieve faction influence level per given system
	 * 
	 * @param args allows for passing in a system name
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String main(String sys) throws ClientProtocolException, IOException {

		
		// if system is provided as argument, set target system to argument
//		if (args.length != 0) {
//			system = args[0].toLowerCase();
//		}
		
//		if (StringUtils.countMatches(sys, "<") > 1) {
//			return "One system only, please. Ex: !influence:<eotienses>";
//		}
		
		
		
		system = sys.trim().toLowerCase();
		
		// pass /systems API url and system, return response (JSON)
		HttpResponse response = getResponse(systemsUrl, system);
		
		// parse response for faction objects (JSON)
		JsonArray factionArray = getFactions(response);
		
		if (factionArray.size() == 0) {
			appendix = String.format("%s", "```");
			sb.append(appendix);
			return sb.toString();
		}
		
		appendix = String.format("%s%n", "```");
		sb.append(appendix);
		
		appendix = String.format("%-20.30s %-40.40s %-24.24s %-10.10s %-8.8s%n",
				"System","Faction", "Updated", "Influence", "State");
		sb.append(appendix);
		
		appendix = String.format("%s%s%n", "------------------------------------------------------",
				"----------------------------------------------------");
		sb.append(appendix);

		// print table headers
//		System.out.printf("%-20.30s %-40.40s %-24.24s %-10.10s %-8.8s%n",
//				"System","Faction", "Updated", "Influence", "State");
//		System.out.println("------------------------------------------------"+
//				"----------------------------------------------------------");
		
		// for each faction object (JSON), get influence
		Iterator<JsonElement> factions = factionArray.iterator();
		while (factions.hasNext()) {
			JsonObject faction = (JsonObject) factions.next();
			factionName = faction.get("name_lower").getAsString();
			// pass /factions API url and faction, return response (JSON)
			response = getResponse(factionsUrl, factionName);
			// print formatted faction details
			
//			System.out.print(getFactionInfluence(response));
			appendix = getFactionInfluence(response);
			sb.append(appendix);
			
		}
		
		appendix = String.format("%s%n", "```");
		sb.append(appendix);
		return sb.toString();
	}
	
	/**
	 * Given a (JSON) http response from /factions API call, parse for influence and timestamp
	 * 
	 * @param response HTTP response as received from API calls
	 * @return String (formatted) faction details
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	private static String getFactionInfluence(HttpResponse response) throws UnsupportedOperationException, IOException {
		
		JsonObject faction;
		
		JsonObject jsonData = getJsonObject(response);
		JsonArray factionArray = jsonData.getAsJsonArray("docs");
		JsonObject ob = factionArray.get(0).getAsJsonObject();
		factionArray = ob.get("faction_presence").getAsJsonArray();
		// trim timestamp of superfluous characters
		String updated = ob.get("updated_at").getAsString().replace("T"," ").replace(".000Z", "");
		
		Iterator<JsonElement> factions = factionArray.iterator();
		while (factions.hasNext()) {
			faction = (JsonObject) factions.next();
			if (faction.get("system_name_lower").getAsString().equals(system)) {
				Float factionInfluence = faction.get("influence").getAsFloat();
				String state = faction.get("state").getAsString();
				return String.format("%-20.30s %-40.40s %-24.24s %-10.3f %-10.10s%n",
						system, factionName, updated, factionInfluence, state);
			}
		}
		return null; // if faction is not found, null
	}
	
	
	/**
	 * Given a (JSON) http response from /systems API call, parse for factions
	 * 
	 * @param response HTTP response as received from API calls
	 * @return JsonArray array for faction objects
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	private static JsonArray getFactions(HttpResponse response) throws UnsupportedOperationException, IOException {
		
		JsonObject jsonData = getJsonObject(response);
		
		if (jsonData.get("total").getAsInt() == 0) {
			appendix = String.format("```No data for %s", system);
			sb.append(appendix);
			return new JsonArray();
		}
		
		JsonArray factionArray = jsonData.getAsJsonArray("docs");
		JsonObject ob = factionArray.get(0).getAsJsonObject();
		factionArray = ob.get("factions").getAsJsonArray();
		String sysUpdate = ob.get("updated_at").getAsString().replace("T"," ").replace(".000Z", "");
		
		appendix = String.format("%s updated at: %s%n", system, sysUpdate);
		sb.append(appendix);
		
		//System.out.format("[DEBUG]: <json %s>%n", factionArray);
		
		return factionArray;
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
	 * Given an (JSON) HttpResponse, parse into JsonObject
	 * 
	 * @param response HttpResponse from API call
	 * @return JsonObject parsed from HttpResponse
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	private static JsonObject getJsonObject(HttpResponse response) throws UnsupportedOperationException, IOException {

		BufferedReader rd = new BufferedReader(
			new InputStreamReader(response.getEntity().getContent()));
	
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return jsonParser.parse(result.toString()).getAsJsonObject();
	}
	
	
	public static void clearMessage() {
		sb = null;
		sb = new StringBuilder();
		
	}
	
	
}
