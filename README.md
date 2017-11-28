# KerguelenPetrel
## Twitter bot

Kerguelen Petrel is a Twitter bot that uses an RSS feed and Wordnik to update a Twitter Status and bother a Twitter user. The bot can also respond to @ messages. Every so often the bot will friend or unfriend someone.

The contents of the Tweet are generated from either an RSS Feed title, an RSS Feed description or a Wordnik sentences. The bot can also add hashtags to a Tweet which are either global trends or invented.

This bot runs on Google App Engine. It uses Rome 1.0 and Knicker libraries, as well as the Twitter4j library. This bot is licenced under the Apache Licence.

List of servlets:

  - BotherSomeoneServlet - Tweets a follower of a follower
  - FriendSomeoneServlet - Chooses a follower to friend and tweets that user
  - RespondServlet - Checks for a response and responds accordingly
  - UnfriendSomeoneServlet - Chooses a follower to unfriend
  - UpdateStatusServlet - Updates the status with a sentence from Wordnik and an RSS feed title

  You can modify the frequency of the bot's performance of each of those Servlets in the `cron.xml` file.

 ------
 
 ## Setting up the bot
1. You will need to sign in to Twitter and create an app at https://apps.twitter.com . Next click on "Keys and Access Tokens" and get your API keys and Access Tokens. Place the keys in the `twitter4j.properties` file.
2. You will also need a Wordnik API key from http://developer.wordnik.com . When you receive your API key place it in the `wordnik.properties` file.
3. Create a GAE project at https://console.developers.google.com/cloud-resource-manager and deploy the bot to Google App Engine.

 ------

**WARNING** Be aware that some features of this bot violate Twitter's Terms and Conditions, found at https://support.twitter.com/articles/76915. Your Twitter account may be suspended if you run this bot. The provided `cron.xml` file has servlet run times that mimic a regular user in order to circumvent Twitter's automatic bot detection.
