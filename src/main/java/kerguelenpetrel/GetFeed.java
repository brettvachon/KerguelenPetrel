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
/**
 * GetFeed - get title and description from a XML feed
 */

import java.util.Random;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.sun.syndication.io.FeedException;

public class GetFeed
 {
 private String feedURLfile;
 private XmlReader reader;
 private SyndFeed feed; 

 private GetLine feedURL;
 private SyndEntry entry;
 
 private static Random rand = new Random();
 
 /**Returns a title from a random XML feed
  * @return A title
  */
 public String title() 
   {
   return entry.getTitle();
   }
 
 /** Returns a description from a random XML feed
  * @return A description 
  */
 public String description()
    {
     return entry.getDescription().getValue();
    }

 /**Constructor - sets local values
  * @param urlFile File with XML URLs
  */ 
 public GetFeed(String urlFile) throws FileNotFoundException, IOException, FeedException
   {
   
   this.feedURLfile = urlFile;
   this.feedURL = new GetLine(feedURLfile);
   this.reader = new XmlReader(new URL(feedURL.line()));
   this.feed = new SyndFeedInput().build(reader);
   
   //Get a random entry
   int ind = rand.nextInt(feed.getEntries().size()); 
   this.entry = (SyndEntry)feed.getEntries().get(ind);
   }
 }

    
   

 

