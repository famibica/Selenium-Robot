package com.sap.amd.parsers;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.gln.arf.entities.Dictionary;
import com.gln.arf.entities.Property;
import com.gln.arf.io.Decoder;
import com.gln.arf.io.Encoder;
import com.sap.amd.utils.Log;
import com.sap.amd.utils.StringUtils;

public class SearchParser
{
	public SearchParser() {}
	
	public void saveSearches(List<String> searches, boolean isBCP)
	{
		Log.write("Verifying 'Searches.arf' file...");		
		if (searches.size() > 0) {						
			Dictionary dictionary = new Dictionary();			
			int i = 0;
			for (String s : searches) {
				Log.write(s);
				//Check is the string is valid before adding to Dictionary
				//It was generating ArrayIndexOutOfBoundsException in the Encoder
				if ((s != null) && (s.length() > 0)) {
					dictionary.add(new Property<String>("Search" + i++, s));
				}
			}									
			Encoder encoder = new Encoder(dictionary);
			if(isBCP)
			{
				encoder.save(new File(".\\resources\\SearchesBCP.arf"));
			}
			else
			{
				encoder.save(new File(".\\resources\\SearchesICP.arf"));
			}
						
			Log.write("Saving searches to file...");
		}
		else {
			Log.write("No searches to save...");
		}
	}
	
	public List<String> parseSearches(boolean isBCP)
	{
		File file = null;
		if(isBCP)
		{
			Log.write("Loading searches from BCP...");
			file = new File(".\\resources\\SearchesBCP.arf");
		}
		else
		{
			Log.write("Loading searches from ICP...");
			file = new File(".\\resources\\SearchesICP.arf"); 
		}
		
		
		if (file.exists())
		{
			try
			{
				Dictionary searchesDictionary = new Decoder(file).decode();
				List<String> searches = new LinkedList<String>();
				
				for (int i = 0; i < searchesDictionary.size(); i++)
				{
					String search = (String)searchesDictionary.get(i).getValue();
					
					if (search != null && !StringUtils.isEmpty(search))
					{
						searches.add(search);
					}
				}

				Log.write(searches.size() + " search(es) loaded");
				
				return searches;
			}
			catch (Exception e)
			{
				Log.write(e);
				
				return new LinkedList<String>();
			}
		}
		else
		{
			return new LinkedList<String>();
		}
	}
}
