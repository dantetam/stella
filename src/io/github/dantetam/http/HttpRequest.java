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
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	HttpGet httpGet = new HttpGet(url);
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);
            JsonElement element = JsonProcessor.parseStringToObject(responseBody);
            return element;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
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
	
	public static String spotifyRequestBearerToken() {
		String encodedToken = new String("NmY0NTExZjk1YTU0NGFjN2EwMWEwZDc1M2U1MTc5MTQ6Nzg5MTE5ODU0MTUzNDhiMWI1OGFhMDg3ZGIzNjQ3ZWU=");
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://accounts.spotify.com/api/token");
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
	
	public static String findCompanySymbol(String name) {
		return findCompanySymbol(new String[]{name})[0];
	}
	public static String[] findCompanySymbol(String[] companyNames) {
		String[] symbols = new String[companyNames.length];
		for (int i = 0; i < companyNames.length; i++) {
			String companyName = companyNames[i];
			
			String url = new String("https://autoc.finance.yahoo.com/autoc?query=" + companyName + "&region=US&lang=en-US&diagnostics=false");
			JsonElement element = getJsonElementFromUrl(url);
			
			JsonElement foundSymbol = element.getAsJsonObject().get("ResultSet").getAsJsonObject().get("Result").getAsJsonArray().get(0).getAsJsonObject().get("symbol");
			symbols[i] = foundSymbol.toString().replaceAll("\"", ""); 
		}
        return symbols;
	}
	
	public static JsonElement intrinioCompanyInfoRequest(String tickerSymbol) {
		String auth = new String("NGUxNTgzZTQzZWNiMDQ0M2VjODBhOWIzM2NhNmJhMGU6NTA0MzI0NmUwNDliNWFkOWJlZGRiNWVmNDZjMTQwZmY=");
	
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.intrinio.com/companies?ticker=" + tickerSymbol);
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.addHeader("Authorization", "Basic " + auth);
        	//httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        	//httpPost.setEntity(new StringEntity("grant_type=client_credentials"));

            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);
            JsonElement element = JsonProcessor.parseStringToObject(responseBody);
            
            return element;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return null;
	}
	
	
	public final static JsonElement wikipediaSearch(String subjectString) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + subjectString + "&utf8=&format=json");
            return getJsonElementFromUrl(url);
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
        	return getJsonElementFromUrl(url);
        } finally {
            httpclient.close();
        }
	}
	
	public static final void main(String[] args) {
		
		System.out.println(spotifyRequestBearerToken());
		
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
		
		System.out.println(waterText);
		
		for (int i = 0; i < 10; i++) {
			System.out.println("-------------------------------------------");
		}
		
		waterText = waterText.replaceAll("\\<(.*?)\\>", "").replaceAll("\\{(.*?)\\}", "").replaceAll("\\{", "").replaceAll("\\}", "");
		
		//String[] lines = waterText.split("\\n");
		String[] lines = waterText.replaceAll("\\[", "").replaceAll("\\]", "").split("[\\r\\n]+");
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].contains("File:"))
				continue;
			
			//A link in Wikipedia is represented as [[True article name|Display name]], 
			//here we simply replace that construction with Display name
			String[] words = lines[i].split(" ");
			for (String word: words) {
				if (word.contains("|")) {
					String[] split = word.split("\\|");
					if (split.length > 1) {
						String displayedWord = split[1];
						lines[i] = lines[i].replace(word, displayedWord);
					}
				}
			}
			
			System.out.println(lines[i]);
		}
		
		/*String str = "Bye{{Hello}}Hi";
		//str = str.replaceAll("\\{(.*?)\\}", "$1");
		str = str.replaceAll("\\{(.*?)\\}", "").replaceAll("\\{", "").replaceAll("\\}", "");
		System.out.println(str);*/
		
		//JsonElement intrinio = intrinioRequest();
		//System.out.println(intrinio);
		
		String[] result = findCompanySymbol(new String[]{"Google", "Apple"});
		for (int i = 0; i < result.length; i++) {
			String symbol = result[i];
			JsonElement element = intrinioCompanyInfoRequest(symbol);
			System.out.println(element.toString());
		}
	}
}
