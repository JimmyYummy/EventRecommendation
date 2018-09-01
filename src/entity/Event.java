package entity;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	private String eventId;
	private String name;
	private String address;
	private String imgUrl;
	private String eventUrl;
	private double distance;
	private Set<String> categories;
	
	private Event(Builder builder) {
		this.eventId = builder.eventId;
		this.name = builder.name;
		this.address = builder.address;
		this.imgUrl = builder.imgUrl;
		this.eventUrl = builder.eventUrl;
		this.distance = builder.distance;
		this.categories = builder.categories;
	}
	//getters
	public String getEventId() {
		return eventId;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public String getEventUrl() {
		return eventUrl;
	}

	public double getDistance() {
		return distance;
	}
	public Set<String> getCategories() {
		return categories;
	}
	//override hashcode & equals
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (! (obj instanceof Event)) return false;
		Event event = (Event) obj;
		if (this.eventId == null) return event.eventId == null;
		return this.eventId.equals(event.eventId);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.eventId == null) ? 0 : this.eventId.hashCode());
		return result;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("eventId", this.eventId);
			obj.put("name", this.name);
			obj.put("address", this.address);
			obj.put("imgUrl", this.imgUrl);
			obj.put("eventUrl", this.eventUrl);
			obj.put("distance", this.distance);
			obj.put("categories", this.categories);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	
	// builder pattern
	public static class Builder {
		private String eventId;
		private String name;
		private String address;
		private String imgUrl;
		private String eventUrl;
		private double distance;
		private Set<String> categories;
		
		public Builder setEventId(String eventId) {
			this.eventId = eventId;
			return this;
		}
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		public Builder setAddress(String address) {
			this.address = address;
			return this;
		}
		public Builder setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
			return this;
		}
		public Builder setEventUrl(String eventUrl) {
			this.eventUrl = eventUrl;
			return this;
		}
		public Builder setDistance(double distance) {
			this.distance = distance;
			return this;
		}
		public Builder setCategories(Set<String> categories) {
			this.categories = categories;
			return this;
		}
		public Event build() {
			return new Event(this);
		}
	}
}
