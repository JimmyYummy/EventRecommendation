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
	//			|
	//     primary key
	//
	// 						categories
	//   					/		\
	//				   event_id  category
	//					/	   \     /
	//				foreign key \   /
	//						 primary key
	//
	// 						favorites
	//						/		\
	//					event_id  user_id
	//					/	   \     /	 \
	//				foreign key \   /  foreign key
	//						 primary key  
	//
	//						   users
	//			/			/			\			\
	//		user_id		password	first_name	last_name
	//			|
	//	  primary key
	
	public void close();
	
	public void setFavoriteEvents(String userId, List<String> eventIds);
	
	public void unsetFavoriteEvents(String userId, List<String> eventIds);
	
	public Set<String> getFavoriteEventIds(String userId);
	
	public Set<Event> getFavoriteEvents(String userId);

	public void saveEvents(List<Event> events);
	
	public Set<String> getCategoriesFromDB(String eventId);
}
