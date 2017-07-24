/*
 * TwitFeedBot is licenced under the The MIT License (MIT)
 * 
 * Copyright (c) 2014–2015 Daloonik daloonik@gmail.com
 * 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package twitfeedbot;

import java.io.IOException;
import java.io.FileNotFoundException;

import com.sun.syndication.io.FeedException;

import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Trends;
import twitter4j.conf.ConfigurationBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.WordsApi;
import net.jeremybrooks.knicker.dto.Example;
import net.jeremybrooks.knicker.dto.Word;

@SuppressWarnings("serial")

public class UpdateStatusServlet extends HttpServlet 
 {
 public static final String feedsFile = "WEB-INF/StaticFiles/feeds";
 public Twitter twit;

  @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   StringBuffer buffer = new StringBuffer();  
   GetFeed feed = new GetFeed(feedsFile);
   Random r = new Random();
   String[] end = new String[] { "?","!"," :-)","...","?!"," ;-p",".","..","("+r.nextInt()+")"};
   
   resp.setContentType("text/html");
   resp.getWriter().println("<html>");
   resp.getWriter().println("<body>");
   
   try 
      {
      resp.getWriter().println("Updating status...<br>");
      ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();		
      twitterConfigBuilder.setDebugEnabled(false);
      
      //Replace these values with your own
      System.setProperty("WORDNIK_API_KEY", "YourWordnikKey");
      twitterConfigBuilder.setOAuthConsumerKey("ConsumerKey");
      twitterConfigBuilder.setOAuthConsumerSecret("ConsumerSecret");
      twitterConfigBuilder.setOAuthAccessToken("AccessToken");
      twitterConfigBuilder.setOAuthAccessTokenSecret("AccessTokenSecret");
	
     twit = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
     
     if(r.nextInt(2) == 0)
        {
        buffer.insert(0,feed.title());
        Trends t = twit.getPlaceTrends(1);  //global trends
        buffer.append(" "+t.getTrends()[r.nextInt(t.getTrends().length)].getName()); 
        //invent a trend
        buffer.append(" #" + WordsApi.randomWord().getWord() + WordsApi.randomWord().getWord()); 
        }
     
     else
        {
        Word random = WordsApi.randomWord();
        Example ex = WordApi.topExample(random.getWord());;
        buffer.insert(0, ex.getText());
        if(buffer.length() < 120)
           {
           Trends t = twit.getPlaceTrends(1);  //global trends
           buffer.append(" "+t.getTrends()[r.nextInt(t.getTrends().length)].getName());
           }
        }
     if(buffer.length() > 140)
        {
        buffer.setLength(buffer.lastIndexOf(" ", 120));  //Tweets are maximum 140 characters
        buffer.append(end[(r.nextInt(end.length))]);
        }
     twit.updateStatus(buffer.toString());
     resp.getWriter().println("Tweet posted: "+ buffer.toString());
     }
   catch(FileNotFoundException e)
     {
     e.printStackTrace(System.err);
     resp.getWriter().println("Input file(s) not found<br>");
     resp.getWriter().println("<pre>");
     e.printStackTrace(resp.getWriter());
     resp.getWriter().println("</pre>");
     }
   catch(TwitterException e)
       {
       resp.getWriter().println("Problem with Twitter <br>");
       resp.getWriter().println("<pre>");
       e.printStackTrace(resp.getWriter());
       resp.getWriter().println("</pre>");
       }
   catch(KnickerException e)
        {
        e.printStackTrace(System.err);
        resp.getWriter().println("Problem with Wordnik <br>");
        resp.getWriter().println("<pre>");
        e.printStackTrace(resp.getWriter());
        resp.getWriter().println("</pre>");
        } 
   catch(FeedException e)
       {
       resp.getWriter().println("Problem with RSS Feed <br>");
       resp.getWriter().println("<pre>");
       e.printStackTrace(resp.getWriter());
       resp.getWriter().println("</pre>");
       }
   catch(Exception e)
      {
      e.printStackTrace(System.err);
      resp.getWriter().println("<pre>");
      e.printStackTrace(resp.getWriter());
      resp.getWriter().println("</pre>");
      }
    }
}
