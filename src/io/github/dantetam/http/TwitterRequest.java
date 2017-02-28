package io.github.dantetam.http;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jna.platform.win32.OaIdl.VARDESC;

import edu.stanford.nlp.util.StringParsingTask;
import io.github.dantetam.data.JsonProcessor;
import io.github.dantetam.parser.ParseGrammarResult;
import io.github.dantetam.parser.StellaDependencyParser;
import io.github.dantetam.parser.StellaTreeAssociation;
import io.github.dantetam.parser.StellaWordAssociationGraph;

public class TwitterRequest {

	private String accessToken;
	
	public TwitterRequest() {
		accessToken = HttpRequest.twitterRequestBearerToken().replace("\"", "");
	}
	
	/*
	 * Get an array of the currently leading topics on Twitter through the GET trends/place endpoint.
	 */
	public String[] twitterGlobalTrending() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/1.1/trends/place.json?id=1");
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.setHeader("Authorization", "Bearer " + accessToken);
                	
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            JsonElement element = JsonProcessor.parseStringToObject(responseBody);            
            JsonArray trends = element.getAsJsonArray().get(0).getAsJsonObject().get("trends").getAsJsonArray();
           
            String[] results = new String[trends.size()];
            for (int i = 0; i < trends.size(); i++) {
            	JsonElement trend = trends.get(i);
            	//System.out.println(trend);
            	results[i] = trend.getAsJsonObject().get("name").toString();
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
	
	/*
	 * Get tweets from Twitter for a certain topic through the Twitter API GET search/tweets endpoint.
	 */
	public String[] twitterSearchTopic(String topic) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/1.1/search/tweets.json?");
        	url += "q=" + topic;
        	url += "&lang=en";
        	url += "&count=100";
        	System.out.println(url);
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.setHeader("Authorization", "Bearer " + accessToken);
        	
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            JsonArray tweets = JsonProcessor.parseStringToObject(responseBody).getAsJsonObject().get("statuses").getAsJsonArray();   
            String[] results = new String[tweets.size()];
            for (int i = 0; i < tweets.size(); i++) {
            	JsonElement tweet = tweets.get(i);
            	System.out.println("-------");
            	System.out.println(tweet);
            	System.out.println(tweet.getAsJsonObject().get("created_at").toString());
            	System.out.println(tweet.getAsJsonObject().get("id").toString());
            	System.out.println(tweet.getAsJsonObject().get("text").toString());
            	System.out.println(tweet.getAsJsonObject().get("user").toString());
            	results[i] = tweet.getAsJsonObject().get("text").toString();
            }
            
            return results;
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
		String[] topics = twitterRequest.twitterGlobalTrending();
		String[] tweets = twitterRequest.twitterSearchTopic("DNCChair");
		
		StellaTreeAssociation stellaTreeAssociation = new StellaTreeAssociation();
		
		for (String tweet: tweets) {
			List<ParseGrammarResult> parseGrammarResults = StellaDependencyParser.parseGrammarStructure(tweet);
			for (ParseGrammarResult psg: parseGrammarResults) {
				StellaWordAssociationGraph graph = stellaTreeAssociation.processText(psg);
				double[][] matrix = StellaWordAssociationGraph.getFullAssociationMatrix(psg.taggedWords, graph);
				Map<String[], Double> sortedCorr = StellaWordAssociationGraph.matrixFindBestCorrelation(psg.taggedWords, matrix);
				for (Map.Entry<String[], Double> entry : sortedCorr.entrySet()) {
					System.out.println(entry.getKey()[0] + ", " + entry.getKey()[1] + ": " + entry.getValue());
				}
			}
		}
	}
	
}
