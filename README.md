# KerguelenPetrel
## Twitter bot

Kerguelen Petrel is a Twitter bot that uses an RSS feed to update a Twitter Status and bother a Twitter user. The bot can also respond to @ messages and can follow or unfollow someone.

The bot creates tweets by using content from either an RSS Feed title or an RSS Feed description. The bot can also add hashtags to a Tweet generated from Twitter.

This bot runs on Google App Engine. The libraries used are Rome 1.9.0, JDom 2.0.6, <s>Knicker 2.4.1</s> and Twitter4j 4.0.7. This bot is licenced under the Apache Licence.

List of servlets:

  - **BotherSomeoneServlet** This servlet finds a follower of a follower and tweets a sentence from Wordnik to this user
  - **FriendSomeoneServlet** - Chooses a follower to friend and tweets that user a feed description
  - **RespondServlet** - Checks for a response and responds accordingly with a feed title and a trend from Wordnik
  - **UnfriendSomeoneServlet** - Chooses a follower to unfriend
  - **UpdateStatusServlet** - Updates the status with a title from an RSS feed, a sentence from Wordnik, a global trend from Twitter and a made-up trend. The servlet can also post a picture from Flickr.

You can modify the frequency of the bot's performance of each of those Servlets in the `cron.xml` file.

----------
 
 ## Setting up the bot
1. You will need to create an app at https://developer.twitter.com. 
2. Next click on "Keys and Access Tokens" and get your API keys and Access Tokens for the app that you just created. 
3. Place the keys in the `twitter4j.properties` file.
4. Create a Google App Engine project at https://console.developers.google.com/cloud-resource-manager 
5. Deploy the bot to GAE.

----------

> Be aware that this bot violates Twitter's automation rules: 
> https://help.twitter.com/en/rules-and-policies/twitter-automation
> Your Twitter account may be suspended if you run this bot. 
> The provided `cron.xml` file has servlet run times that mimic a regular user in order to circumvent Twitter's automatic bot detection.
