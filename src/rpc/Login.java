package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import dao.DBConnection;
import dao.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject obj = new JSONObject();
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			try {
				obj.put("status", "Session Invalid");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			String userId = (String) session.getAttribute("user_id");
			DBConnection conn = DBConnectionFactory.getConnection();
			String name = conn.getFullName(userId);
			conn.close();
			try {
				obj.put("status", "OK");
				obj.put("user_id", userId);
				obj.put("name", name);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		RpcUtil.writeJSONObject(response, obj);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession(false) != null) {
			doGet(request,response);
		} else {
			DBConnection conn = DBConnectionFactory.getConnection();
			JSONObject input = RpcUtil.readJSONObject(request);
			try {
			String userId = input.getString("user_id");
			String pwd = input.getString("password");

			JSONObject obj = new JSONObject();

			if (conn.verifyLogin(userId, pwd)) {
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				// setting session to expire in 10 minutes
				session.setMaxInactiveInterval(10 * 60);
				// Get user name
				String name = conn.getFullName(userId);
				obj.put("status", "OK");
				obj.put("user_id", userId);
				obj.put("name", name);
			} else {
				response.setStatus(401);
				obj.put("status", "invalid user-password combination");
			}
			RpcUtil.writeJSONObject(response, obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}


