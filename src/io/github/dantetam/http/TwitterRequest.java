package io.github.dantetam.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.lwjgl.Sys;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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
	public Map<String, Integer> getTopicScores(JsonElement[] topics) {
		Map<String, Integer> result = new HashMap<>();
		for (int i = 0; i < topics.length; i++) {
			String name = topics[i].getAsJsonObject().get("name").toString();
			String tweetVol = topics[i].getAsJsonObject().get("tweet_volume").toString();
			if (tweetVol == null || tweetVol.equals("null")) {
				continue;
			}
			result.put(name, Integer.parseInt(tweetVol));
		}
		return result;
	}
	public String[] getTopicsFromJson(JsonElement[] topics) {
		String[] result = new String[topics.length];
		for (int i = 0; i < topics.length; i++) {
			result[i] = topics[i].getAsJsonObject().get("name").toString();
		}
		return result;
	}
	public JsonElement[] twitterGlobalTrending() {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/1.1/trends/place.json?id=1");
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.setHeader("Authorization", "Bearer " + accessToken);
                	
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            JsonElement element = JsonProcessor.parseStringToObject(responseBody);            
            JsonArray trends = element.getAsJsonArray().get(0).getAsJsonObject().get("trends").getAsJsonArray();
           
            JsonElement[] results = new JsonElement[trends.size()];
            for (int i = 0; i < trends.size(); i++) {
            	//System.out.println(trends.get(i).toString());
            	results[i] = trends.get(i);
            }
            
            //System.out.println(trends);
            return results;
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
	public String[] getTweetsFromJson(JsonElement[] tweets) {
		String[] result = new String[tweets.length];
		for (int i = 0; i < tweets.length; i++) {
			result[i] = tweets[i].getAsJsonObject().get("text").toString();
		}
		return result;
	}
	public JsonElement[] twitterSearchTopic(String topic) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String url = new String("https://api.twitter.com/1.1/search/tweets.json?");
        	url += "q=" + topic;
        	url += "&lang=en";
        	url += "&count=100";
        	url += "&result_type=popular";
        	
        	HttpGet httpGet = new HttpGet(url);
        	httpGet.setHeader("Authorization", "Bearer " + accessToken);
        	
            ResponseHandler<String> responseHandler = new HttpResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);

            JsonArray tweets = JsonProcessor.parseStringToObject(responseBody).getAsJsonObject().get("statuses").getAsJsonArray();   
            JsonElement[] results = new JsonElement[tweets.size()];
            for (int i = 0; i < tweets.size(); i++) {
            	results[i] = tweets.get(i);
            	//System.out.println(tweets.get(i));
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
	
	public static String sanitizeTweet(String text) {
		text = text.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\"", "");
		//text = text.replaceAll("\\p{P}", "");
		String[] tokens = text.split(" ");
		String result = "";
		for (int i = 0; i < tokens.length - 1; i++) { //Remove last token which is often truncated
			String token = tokens[i];
			if (token.contains("#") || token.contains("@")) {
				token = token.substring(1);
			}
			if (!token.equals("RT") && !token.contains("&") && !token.contains("...") && !token.contains("\\") && token.trim().length() > 0) {
				result += token.replaceAll("[^A-Za-z0-9 ]", "") + " ";
			}
		}
		return result;
	}
	
	public List<Tweet> collatePopularTopicTweets(int numTopics, int tweetsPerTopic) {
		JsonElement[] topics = twitterGlobalTrending();
		String[] topicsString = getTopicsFromJson(topics);
		Map<String, Integer> popularTopics = getTopicScores(topics);
		List<Tweet> result = new ArrayList<>();
		
		for (int topicIndex = 0; topicIndex < numTopics; topicIndex++) {
			if (topicIndex >= topics.length) break;
			String topic = topicsString[topicIndex];
			if (!popularTopics.containsKey(topic)) {
				numTopics++;
				continue;
			}
			topic = topic.replace("\n", "").replace("\"", "").replace("#", "").replace(" ", "_").replace("?", "");
			int topicScore = Integer.parseInt(topics[topicIndex].getAsJsonObject().get("tweet_volume").toString());
			
			JsonElement[] tweetsJson = twitterSearchTopic(topic);
			for (int i = 0; i < Math.min(tweetsPerTopic, tweetsJson.length); i++) {
				//System.out.println(Math.min(tweetsPerTopic, tweetsJson.length));
				JsonElement jsonElement = tweetsJson[i];
				Tweet tweet = new Tweet(topic, topicScore, jsonElement);
				if (tweet.text().contains("???")) {
					continue;
				}
				result.add(tweet);
			}
		}
		
		return result;
	}
	
	public static Set<String> collectAllTopics(List<Tweet> tweets) {
		Set<String> results = new HashSet<>();
		for (Tweet tweet: tweets) {
			if (!results.contains(tweet.topic)) {
				results.add(tweet.topic);
			}
		}
		return results;
	}
	
	public static void main(String[] args) {
		//System.out.println("#???????_??????_????? #?????????_??????????".contains("???"));
		//System.out.println("--------------");
		
		List<Tweet> popularTweets = new TwitterRequest().collatePopularTopicTweets(50,1000);
		//System.out.println("----------------------");
		/*for (Tweet tweet: popularTweets) {
			System.out.println(tweet.text());
			System.out.println(tweet.hasPhoto());
			System.out.println(tweet.hasVideo());
			System.out.println(tweet.hasAnimatedGif());
			System.out.println("----------------------");
		}*/
		
		for (Tweet tweet: popularTweets) {
			System.out.println(tweet.toString());
		}
		
		System.out.println("------------------------------");
		
		Set<String> allTopicNames = collectAllTopics(popularTweets);
		
		for (String topicName: allTopicNames) {
			System.out.println(topicName);
		}
		
		/*TwitterRequest twitterRequest = new TwitterRequest();
		String[] topics = twitterRequest.twitterGlobalTrending();
		
		for (int topicIndex = 0; topicIndex < topics.length; topicIndex++) {
			if (topicIndex >= topics.length) break;
			String topic = topics[topicIndex];
			topic = topic.replace("\"", "").replace("#", "").replace(" ", "_");
			
			System.out.println(topics[topicIndex]);
		}
		
		for (int topicIndex = 0; topicIndex < 10; topicIndex++) {
			if (topicIndex >= topics.length) break;
			String topic = topics[topicIndex];
			topic = topic.replace("\"", "").replace("#", "").replace(" ", "_").replace("?", "");
			
			String[] tweets = twitterRequest.twitterSearchTopic(topic);
			
			StellaTreeAssociation stellaTreeAssociation = new StellaTreeAssociation();
			
			//String tweet = tweets[0];
			//String tweet = "I can always tell when movies use fake dinosaurs";
			for (String tweet: tweets) {
				//System.out.println(tweet);
				tweet = sanitizeTweet(tweet);
				//System.out.println(tweet);
				List<ParseGrammarResult> parseGrammarResults = StellaDependencyParser.parseGrammarStructure(tweet);
				for (ParseGrammarResult psg: parseGrammarResults) {
					StellaWordAssociationGraph graph = stellaTreeAssociation.processText(psg);
					double[][] matrix = StellaWordAssociationGraph.getFullAssociationMatrix(psg.taggedWords, graph);
					Map<String[], Double> sortedCorr = StellaWordAssociationGraph.matrixFindBestCorrelation(psg.taggedWords, matrix);
					int i = 0;
					for (Map.Entry<String[], Double> entry : sortedCorr.entrySet()) {
						String word1 = entry.getKey()[0], word2 = entry.getKey()[1];
						//if ()
						if (word1 == "``" || word2 == "``") continue;
						System.out.println(word1 + ", " + word2 + ": " + entry.getValue());
						i++;
						if (i == 30) break;
					}
				}
			}
		}*/
	}
	
}
