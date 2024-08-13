package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;  // Import SimpleDateFormat
import java.util.Date;  // Import Date
//import java.sql.Date;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		//API Setup
		String apiKey= "99f0ba06eca4550706d920610df79deb";
		//Get the city from the input
		String city= request.getParameter("city");
		String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
		//Create the URL for the OpenWeatherMap API request
		 String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + apiKey;
		try {
		URL url=new URL(apiUrl);
		 HttpURLConnection connection =(HttpURLConnection) url.openConnection();
		 connection.setRequestMethod("GET");
		 
		// Check for a successful response code or throw an exception
		  
		 //Reading the data from network
		    InputStream inputStream=connection.getInputStream();
		    InputStreamReader reader=new InputStreamReader(inputStream);
		    Scanner sc=new Scanner(reader);
		 
		 //Store in string
		     StringBuilder responseContent = new StringBuilder();
		 
		 
		 while(sc.hasNext()) {
			 responseContent.append(sc.nextLine());
			 
		 }
		//System.out.println(responseContent);
		 sc.close();
		
		//typecasting= parsing the data into json
		// Parse the JSON response to extract temperature, date, and humidity
		Gson gson=new Gson();
		JsonObject jsonObject= gson.fromJson(responseContent.toString(),JsonObject.class);
		 //System.out.println(jsonObject);
		 
		 //Date & Time
		 long dateTimestamp=jsonObject.get("dt").getAsLong() * 1000;
		 String date= new Date(dateTimestamp).toString();
		 
		 //Temperature
		 double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		 int temperatureCelsius= (int) (temperatureKelvin-273.15);
		 
		 //Humidity
		 int humidity=jsonObject.getAsJsonObject("main").get("humidity").getAsInt();//typecasting into integer
		 
		 //Wind Speed
		 double windSpeed=jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
		 
		 //Weather Condition
		 String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		 
		 //Set the data as request attributes(for sending to the jsp page)
		 request.setAttribute("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		 request.setAttribute("day", new SimpleDateFormat("EEEE").format(new Date()));
		 request.setAttribute("city", city);
		 request.setAttribute("temperature", temperatureCelsius);
		 request.setAttribute("weatherCondition", weatherCondition);
		 request.setAttribute("humidity", humidity);
		 request.setAttribute("windSpeed", windSpeed);
		 request.setAttribute("weatherData", responseContent.toString());
		 
		 connection.disconnect();
		 
		}catch(IOException e) {
			e.printStackTrace();
		} 
		
		//Forward the request to the weather.jsp page for rendering
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
 
}
