package fr.mrcraftcod.youtubemp3updater;

import fr.mrcraftcod.utils.FileUtils;
import fr.mrcraftcod.youtubemp3updater.objects.VideoWorker;
import fr.mrcraftcod.youtubemp3updater.utils.Configuration;
import fr.mrcraftcod.youtubemp3updater.utils.JSONIDS;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main
{
	public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException, InterruptedException
	{
		//ArrayList<URL> videos = ChromeBookmarks.getBarBookmarks("YTMP3");
		ArrayList<URL> videos = JSONIDS.parse(FileUtils.askFile());
		Configuration config = new Configuration(new File(System.getProperty("user.home") + "\\AppData\\Roaming\\YoutubeMP3Updater\\config.db"), false);
		for(URL url : videos)
		{
			String videoID = null;
			if((videoID = getVideoID(url)) != null && !config.isVideoDone(videoID))
				new VideoWorker(config, videoID).onDone();
		}
		config.close();
		System.exit(0);
	}

	private static String getVideoID(URL url) throws UnsupportedEncodingException
	{
		if(!url.getHost().equals("www.youtube.com"))
			return null;
		Map<String, String> parameters = splitQuery(url);
		return parameters.containsKey("v") ? parameters.get("v") : null;
	}

	private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException
	{
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		String query = url.getQuery();
		String[] pairs = query.split("&");
		for (String pair : pairs)
		{
			int idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		}
		return query_pairs;
	}
}
