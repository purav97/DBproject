

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
 * Servlet implementation class AddProfilePhoto
 */
@WebServlet("/AddProfilePhoto")
public class AddProfilePhoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddProfilePhoto() {
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
		System.out.println("Entered servlet addprofile photo");
		PrintWriter out = response.getWriter();	
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");	
		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) 
		{
			try {
				obj.put("status", false);
				obj.put("message", "Invalid session");
				System.out.println("app: Invalid session");
				out.print(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else 
		{
			String id = (String)request.getSession().getAttribute("id");
			String encodedImage = request.getParameter("image");
			System.out.println("app: Sending Db the request");
			obj = DbHandler.add_profile_photo(id,encodedImage);
			String resp = obj.toString();
			out.write(resp);
			out.close();
		}
	}

}
