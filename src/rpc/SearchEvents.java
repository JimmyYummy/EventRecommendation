package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dao.DBConnection;
import dao.DBConnectionFactory;
import entity.Event;
import external.EventSourceAPI;
import external.TicketMaster.TicketMasterAPI;

/**
 * Servlet implementation class SearchEvents
 */
@WebServlet("/search")
public class SearchEvents extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final EventSourceAPI eventSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchEvents() {
        super();
        eventSource = new TicketMasterAPI();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get the params for search
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String keyword = request.getParameter("keyword");
		
		List<Event> events = eventSource.search(lat, lon, keyword);
		JSONArray jarr = new JSONArray();
		try (DBConnection conn = DBConnectionFactory.getConnection()) {
			conn.saveEvents(events);
			Set<String> favoriteEventIds = conn.getFavoriteEventIds(userId);
			for (Event e : events) {
				JSONObject obj = e.toJSONObject();
				obj.put("favorite", favoriteEventIds.contains(e.getEventId()));
				jarr.put(obj);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		RpcUtil.writeJSONArray(response, jarr);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
