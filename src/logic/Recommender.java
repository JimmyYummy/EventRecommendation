package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dao.DBConnection;
import dao.DBConnectionFactory;
import entity.Event;
import external.EventSourceAPI;
import external.TicketMaster.TicketMasterAPI;

public class Recommender {
	public List<Event> recommendEvents(String userId, double lat, double lon) {
		// get previous favorite items
		DBConnection conn = DBConnectionFactory.getConnection();
		Set<String> favoritedEventIds = conn.getFavoriteEventIds(userId);
		Map<String, Integer> favoritedCategories = new HashMap<>();
		for (String eventId : favoritedEventIds) {
			Set<String> categories = conn.getCategoriesFromDB(eventId);
			for (String category : categories) {
				favoritedCategories.put(category, favoritedCategories.getOrDefault(category, 0) + 1);
			}
		}

		// sort the favorite categories by frequency
		List<Entry<String, Integer>> categoryList = new ArrayList<> (favoritedCategories.entrySet());
		Collections.sort(categoryList, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});

		//search nearby events based on categories and filter out the favorite events
		List<Event> recommendedEvents = new ArrayList<>();
		EventSourceAPI source = new TicketMasterAPI();
		for (Entry<String, Integer> category : categoryList) {
			List<Event> filteredEvents = new ArrayList<>();
			List<Event> events = source.search(lat, lon, category.getKey());
			for (Event event : events) {
				if (! favoritedEventIds.contains(event.getEventId())) {
					filteredEvents.add(event);
					favoritedEventIds.add(event.getEventId());
				}
			}
			Collections.sort(filteredEvents, new Comparator<Event>() {
				@Override
				public int compare(Event item1, Event item2) {
					return Double.compare(item1.getDistance(), item2.getDistance());
				}
			});
			recommendedEvents.addAll(filteredEvents);
		}
		return recommendedEvents;

	}
}
