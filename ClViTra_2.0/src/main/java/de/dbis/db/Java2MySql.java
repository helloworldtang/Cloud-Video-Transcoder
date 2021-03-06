package de.dbis.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.dbis.util.GetProperty;

/**
 * 
 * Establishes connection to the MySql database.
 * Uses 'dbconnection.properties' file for configuration.
 *
 */
public class Java2MySql
{
	private final static String INPUT_FILE = "dbconnection";
	private final static String BASE_INPUT_FILE = "base";
	private static String url;
	private static String base;
	private static String port;
	private static String dbName;
	private static String driver;
	private static String userName;
	private static String password;
	private static String databaseServer;
	private static String useUniCode;
	private static String charEncoding;
	private static String charSet;
	private static String collation;
	
	public static void init() {

		driver = GetProperty.getParam("driverName", INPUT_FILE);
		databaseServer = GetProperty.getParam("databaseServer", INPUT_FILE);
		base = GetProperty.getParam("uri", BASE_INPUT_FILE);
		port = GetProperty.getParam("port", INPUT_FILE);
		dbName = GetProperty.getParam("database", INPUT_FILE);
		userName = GetProperty.getParam("username", INPUT_FILE);
		password = GetProperty.getParam("password", INPUT_FILE);
		useUniCode = GetProperty.getParam("useUniCode", INPUT_FILE);
		charEncoding = GetProperty.getParam("charEncoding", INPUT_FILE);
		charSet = GetProperty.getParam("charSet", INPUT_FILE);
		collation = GetProperty.getParam("collation", INPUT_FILE);
		
		url = "jdbc:" + databaseServer + "://" + base +":"+ port + "/";

	}

	public static Boolean approvedUser(String username){
		
		init();
		ResultSet res = null;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = "SELECT * FROM Approved_User WHERE username = ?";
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, username);
			res = pstmt.executeQuery();

