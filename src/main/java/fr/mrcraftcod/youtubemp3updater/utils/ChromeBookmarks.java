package fr.mrcraftcod.youtubemp3updater.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
public class ChromeBookmarks
{
	public static ArrayList<URL> getBarBookmarks(String folder) throws IOException, ParseException
	{
		ArrayList<URL> urls = new ArrayList<>();
		JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Bookmarks"));
		for(Object elementBarRaw : (JSONArray)getJSonPath(json, "roots.bookmark_bar.children"))
		{
			JSONObject elementBar = (JSONObject)elementBarRaw;
			if(elementBar.get("type").toString().equals("folder") && elementBar.get("name").toString().equals(folder))
				if(elementBar.containsKey("children"))
					for(Object elementBarFolderRaw : (JSONArray) elementBar.get("children"))
					{
						JSONObject elementFolderBar = (JSONObject) elementBarFolderRaw;
						if(elementFolderBar.get("type").toString().equals("url"))
							urls.add(new URL(elementFolderBar.get("url").toString()));
					}
		}
		return urls;
	}

	private static Object getJSonPath(JSONObject json, String path)
	{
		int dotPos = path.indexOf(".");
		if(dotPos > 0)
		{
			if(!json.containsKey(path.substring(0, dotPos)))
				return null;
			return getJSonPath((JSONObject) json.get(path.substring(0, dotPos)), path.substring(dotPos + 1));
		}
		if(!json.containsKey(path))
			return null;
		return json.get(path);
	}
}
