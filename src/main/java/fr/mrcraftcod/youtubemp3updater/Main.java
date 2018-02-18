package fr.mrcraftcod.youtubemp3updater;

import fr.mrcraftcod.utils.base.FileUtils;
import fr.mrcraftcod.youtubemp3updater.objects.DownloaderCallable;
import fr.mrcraftcod.youtubemp3updater.utils.Configuration;
import fr.mrcraftcod.youtubemp3updater.utils.JSONIDS;
import javafx.util.Pair;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main
{
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException
	{
		Configuration config = new Configuration(new File(new File(FileUtils.getAppDataFolder(), "YoutubeMP3Updater"), "config.db"), false);
		if(args.length == 0)
		{
			processFile(FileUtils.askFile(), config);
		}
		else
		{
			switch(args[0])
			{
				case "-r":
					for(int i = 1; i < args.length; i++)
					{
						System.out.println("Removing video " + args[i]);
						config.removeVideo(args[i]);
					}
					break;
				default:
					if(new File(args[0]).exists())
						processFile(new File(args[0]), config);
					else
						System.out.println("Wrong arguments");
			}
		}
		config.close();
		System.exit(0);
	}
	
	private static void processFile(File file, Configuration config) throws IOException
	{
		//ArrayList<URL> videos = ChromeBookmarks.getBarBookmarks("YTMP3");
		ArrayList<URL> videos = JSONIDS.parse(file);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		ArrayList<Future<Pair<String, Boolean>>> futures = new ArrayList<>();
		for(URL url : videos)
		{
			String videoID;
			if((videoID = getVideoID(url)) != null && !config.isVideoDone(videoID))
				futures.add(executorService.submit(new DownloaderCallable(videoID, Paths.get(FileUtils.getDesktopFolder("YTMP3").toURI()))));
		}
		executorService.shutdown();
		futures.forEach(f -> {
			try
			{
				Pair<String, Boolean> result = f.get();
				if(result.getValue())
					config.setVideoDone(result.getKey());
				
			}
			catch(InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		});
	}
	
	private static String getVideoID(URL url) throws UnsupportedEncodingException
	{
		if(!url.getHost().equals("www.youtube.com"))
			return null;
		Map<String, String> parameters = splitQuery(url);
		return parameters.getOrDefault("v", null);
	}

	private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException
	{
		Map<String, String> query_pairs = new LinkedHashMap<>();
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