			if (!res.next()) {
				System.out.println("User Not Found!");
				return false;
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void deleteVideo(String username, String videoName) {
		
		init();
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = "DELETE FROM video WHERE Name=? AND Username = ?";
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, videoName);
			pstmt.setString(2, username);
			Boolean res = pstmt.execute();
  	  
			System.out.println("DELETE VIDEO: "+res);

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Boolean Exists(String username, String videoName) {
		
		init();
		ResultSet res = null;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = "SELECT * FROM video WHERE Name=? AND Username = ?";
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, videoName+".mp4");
			pstmt.setString(2, username);
			res = pstmt.executeQuery();

			if (!res.next()) {
				System.out.println("No record found!");
				return false;
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Updates or enter the database with the video details.
	 * @param filename
	 * @param ext
	 * @param ThumbnailFilename
	 * @param Duration
	 * @param UserId
	 * @return String Video ID
	 */
	public static String VideoUpdate(String filename, String ext, String Thumbnail_big, String Thumbnail_small, long Duration, String Username) {
        
		init();
		PreparedStatement pstmt = null;
        int rowCount = 0;
        UUID ID = null;
        
        try {
        	Class.forName(driver).newInstance();
      	  	Connection conn = DriverManager.getConnection(url+dbName,userName,password);
      	  	ID = UUID.randomUUID();

      	  	String insertQuery = "INSERT INTO video (ID, Name, Format, Status, Duration, Thumbnail, Username, Thumbnail_small) VALUES (?,?,?,?,?,?,?,?)";
      	  	
      	  	pstmt = conn.prepareStatement(insertQuery);
      	  	pstmt.setString(1, ID.toString());
      	  	pstmt.setString(2, filename);
      	  	pstmt.setString(3, ext);
      	  	if(ext.equals("h264")){
      	  		System.out.println("ext == MP4:  "+ext);
      	  		pstmt.setString(4, "TRANSCODED");
      	  	}
      	  	else{
      	  		System.out.println("ext != MP4/mp4:   "+ext);
      	  		pstmt.setString(4, "INITIALIZED");
      	  	}

      	  	pstmt.setLong(5, Duration);
      	  	pstmt.setString(6, Thumbnail_big);
      	  	pstmt.setString(7, Username);
      	  pstmt.setString(8, Thumbnail_small);
      	  	System.out.println(Thumbnail_big);
      	  	rowCount = pstmt.executeUpdate();
      	  	
      	  	conn.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return ID.toString();
	}
	
	
	
	/**
	 * Updates the database with the video details.
	 * @param ID
	 * @param outputFile
	 * @param URI
	 * @return
	 */
	public static int VideoUpdate(String ID, String outputFile, String URI, String hours, String mins, String secs) {
        
		init();
		PreparedStatement pstmt = null;
        int rowCount = 0;
        
        try {
        	Class.forName(driver).newInstance();
      	  	Connection conn = DriverManager.getConnection(url+dbName,userName,password);
      	  	String insertQuery = "UPDATE  video SET  Status = ?, Name = ?, Format = ?, URI = ?, TIME = ? WHERE  ID = ?";
      	  	
      	  	pstmt = conn.prepareStatement(insertQuery);
      	  	pstmt.setString(1, "TRANSCODED");
      	  	pstmt.setString(2, new File(outputFile).getName());
      	  	pstmt.setString(3, "MP4");
      	  	pstmt.setString(4, URI);
      	  	pstmt.setString(5, hours+":"+mins+":"+secs);
      	  	pstmt.setString(6, ID);
      	  	rowCount = pstmt.executeUpdate();
      	  	
      	  	conn.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return rowCount;
	}
	
	
	public static void getVideosbyLocation(){
		
	}

	/**
	 * Returns the first initialized video, from the database, to be transcoded.
	 * @return String Video ID
	 */
	public static String getFirstInitializedVideo() {
	
		init();
		String ID = null;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			
			String insertQuery = "SELECT * FROM  video WHERE Status=?";
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			pstmt.setString(1, "INITIALIZED");
			ResultSet res = pstmt.executeQuery();
  	  
			if (res.next())	
				ID = res.getString("ID");

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ID;
	}
	
	/**
	 * Sets the status of the video to 'Processing'
	 * @param ID Video ID
	 */
	public static void Processing(String ID) {
		init();
		
		try {
        	Class.forName(driver).newInstance();
      	  	Connection conn = DriverManager.getConnection(url+dbName,userName,password);
      	  	String insertQuery = "UPDATE  video SET  Status = ? WHERE  ID = ?";
      	  	PreparedStatement pstmt = null;
      	  	pstmt = conn.prepareStatement(insertQuery);
      	  	pstmt.setString(1, "PROCESSING");
      	  	pstmt.setString(2, ID);
      	  	pstmt.executeUpdate();
      	  	
      	  	conn.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Returns the name of the video for the corresponding ID.
	 * @param ID
	 * @return String Name
	 */
	public static String getVideoName(String ID) {
		
		init();
		
		String Name = null;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			String insertQuery;

			insertQuery = "SELECT * FROM  video WHERE ID=?";
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
      	  	
      	  	pstmt.setString(1, ID);
      	  	ResultSet res = pstmt.executeQuery();
      	  	if (res.next())
      	  		Name = res.getString("Name");

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Name;
	}
	
	/**
	 * Returns the Thumbnail URI for the given video ID.
	 * @param ID
	 * @return String Thumbnail URI
	 */
	public static String getThumbnailURI(String ID) {
		
		init();
		
		String ThumbnailURI = null;
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			String insertQuery;

			insertQuery = "SELECT * FROM  video WHERE ID=?";
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
      	  	
      	  	pstmt.setString(1, ID);
      	  	ResultSet res = pstmt.executeQuery();
      	  	if (res.next())
      	  	ThumbnailURI = res.getString("Thumbnail");

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ThumbnailURI;
	}
	
	/**
	 * Returns a list of video(s) uploaded by the given User having the given status. 
	 * @param UserId
	 * @param status 'Initialized', 'Processing', or 'Transcoded'.
	 * @return List<String> List of Videos with their Name, Thumbnail URL, and Video URL.
	 */
	public static List<String> getVideos(String Username, String status) {
		
		init();
		
		List<String> myList = new ArrayList<String>();
		String Thumbnail, Name, URI;
        try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);

			String Query = "SELECT * FROM video WHERE Username = ? AND Status = ?";
			PreparedStatement pstmt = conn.prepareStatement(Query);
			pstmt.setString(1, Username);
			pstmt.setString(2, status);
			ResultSet res = pstmt.executeQuery();
			while (res.next()) {
				URI = res.getString("URI");
				Thumbnail = res.getString("Thumbnail");
				Name = res.getString("Name");
				myList.add(Name);
				myList.add(URI);
				myList.add(Thumbnail);
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return myList;
	}

	
public static List<String> getVideos(String Username) {
		
		init();
		
		List<String> myList = new ArrayList<String>();
		String Thumbnail, Name, URI;
        try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);

			String Query = "SELECT * FROM video WHERE Username = ?";
			PreparedStatement pstmt = conn.prepareStatement(Query);
			pstmt.setString(1, Username);
			ResultSet res = pstmt.executeQuery();
			while (res.next()) {
				URI = res.getString("URI");
				Thumbnail = res.getString("Thumbnail");
				Name = res.getString("Name");
				myList.add(Name);
				myList.add(URI);
				myList.add(Thumbnail);
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return myList;
	}
	
	
	
	
	/**
	 * Returns the Video URL, Thumbnail URL, Name, and Status for the given video ID uploaded by the given User.
	 * @param UserId
	 * @param videoId
	 * @return String[] Video details.
	 */
	public static String[] getVideoDetails(String username, String videoURI, String clientType) {
		
		init();

		String Details[]=new String[4];
		String Thumbnail, Name, Status, Time;
        try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);

			System.out.println("Check");
			String Query = "SELECT * FROM video WHERE Username = ? AND URI = ?";
			PreparedStatement pstmt = conn.prepareStatement(Query);
			pstmt.setString(1, username);
			pstmt.setString(2, videoURI);
			ResultSet res = pstmt.executeQuery();
			if (res.next()){
				
				if(clientType.equals("mobile"))
					Thumbnail = res.getString("Thumbnail_small");
				else
					Thumbnail = res.getString("Thumbnail");
				Name = res.getString("Name");
				Status = res.getString("Status");
				Time = res.getString("TIME");
				Details[0]=Name;
				Details[1]=Thumbnail;
				Details[2]=Status;
				Details[3]=Time;
			}
			else{
				Details[0]="Not Found";
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return Details;
	}

	
public static String[] getVideoDetails(String videoId) {
		
		init();

		String Details[]=new String[5];
		String thumbnail, name, uri, status, username;
        try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url+dbName,userName,password);

			System.out.println("Check");
			String Query = "SELECT * FROM video WHERE ID = ?";
			PreparedStatement pstmt = conn.prepareStatement(Query);
			pstmt.setString(1, videoId);
			ResultSet res = pstmt.executeQuery();
			if (res.next()){
				
				uri = res.getString("URI");
				thumbnail = res.getString("Thumbnail");
				name = res.getString("Name");
				status = res.getString("Status");
				username = res.getString("Username");
				Details[0]=name;
				Details[1]=uri;
				Details[2]=thumbnail;
				Details[3]=status;
				Details[4]=username;
			}
			else{
				Details[0]="Not Found";
			}

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return Details;
	}
	
	
}