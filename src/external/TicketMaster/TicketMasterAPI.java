package external.TicketMaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.*;

import entity.Event;
import external.EventSourceAPI;

public class TicketMasterAPI implements EventSourceAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "BWIGzihsOTAQG33GhmXgucZHUH8APAQk";
	private static final int RADIUS = 50;

	@Override
	public List<Event> search(double lat, double lon, String keyword) {
		// change the keyword to default keyword if is not given
		if (keyword == null) keyword = DEFAULT_KEYWORD;
		// format the string to the querying URL
		try {
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		String query = String.format("%s?apikey=%s&geoPoint=%s&keyword=%s&radius=%d",
				URL, API_KEY, geoHash, keyword, RADIUS);
		// query the search to TicketMaster
		try {
			// create new connection
			HttpURLConnection conn = (HttpURLConnection) new URL(query).openConnection();
			conn.setRequestMethod("GET");
			// get the request
			int responseCode = conn.getResponseCode();
			System.out.println("TicketMasterAPI: sent a request with query" + query);
			System.out.println("Response code is " + responseCode);
			// read the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String newline = null;
			while ((newline = reader.readLine()) != null) {
				response.append(newline);
			}
			reader.close();
			// convert the plain text respond to be a list of Event objects
			JSONObject jsonObj = new JSONObject(response.toString());
			// in case got a empty response
			if (jsonObj.isNull("_embedded")) return new ArrayList<>();
			JSONObject embedded = jsonObj.getJSONObject("_embedded");
			JSONArray events = embedded.getJSONArray("events");
			return parseToEvents(events);

		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private List<Event> parseToEvents(JSONArray jArr) {
		List<Event> events = new ArrayList<>();
		for (int i = 0; i < jArr.length(); i++) {
			try {
				JSONObject obj = jArr.getJSONObject(i);
				Event.Builder builder = new Event.Builder();
				builder.setEventId(obj.getString("id"))
						.setName(obj.getString("name"))
						.setEventUrl(obj.getString("url"));
				if (!obj.isNull("distance")) {
					builder.setDistance(obj.getDouble("distance"));
				}				
				builder.setAddress(getAddr(obj));
				builder.setImgUrl(getImgUrl(obj));
				builder.setCategories(getCategories(obj));
				events.add(builder.build());
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		return events;
	}

	private Set<String> getCategories(JSONObject obj) throws JSONException {
		Set<String> cates = new HashSet<>();
		if (! obj.isNull("classifications")) {
			JSONArray classifications = obj.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						String name = segment.getString("name");
						cates.add(name);
					}
				}
			}
		}
		// TODO Auto-generated method stub
		return cates;
	}

	private String getImgUrl(JSONObject obj) throws JSONException {
		if (! obj.isNull("images")) {
			JSONArray images = obj.getJSONArray("images");
			for (int i = 0; i < images.length(); i++) {
				JSONObject img = images.getJSONObject(i);
				if (! img.isNull("url")) return img.getString("url");
			}
		}
		return "";
	}

	private String getAddr(JSONObject obj) throws JSONException {
			if (! obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				if (!embedded.isNull("venues")) {
					JSONArray venues = embedded.getJSONArray("venues");
					//iterate each location of the event
					// only return the first address (with full lines) found
					for (int i = 0; i < venues.length(); ++i) {
						JSONObject venue = venues.getJSONObject(i);
						StringBuilder sb = new StringBuilder();
						if (!venue.isNull("address")) {
							JSONObject address = venue.getJSONObject("address");
							if (!address.isNull("line1")) {
								sb.append(address.getString("line1"));
							}
							if (!address.isNull("line2")) {
								sb.append(" ");
								sb.append(address.getString("line2"));
							}
							if (!address.isNull("line3")) {
								sb.append(" ");
								sb.append(address.getString("line3"));
							}
						}
						if (!venue.isNull("city")) {
							JSONObject city = venue.getJSONObject("city");
							if (!city.isNull("name")) {
								sb.append(",;,");
								sb.append(city.getString("name"));
							}
						}
						if (!venue.isNull("state")) {
							JSONObject state = venue.getJSONObject("state");
							if (!state.isNull("name")) {
								sb.append(",;,");
								sb.append(state.getString("name"));
							}
						}
						if (!sb.toString().equals("")) {
							return sb.toString();
						}
					}
				}
			}
		return "";
	}
}
