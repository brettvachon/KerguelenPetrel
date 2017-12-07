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
import java.io.InputStream;
import java.net.URL;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.EnumSet; 

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.WordsApi;
import net.jeremybrooks.knicker.dto.Word;
import net.jeremybrooks.knicker.Knicker.PartOfSpeech; 

@SuppressWarnings("serial")

public class UpdateStatusServlet extends HttpServlet 
  {
  public static final String feedsFile = "WEB-INF/StaticFiles/feeds";
  public static final String[] separator = new String[] { "?","!",",","-"," " };
  public static final Random r = new Random();
 
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
    {
    StringBuilder builder = new StringBuilder();   
   
    resp.setContentType("text/plain; charset=UTF-8");
    resp.getWriter().println("Updating status...");
    
    try 
      {
      //Append feed title
      builder.append(getFeedTitle(resp));
     
      //Append Wordnik example sentence
      builder.append(getWordnikSentence(resp));
         
     /* Tweets are maximum 280 characters, so trim our sentence appropriately */
     if(builder.length() > 280) 
        {
        if(builder.lastIndexOf(";",220) > 0)
           builder.setLength(builder.lastIndexOf(";",220)); 
        else if(builder.lastIndexOf(":",220) > 0)
           builder.setLength(builder.lastIndexOf(":", 220));  
        else if(builder.lastIndexOf(",",220) > 0)
           builder.setLength(builder.lastIndexOf(",", 220));
        else builder.setLength(220);
        }
     
      //Append a Global trend
      Twitter twit = TwitterFactory.getSingleton();
      builder.append(" "+twit.getPlaceTrends(1).getTrends()[r.nextInt(twit.getPlaceTrends(1).getTrends().length)].getName());
     
      // Append a Wordnik trend
      builder.append(getWordnikTrend(resp));
          
      if(builder.length() > 280)       
         builder.setLength(280); //Tweets are limited to 280 characters
     
      //Set the status
      StatusUpdate status = new StatusUpdate(builder.toString());
     
      /* Add an image from Flickr for small status */
      if(builder.length() < 180)
         status.setMediaIds(addFlickrImg(twit,resp));   

      twit.updateStatus(status);
      resp.getWriter().println("Tweet posted: "+ status.getStatus());
      }
 
     catch(TwitterException e)
       {
       resp.getWriter().println("Problem with Twitter \n");
       e.printStackTrace(resp.getWriter());
       }
    }
 
    public static String getFeedTitle(HttpServletResponse resp) throws IOException
      {
      StringBuilder builder = new StringBuilder();
        try 
          {
          GetFeed feed = new GetFeed(feedsFile); 
          builder.append(feed.title());
          builder.append(separator[(r.nextInt(separator.length))] + " ");
          }
        catch(FileNotFoundException e)
          {
          resp.getWriter().println("Input file(s) not found \n");
          e.printStackTrace(resp.getWriter());
          }
        catch(FeedException e)
          {
          resp.getWriter().println("Problem with RSS Feed \n");
          e.printStackTrace(resp.getWriter());
          }
       return builder.toString();
       }
    
    public static String getWordnikSentence(HttpServletResponse resp) throws IOException
      {
       StringBuilder builder = new StringBuilder();
       try
          {
          Properties p = new Properties();
          InputStream in = UpdateStatusServlet.class.getResourceAsStream("wordnik.properties");
          p.load(in);
          System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
       
          builder.append(WordApi.topExample(WordsApi.randomWord().getWord()).getText());
          }
       catch(FileNotFoundException e)
          {
          resp.getWriter().println("Wordnik property file not found \n");
          e.printStackTrace(resp.getWriter());
          }
       catch(KnickerException e)
          {
         resp.getWriter().println("Problem with Wordnik \n");
         e.printStackTrace(resp.getWriter());
          }  
       return builder.toString();
      }
    
    public static long[] addFlickrImg(Twitter twit, HttpServletResponse resp) throws IOException, TwitterException
      {
      long[] mediaID = new long[1];
      StringBuilder imageFeedUrl = new StringBuilder();
      String tag;
      
      imageFeedUrl.append("http://api.flickr.com/services/feeds/photos_public.gne");
      
      try
         {
         Properties p = new Properties();
         InputStream in = UpdateStatusServlet.class.getResourceAsStream("wordnik.properties");
         p.load(in);
         System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
         
         boolean hasDictionaryDef = false; 
         EnumSet<PartOfSpeech> includePartOfSpeech = null; 
         EnumSet<PartOfSpeech> excludePartOfSpeech = null; 
         int minCorpusCount = 0; 
         int maxCorpusCount = 0; 
         int minDictionaryCount = 0; 
         int maxDictionaryCount = 0; 
         int minLength = 3; 
         int maxLength = 6; 
        
         Word result = WordsApi.randomWord(hasDictionaryDef, includePartOfSpeech, excludePartOfSpeech, minCorpusCount, maxCorpusCount, minDictionaryCount, maxDictionaryCount, minLength, maxLength); 
         tag = result.getWord();
         imageFeedUrl.append("?tag=").append(tag);
         }
      
      catch(FileNotFoundException e)
         {
         resp.getWriter().println("Wordnik property file not found \n");
         e.printStackTrace(resp.getWriter());
         }
      catch(KnickerException e)
         {
         resp.getWriter().println("Problem with Wordnik \n");
         e.printStackTrace(resp.getWriter());
         }  
            
      try 
         {
         XmlReader reader = new XmlReader(new URL(imageFeedUrl.toString()));
         SyndFeed feed = new SyndFeedInput().build(reader);
      
         int ind = r.nextInt(feed.getEntries().size()); 
         SyndEntry entry =  (SyndEntry)feed.getEntries().get(ind); //Get a random entry
      
         //Get our links from the feed
         List<SyndLink> links = entry.getLinks();
      
         //get the URL for the image
         String mediaURL = links.get(1).getHref();
         InputStream imageStream = new URL(mediaURL).openConnection().getInputStream(); 
         mediaID[0] = twit.uploadMedia(mediaURL, imageStream).getMediaId();
         }
      catch(FeedException e)
         {
         resp.getWriter().println("Problem with RSS Feed \n");
         e.printStackTrace(resp.getWriter());
         }
      return mediaID;
       }
    
    public static String getWordnikTrend(HttpServletResponse resp) throws IOException
       {
       StringBuilder trend = new StringBuilder();
       String[] results = new String[2]; //Trend will be made up of 2 words
    
       try
          {
          Properties p = new Properties();
          InputStream in = UpdateStatusServlet.class.getResourceAsStream("wordnik.properties");
          p.load(in);
          System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
          }
       catch(FileNotFoundException e)
          {
          resp.getWriter().println("Wordnik property file not found \n");
          e.printStackTrace(resp.getWriter());
          }
       try
          {
          for(int i=0; i<results.length;i++)
             {
             results[i] = WordsApi.randomWord().getWord();
             while(results[i].contains("-")) //reject words with dashes
                results[i] = WordsApi.randomWord().getWord();
             }
          //Build the trend
          trend.append(" #");
          for(String res : results)
             trend.append(res);
          }
       catch(KnickerException e)
          {
          resp.getWriter().println("Problem with Wordnik \n");
          e.printStackTrace(resp.getWriter());
          }  
       return trend.toString(); 
       }     
     }