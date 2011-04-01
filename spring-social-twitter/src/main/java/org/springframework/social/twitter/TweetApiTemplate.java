/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.twitter;

import java.util.List;

import org.springframework.social.twitter.support.extractors.TweetResponseExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.types.StatusDetails;
import org.springframework.social.twitter.types.Tweet;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link TweetApi}, providing a binding to Twitter's tweet and timeline-oriented REST resources.
 * @author Craig Walls
 */
public class TweetApiTemplate implements TweetApi {

	private final RestTemplate restTemplate;

	private TwitterProfileResponseExtractor profileExtractor;
	
	private TweetResponseExtractor tweetExtractor;

	private final TwitterRequestApi requestApi;

	public TweetApiTemplate(TwitterRequestApi requestApi, RestTemplate restTemplate) {
		this.requestApi = requestApi;
		this.restTemplate = restTemplate;
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.tweetExtractor = new TweetResponseExtractor();
	}

	public List<Tweet> getPublicTimeline() {
		return requestApi.fetchObjects("statuses/public_timeline.json", tweetExtractor);
	}

	public List<Tweet> getHomeTimeline() {
		return requestApi.fetchObjects("statuses/home_timeline.json", tweetExtractor);
	}

	public List<Tweet> getFriendsTimeline() {
		return requestApi.fetchObjects("statuses/friends_timeline.json", tweetExtractor);
	}

	public List<Tweet> getUserTimeline() {
		return requestApi.fetchObjects("statuses/user_timeline.json", tweetExtractor);
	}

	public List<Tweet> getUserTimeline(String screenName) {
		return requestApi.fetchObjects("statuses/user_timeline.json?screen_name={screenName}", tweetExtractor, screenName);
	}

	public List<Tweet> getUserTimeline(long userId) {
		return requestApi.fetchObjects("statuses/user_timeline.json?user_id={userId}", tweetExtractor, userId);
	}

	public List<Tweet> getMentions() {
		return requestApi.fetchObjects("statuses/mentions.json", tweetExtractor);
	}

	public List<Tweet> getRetweetedByMe() {
		return requestApi.fetchObjects("statuses/retweeted_by_me.json", tweetExtractor);
	}

	public List<Tweet> getRetweetedToMe() {
		return requestApi.fetchObjects("statuses/retweeted_to_me.json", tweetExtractor);
	}

	public List<Tweet> getRetweetsOfMe() {
		return requestApi.fetchObjects("statuses/retweets_of_me.json", tweetExtractor);
	}

	public Tweet getStatus(long tweetId) {
		return requestApi.fetchObject("statuses/show/{tweet_id}.json", tweetExtractor, tweetId);
	}

	public void updateStatus(String message) {
		updateStatus(message, new StatusDetails());
	}

	public void updateStatus(String message, StatusDetails details) {
		MultiValueMap<String, Object> tweetParams = new LinkedMultiValueMap<String, Object>();
		tweetParams.add("status", message);
		tweetParams.setAll(details.toParameterMap());
		requestApi.publish("statuses/update.json", tweetParams);
	}

	public void deleteStatus(long tweetId) {
		requestApi.delete("statuses/destroy/{tweetId}.json", tweetId);
	}

	public void retweet(long tweetId) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		requestApi.publish("statuses/retweet/{tweetId}.json", data, tweetId);
	}

	public List<Tweet> getRetweets(long tweetId) {
		return requestApi.fetchObjects("statuses/retweets/{tweetId}.json", tweetExtractor, tweetId);
	}

	public List<TwitterProfile> getRetweetedBy(long tweetId) {
		return requestApi.fetchObjects("statuses/{tweet_id}/retweeted_by.json", profileExtractor, tweetId);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getRetweetedByIds(long tweetId) {
		return restTemplate.getForObject(RETWEETED_BY_IDS_URL, List.class, tweetId);
	}

	public List<Tweet> getFavorites() {
		return requestApi.fetchObjects("favorites.json", tweetExtractor);
	}

	public void addToFavorites(long tweetId) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		requestApi.publish("favorites/create/{tweetId}.json", data, tweetId);
	}

	public void removeFromFavorites(long tweetId) {
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		requestApi.publish("favorites/destroy/{tweetId}.json", data, tweetId);
	}

	private static final String RETWEETED_BY_IDS_URL = TwitterTemplate.API_URL_BASE + "statuses/{tweet_id}/retweeted_by/ids.json";
}
