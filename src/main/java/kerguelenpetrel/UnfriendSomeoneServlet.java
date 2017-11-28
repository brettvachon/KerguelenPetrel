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
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

@SuppressWarnings("serial")
public class UnfriendSomeoneServlet extends HttpServlet
   {
   public static final long cursor = -1;
   
   public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
      {
      User unfriend = null;  

      Random r = new Random();   

      resp.setContentType("text/plain; charset=UTF-8");
      try 
         {
         //Get the Twitter object
         Twitter twit = TwitterFactory.getSingleton();

         //Find a friend of a follower to bother
         long[] followerIDs = twit.getFollowersIDs(twit.getId(), cursor).getIDs();
         if (followerIDs.length == 0)
           {
           resp.getWriter().println("No friends to unfollow");
           return;
           }
         unfriend = twit.showUser(followerIDs[r.nextInt(followerIDs.length)]);
         twit.destroyFriendship(unfriend.getId());
         resp.getWriter().println("Successfully unfollowed @"+unfriend.getScreenName());
         resp.getWriter().println("\n");
         }
      catch(TwitterException e)
         {
         resp.getWriter().println("Problem with Twitter \n");
         e.printStackTrace(resp.getWriter());
         }
      catch(Exception e)
         {
         resp.getWriter().println("Problem! \n");
         e.printStackTrace(resp.getWriter());
         }  
      }
   }
