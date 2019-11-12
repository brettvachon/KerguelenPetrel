/*
 * Copyright (c) 2017 daloonik
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package kerguelenpetrel;

import java.io.IOException;
import java.io.PrintWriter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.Status;
import twitter4j.StatusUpdate;

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rometools.rome.io.FeedException;

@SuppressWarnings("serial")

public class BotherSomeoneServlet extends HttpServlet
 {
 public static final long cursor = -1;
 public static final Random r = new Random();
 public static final String feedsFile = "WEB-INF/StaticFiles/feeds";

 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   StringBuilder builder = new StringBuilder();
   PrintWriter out = resp.getWriter();
   long[] friendIDs, victimIDs;
   User victim;

   resp.setContentType("text/plain; charset=UTF-8");       
    
   try 
     {
     //Get the Twitter object
     Twitter twit = TwitterFactory.getSingleton();

     //Find a friend of a follower to bother
     friendIDs = twit.getFollowersIDs(twit.getId(), cursor).getIDs();
     
     if(friendIDs.length == 0)
        {
        resp.getWriter().println("Cannot find any followers to bother \n");
        return;
        }
     
     //Load the potential victim IDs
     victimIDs = twit.getFollowersIDs(friendIDs[r.nextInt(friendIDs.length)], cursor).getIDs();
 
     if(victimIDs.length == 0)
        {
         resp.getWriter().println("Cannot find any followers to bother \n");
         return;
        }
       
     //Write to our victim
     victim = twit.showUser(victimIDs[r.nextInt(victimIDs.length)]);
     builder.append("@" + victim.getScreenName() + " ");

     //Append feed description
     GetFeed feed = new GetFeed(feedsFile); 
     builder.append(feed.description());
   
     if(builder.length() > 280)
          builder.setLength(280); //Tweets are limited to 280 characters
       
     //Set the status
     StatusUpdate status = new StatusUpdate(builder.toString());
       
     //Post the status to out
     twit.updateStatus(status);
     out.println("Tweet posted: "+ status.getStatus() +" \n");
     
     //Favorite one of the first 20 tweets of our victim
     List<Status> tweets = twit.getUserTimeline(victim.getId()); 
     
     Status randomStatus = tweets.get(new Random().nextInt(tweets.size()));
     Status favStatus = twit.createFavorite(randomStatus.getId());
     out.println("Added favourite to status "+ favStatus.getId() +" \n");
     } 
   catch(TwitterException e)
     {
     out.println("Problem with Twitter \n");
     e.printStackTrace(resp.getWriter());
     }
   catch(FeedException e)
      {
      out.println("Problem with RSS Feed <br />");
      e.printStackTrace(out);
      }
   finally 
      {
      out.close();  // Always close the output writer
      } 
   }
 }
