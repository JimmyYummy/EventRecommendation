package external;
import entity.Event;
import java.util.List;
public interface EventSourceAPI {
	public List<Event> search(double lat, double lon, String keyword);
}
