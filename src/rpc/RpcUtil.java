package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RpcUtil {
	
	static void writeJSONObject(HttpServletResponse response, JSONObject obj) {
		response.setContentType("application/json");
		// to allow AJAX
		response.addHeader("Access-Control-Allow-Origin","*");
		try (PrintWriter writer = response.getWriter()) {
			writer.print(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void writeJSONArray(HttpServletResponse response, JSONArray jarr) {
		response.setContentType("application/json");
		// to allow AJAX
		response.addHeader("Access-Control-Allow-Origin","*");
		try (PrintWriter writer = response.getWriter()) {
			writer.print(jarr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		// reader the info
		try (BufferedReader reader = request.getReader();){
			String newline = null;
			while ((newline = reader.readLine()) != null) {
				sb.append(newline);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// create and return the JSON object
		try {
			return new JSONObject(sb.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println(sb.toString());
			e.printStackTrace();
			return null;
		}
		
	}
}
