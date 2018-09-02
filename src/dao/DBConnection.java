package dao;

import java.util.List;
import java.util.Set;

import entity.Event;

public interface DBConnection extends AutoCloseable{
	
	//				STRUCTURE OF THE DATABSE:
	//						 database
	//				/		/		\		\
	//		categories	events  favorites  users
	//
	//						  events
	//			/		/		|		\		\
	//	    event_id  name	address	 event_url  img_url
	//
	// 						categories
	//   					/		\
	//				   event_id  category
	//
	// 						favorites
	//						/		\
	//					event_id  user_id
	//
	//						   users
	//			/			/			\			\
	//		user_id		password	first_name	last_name
	//
	
	public void close();
	
	public void setFavoriteEvents(String userId, List<String> eventIds);
	
	public void unsetFavoriteEvents(String userId, List<String> eventIds);
	
	public Set<String> getFavoriteEventIds(String userId);
	
	public Set<Event> getFavoriteEvents(String userId);

	public void saveEvents(List<Event> events);
}
