package io.github.dantetam.http;

import java.awt.print.Printable;
import java.io.Console;
import java.io.IOException;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.stanford.nlp.util.StringParsingTask;
import io.github.dantetam.data.JsonProcessor;

/*
 * A class for collecting all methods for GET and POST methods to various web APIs.
 */
public class HttpRequest {
	
	public final static void testYahooRequest() throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("http://autoc.finance.yahoo.com/autoc?query=Twitter&region=1&lang=en");
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println(responseBody);
        } finally {
            httpclient.close();
        }
    }
	
	public static JsonElement getJsonElementFromUrl(String url) {
		return null;
	}
	
	public static String twitterRequestBearerToken() {
		String encodedToken = new String("YTdLdzNkaXc2WXFKUnNtaEZ2a1dCbGphYTpmZUJoVzB2MGIybmhneHNwcG9lanpWZUNxSWtnbUROTFExaWtXV2RaNXpuVkNJOFc5Rg==");
	
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/oauth2/token");
        	HttpPost httpPost = new HttpPost(url);
        	httpPost.addHeader("Authorization", "Basic " + encodedToken);
        	httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        	httpPost.setEntity(new StringEntity("grant_type=client_credentials"));

            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpPost, responseHandler);
            JsonElement element = JsonProcessor.parseStringToObject(responseBody);
            
            String accessToken = element.getAsJsonObject().get("access_token").toString();
            return accessToken;
        } catch (Exception e) {
        	
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
	
	
	public final static JsonElement wikipediaSearch(String subjectString) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + subjectString + "&utf8=&format=json");
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            
            String responseBody = httpclient.execute(httpget, responseHandler);
            JsonElement element = JsonProcessor.parseStringToObject(responseBody);

            return element;
        } finally {
            httpclient.close();
        }
    }
	
	public final static JsonElement wikipediaArticle(String articleName) throws ClientProtocolException, IOException {
		JsonElement articleRoot;
		try {
			articleRoot = wikipediaArticleRequest("Water");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		//System.out.println(articleRoot);
		
		JsonObject article = articleRoot.getAsJsonObject().get("query").getAsJsonObject().get("pages").getAsJsonObject();
		JsonElement firstArticle = article.entrySet().iterator().next().getValue();
		JsonElement firstArticleText = firstArticle.getAsJsonObject().get("revisions").getAsJsonArray().get(0).getAsJsonObject().get("*");
		
		return firstArticleText;
	}
	
	private final static JsonElement wikipediaArticleRequest(String articleName) throws ClientProtocolException, IOException {
		return wikipediaArticleRequests(new String[]{articleName});
	}
	private final static JsonElement wikipediaArticleRequests(String[] articleNames) throws ClientProtocolException, IOException {
		String allArticleNames = "";
		for (int i = 0; i < articleNames.length; i++) {
			allArticleNames += articleNames[i].replace(" ", "_");
			if (i != articleNames.length - 1) {
				allArticleNames += "|";
			}
		}
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://en.wikipedia.org/w/api.php?action=query&titles=" + allArticleNames + "&prop=revisions&rvprop=content&format=json");
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            
            String responseBody = httpclient.execute(httpget, responseHandler);
            JsonElement element = JsonProcessor.parseStringToObject(responseBody);

            return element;
        } finally {
            httpclient.close();
        }
	}
	
	public static final void main(String[] args) {
		/*JsonElement root;
		try {
			root = wikipediaSearch("Water");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		JsonArray object = root.getAsJsonObject().get("query").getAsJsonObject().get("search").getAsJsonArray();
		for (JsonElement element: object) {
			System.out.println(element);
		}*/
		
		String waterText;
		try {
			JsonElement waterArticle = wikipediaArticle("Water");
			waterText = waterArticle.getAsString();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		//System.out.println(waterText);
		
		//String[] lines = waterText.split("\\n");
		String[] lines = waterText.replaceAll("\\[", "").replaceAll("\\]", "").split("[\\r\\n]+");
		for (String line: lines) {
			System.out.println(line);
		}
		
		String str = "Bye{{Hello}}Hi";
		//str = str.replaceAll("\\{(.*?)\\}", "$1");
		str = str.replaceAll("\\{(.*?)\\}", "").replaceAll("\\{", "").replaceAll("\\}", "");
		System.out.println(str);
		
	}
}
