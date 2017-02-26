package io.github.dantetam.http;

import java.io.IOException;
import java.sql.Time;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jna.platform.win32.OaIdl.VARDESC;

import io.github.dantetam.data.JsonProcessor;

public class TwitterRequest {

	private String accessToken;
	
	public TwitterRequest() {
		accessToken = HttpRequest.twitterRequestBearerToken().replace("\"", "");
	}
	
	public String twitterGlobalTrending() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/1.1/trends/place.json?id=1");
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.setHeader("Authorization", "Bearer " + accessToken);
        	
        	System.out.println("Bearer " + accessToken);
        	
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            JsonElement element = JsonProcessor.parseStringToObject(responseBody);            
            JsonArray trends = element.getAsJsonArray().get(0).getAsJsonObject().get("trends").getAsJsonArray();
           
            for (int i = 0; i < trends.size(); i++) {
            	JsonElement trend = trends.get(i);
            	System.out.println(trend);
            }
            
            //System.out.println(trends);
            
            
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;
	}
	
	public static void main(String[] args) {
		TwitterRequest twitterRequest = new TwitterRequest();
		String topics = twitterRequest.twitterGlobalTrending();
	}
	
}
