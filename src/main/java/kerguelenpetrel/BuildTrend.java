package kerguelenpetrel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordsApi;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class BuildTrend
   {
   private PrintWriter out;
   private Twitter twit;
   public static final Random r = new Random();
     
   public String wordnikTrend()
      {
      StringBuilder trend = new StringBuilder();
      String[] results = new String[2]; //Trend will be made up of 2 words
      
      try
         {
         Properties p = new Properties();
         InputStream in = UpdateStatusServlet.class.getResourceAsStream("wordnik.properties");
         p.load(in);
         System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));

         for(int i=0; i<results.length;i++)
            {
            results[i] = WordsApi.randomWord().getWord();
            while(results[i].contains("-")) //reject words with dashes
               results[i] = WordsApi.randomWord().getWord();
            }
         //Build the trend
         trend.append("#");
         for(String res : results)
            trend.append(res);
         }
      catch(KnickerException e)
         {
         out.println("Problem with Wordnik \n");
         e.printStackTrace(out);
         } 
      catch(FileNotFoundException e)
         {
         out.println("Wordnik property file not found \n");
         e.printStackTrace(out);
         }
      catch(IOException e)
         {
         out.println("Problem loading input stream");
         e.printStackTrace(out);
         }
      finally 
         {
         out.close();  // Always close the output writer
         }  
      return trend.toString();  
   }
   
   public String twitterTrend()
      {
      StringBuilder trend = new StringBuilder();

      try 
         { 
            //Get a trend using a WOEID from the list (change this list to match language of your tweet)
            int[] woeids = new int[] { 23424803,23424975,24554868,23416974,44418,23424775,2514815,2347563,2347580,2357024,2388929,12589778,2459115 };
      
            Trends t = twit.getPlaceTrends(woeids[r.nextInt(woeids.length)]);
            trend.append(t.getTrends()[r.nextInt(t.getTrends().length)].getName());
            }
         catch(TwitterException e)
            {
            out.println("Problem with Twitter!");
            e.printStackTrace(out);
            }
         finally 
            {
            out.close();  // Always close the output writer
            } 
      return trend.toString(); 
      }     
      
   
   public BuildTrend(HttpServletResponse resp, Twitter twit) throws IOException
      {
      this.out = resp.getWriter();
      this.twit = twit;
      resp.setContentType("text/plain; charset=UTF-8");
      }
   }