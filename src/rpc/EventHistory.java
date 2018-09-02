package rpc;

import java.io.IOException;
import java.util.ArrayList;
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

/**
 * Servlet implementation class FavoriteEvents
 */
@WebServlet("/history")
public class EventHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EventHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		JSONArray jarr = new JSONArray();
		try (DBConnection conn = DBConnectionFactory.getConnection()) {
			Set<Event> favEvents = conn.getFavoriteEvents(userId);
			for (Event e : favEvents) {
				JSONObject obj = e.toJSONObject();
				obj.put("favorite", true);
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
		try {
			// get the userId and favorite eventIds
			JSONObject obj = RpcUtil.readJSONObject(request);
			String userId = obj.getString("user_id");
			JSONArray jarr = obj.getJSONArray("favorites");
			List<String> eventIds = new ArrayList<>();
			for(int i = 0; i < jarr.length(); i++) {
				eventIds.add(jarr.get(i).toString());
			}
			// update the database
			System.out.println("updating the database");
			DBConnection conn = DBConnectionFactory.getConnection();
			conn.setFavoriteEvents(userId, eventIds);
			conn.close();
			// return response
			JSONObject resObj = new JSONObject();
			resObj.put(String.format("User %s successfully favorited the events", userId), jarr);
			RpcUtil.writeJSONObject(response, resObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RpcUtil.writeJSONObject(response, new JSONObject());
	} 

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// get the userId and favorite eventIds
			JSONObject obj = RpcUtil.readJSONObject(request);
			String userId = obj.getString("user_id");
			JSONArray jarr = obj.getJSONArray("favorites");
			List<String> eventIds = new ArrayList<>();
			for(int i = 0; i < jarr.length(); i++) {
				eventIds.add(jarr.get(i).toString());
			}
			// update the database
			DBConnection conn = DBConnectionFactory.getConnection();
			conn.unsetFavoriteEvents(userId, eventIds);
			conn.close();
			// return response
			JSONObject resObj = new JSONObject();
			resObj.put(String.format("User %s successfully unfavorited the events", userId), jarr);
			RpcUtil.writeJSONObject(response, resObj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RpcUtil.writeJSONObject(response, new JSONObject());
	}
}
