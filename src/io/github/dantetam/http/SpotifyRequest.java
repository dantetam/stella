package io.github.dantetam.http;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import io.github.dantetam.data.JsonProcessor;

public class SpotifyRequest {

	public String accessToken;
	
	public SpotifyRequest() {
		accessToken = HttpRequest.twitterRequestBearerToken().replace("\"", "");
	}
	
	public JsonElement searchForTrack(String trackString) {
		trackString = trackString.replace(" ", "_");
		
		//curl -H "Authorization: Bearer AAAAAAAAAAAAAAAAAAAAALKmywAAAAAABso63L91FWY6lPdxmtQXDnNNPvU%3DbNetMn8FejElH2XDIInLx52nEf8wMxGNLBOsZJ5VgaNbpKICL1" https://api.spotify.com/v1/search?q=Monsieur_Adi_Bad_Habits_Verite&type=track&market=US
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.spotify.com/v1/search?");
        	url += "q=" + trackString;
        	url += "&type=track";
        	url += "&market=US";
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.setHeader("Authorization", "Bearer " + accessToken);
                	
        	System.out.println(accessToken);
        	System.out.println(url);
        	
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            JsonElement element = JsonProcessor.parseStringToObject(responseBody);            
            //JsonArray trends = element.getAsJsonArray().get(0).getAsJsonObject().get("trends").getAsJsonArray();

            //System.out.println(trends);
            return element;
        } catch (ClientProtocolException e) {
        	e.printStackTrace();
        	//System.out.println(e.getMessage());
        } catch (IOException e) {
        	e.printStackTrace();
        }
        finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return null;
	}
	
	public static void main(String[] args) {
		new SpotifyRequest().searchForTrack("Monsieur Adi Bad Habits Verite");
	}

}
