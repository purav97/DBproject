

import java.sql.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;


public class DbHandler {
	// connection strings
	private static String connString = "jdbc:postgresql://localhost:5104/postgres";
	private static String userName = "devansh";
	private static String passWord = "";
	
	
	public static JSONObject authenticate(String id, String password,HttpServletRequest request){		
		JSONObject obj = new JSONObject();
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "select count(*) from password where id=? and password=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			preparedStmt.setString(2, password);
			ResultSet result =  preparedStmt.executeQuery();
			result.next();
			boolean ans = (result.getInt(1) > 0); 
			preparedStmt.close();
			conn.close();
			if(ans==true){
				request.getSession(true).setAttribute("id", id);
				obj.put("status",true);				
				obj.put("data", id);			
			}
			else{						
					obj.put("status",false);
					obj.put("message", "Authentication Failed");					
			}			
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	}
	
	public static JSONObject register(String id, String password,String name,String email,HttpServletRequest request) {
		JSONObject obj = new JSONObject();
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String query = "select count(*) from password where id=? ;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, id);
			ResultSet result =  preparedStmt.executeQuery();
			result.next();
			boolean ans = (result.getInt(1) > 0); 
			preparedStmt.close();
			conn.close();
			if(ans==true){
				//the user with given id is already registered
				//send back failed response
				obj.put("status", false);
				obj.put("duplicate_user", "yes");
				obj.put("message", "User already exists with given userid");
			}
			else {
				//create new user and 
				int x = addToUser(id,name,email,request);
				int y = addToPassword(id,password,request);
				if(x==1 && y==1)
					obj.put("status", true);
				else {
					obj.put("status", false);
					obj.put("duplicate_user", "no");
					obj.put("message","Registration failed");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static int addToUser(String uid, String name, String email, HttpServletRequest request){
		int success = -1;
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String insertTableSQL = "insert into \"user\" (uid, name, email) values (?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, uid);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, email);
			preparedStatement.executeUpdate();
			success = 1;
			preparedStatement.close();
			conn.close();		
		} 
		catch(Exception e){
			e.printStackTrace();
			success = -1;
		}
		return success;
	}
	
	
	public static int addToPassword(String uid, String password, HttpServletRequest request){
		int success = -1;
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String insertTableSQL = "insert into password (id, password) values (?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, uid);
			preparedStatement.setString(2, password);
			preparedStatement.executeUpdate();
			success = 1;
			preparedStatement.close();	
			conn.close();
		} 
		catch(Exception e){
			e.printStackTrace();
			success = -1;
		}
		return success;
	}
	
	public static JSONObject getProfileInfo(String uid){
		JSONObject obj = new JSONObject();
		System.out.println("getting profile info");
		try{
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String insertTableSQL = "select profile_photo from \"user\" where uid = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setString(1, uid);
			ResultSet rs = preparedStatement.executeQuery();
			JSONArray jarr = ResultSetConverter(rs);
			if(jarr.length() != 1) {
				System.out.println("jarr length: " + jarr.length());
			}
			obj = jarr.getJSONObject(0);
			
			if(obj.getString("profile_photo").equals("None")) {
				System.out.println("jarr length: " + jarr.length());
				obj.put("status", false);
				obj.put("message", "Empty Profile Photo");
				obj.put("image_set", false);
				return obj;
			}
			else {
				obj.put("status", true);
				obj.put("image_set", true);
			}
			preparedStatement.close();	
			conn.close();
		} 
		catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	}
	
	public static JSONObject add_profile_photo(String uid,String encodedImage) {
		JSONObject obj = new JSONObject();
		System.out.println("starting to add photo");
		try{
			byte[] img = null; 
			if(encodedImage!=null) {
				encodedImage.replaceAll("\\s","");
				encodedImage.replace("\\", "");
				img = DatatypeConverter.parseBase64Binary(encodedImage);
			}
			
			// Create the connection
			Connection conn = DriverManager.getConnection(connString, userName, passWord);
			String update_photo = "update \"user\"\n" + 
					"set profile_photo = ? \n" + 
					"where uid = ? ;";
			PreparedStatement preparedStatement = conn.prepareStatement(update_photo);
			preparedStatement.setBytes(1, img);
			preparedStatement.setString(2, uid);
			if(preparedStatement.executeUpdate() > 0) {
				obj.put("status", true);
				obj.put("message","Updated Profile Photo");	
			}
			else {
				obj.put("status",false);
				obj.put("message", "Unable to update Profile Photo");
			}
			System.out.println("sending msg: status = " + obj.get("status"));
			preparedStatement.close();	
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	}
	
	public static JSONObject getCategoriesSubCategories(){
		JSONObject obj = new JSONObject();
		return obj;
	}
	
	private static JSONArray ResultSetConverter(ResultSet rs) throws SQLException, JSONException {
		
		// TODO Auto-generated method stub
		JSONArray json = new JSONArray();
	    ResultSetMetaData rsmd = rs.getMetaData();
	    while(rs.next()) {
	        int numColumns = rsmd.getColumnCount();
	        JSONObject obj = new JSONObject();
	        int postid=-1;
	        for (int i=1; i<numColumns+1; i++) {
	          String column_name = rsmd.getColumnName(i);

	          if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
	           obj.put(column_name, rs.getArray(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
	           obj.put(column_name, rs.getBoolean(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
	           obj.put(column_name, rs.getBlob(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
	           obj.put(column_name, rs.getDouble(column_name)); 
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
	           obj.put(column_name, rs.getFloat(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
	           obj.put(column_name, rs.getNString(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
	           obj.put(column_name, rs.getString(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
	           obj.put(column_name, rs.getInt(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
	           obj.put(column_name, rs.getDate(column_name));
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
	          obj.put(column_name, rs.getTimestamp(column_name));   
	          }
	          else if(rsmd.getColumnType(i)==java.sql.Types.BINARY) {
	        	  if(rs.getBytes(column_name) == null) {
		        	  obj.put(column_name, "None");
		          }
		          else {
		        	  String imageEncoded = Base64.getEncoder().encodeToString(rs.getBytes(column_name));
		        	  obj.put(column_name, imageEncoded);
		          }
	          }
	          else{
	           obj.put(column_name, rs.getObject(column_name));
	          }	          	          
	        }
	        json.put(obj);
	       
	      }
	    return json;
	}
	
}