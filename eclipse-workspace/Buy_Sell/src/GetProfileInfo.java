

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class GetProfileInfo
 */
@WebServlet("/GetProfileInfo")
public class GetProfileInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetProfileInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();	
		System.out.println("got Profile Info request");
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");	
		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) 
		{
			try {
				obj.put("status", false);
				obj.put("message", "Invalid session");
				out.print(obj);
				System.out.println("sending false status msg");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else 
		{	
			String userid = (String) request.getSession().getAttribute("id"); 
			
			if( userid == null) {
				try {
					obj.put("status", false);
					obj.put("message", "Invalid username");
					System.out.println("sending false username msg");
					out.print(obj);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.out.println("sending Dbhandler the request");
				out.print(DbHandler.getProfileInfo(userid));
			}
		}
	}

}
