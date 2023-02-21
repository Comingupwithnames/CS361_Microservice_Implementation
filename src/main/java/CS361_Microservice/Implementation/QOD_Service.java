package CS361_Microservice.Implementation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
@SpringBootApplication
@RestController
/**
 *
 * This class will utilize the Spring Boot framework
 * to return a JSON formatted Quote_Obj to display on
 * a web page
 *
 * @author Skyelar Cann
 */
public class QOD_Service {
	private String QOD;
	private String author;
	BufferedReader br;
	private String key = "YOUR API KEY HERE";
	String lineToRead;
	StringBuffer responseContent = new StringBuffer();

	public static void main(String[] args) {
		SpringApplication.run(QOD_Service.class, args);
	}

	@GetMapping("/QuoteOfTheDay")
	public Quote_Obj quote_obj() throws IOException
	{
		Quote_Obj toReturn = new Quote_Obj(QOD, author);
		responseContent.setLength(0); // Set the length of our StringBuffer to 0 for reuse
		try
		{
			URL quoteURL = new URL("https://api.api-ninjas.com/v1/quotes?category=happiness");
			/* Initialize the connection and set it to a get request */
			HttpURLConnection connection = (HttpURLConnection) quoteURL.openConnection();
			connection.setRequestProperty("accept", "application/json");
			connection.setRequestProperty("X-Api-Key", key);
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			/* Get our status and initialize the BufferedReader if the status is 200 (OK) */
			int status = connection.getResponseCode();
			System.out.println("Status: " + status);

			if(status > 299)
			{
				br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}
			else
			{
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			}
			/* If we get here, read the incoming JSON into our StringBuffer */
			while((lineToRead = br.readLine()) != null)
			{
				responseContent.append(lineToRead);
			}
			br.close(); // Close our BufferedReader

			/* Initialize a JsonArray since our incoming JSON is an array containing a single JsonObject then get that object */
			JsonArray output = JsonParser.parseString(responseContent.toString()).getAsJsonArray();
			JsonObject JObject = (JsonObject) output.get(0);

			/* Update the QOD and author fields from our jObject and create a new Quote_Obj using those as params */
			QOD = JObject.get("quote").toString();
			author = JObject.get("author").toString();
			connection.disconnect();
			toReturn = new Quote_Obj(QOD, author);
		}
		catch (Exception e)
		{
			System.out.println("Some error occurred");
			System.out.println(e.toString());
		}
		return toReturn;
	}
}
