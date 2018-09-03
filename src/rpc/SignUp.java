package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import dao.DBConnection;
import dao.DBConnectionFactory;

/**
 * Servlet implementation class signUp
 */
@WebServlet("/signup")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		JSONObject input = RpcUtil.readJSONObject(request);
		try {
		String userId = input.getString("user_id");
		String pwd = input.getString("password");

		JSONObject obj = new JSONObject();
		
		if (conn.isUserExist(userId)) {
			obj.put("msg", "User already exist, please try another one.");
		} else {
			if (conn.createUser(userId, pwd)) {
				obj.put("msg", "Successfully signed up, please login.");
			} else {
				obj.put("msg", "signed up failed due to server error, please try again later");
			}
		}
		RpcUtil.writeJSONObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
