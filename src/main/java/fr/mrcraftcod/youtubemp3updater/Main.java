package fr.mrcraftcod.youtubemp3updater;

import fr.mrcraftcod.utils.base.FileUtils;
import fr.mrcraftcod.youtubemp3updater.objects.DownloaderCallable;
import fr.mrcraftcod.youtubemp3updater.utils.Configuration;
import fr.mrcraftcod.youtubemp3updater.utils.JSONIDS;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	
	public static void main(final String[] args) throws IOException, ClassNotFoundException, InterruptedException
	{
		Configuration config = new Configuration(new File(new File(FileUtils.getAppDataFolder(), "YoutubeMP3Updater"), "config.db"));
		if(args.length == 0)
		{
			processFile(FileUtils.askFile(), config);
		}
		else
		{
			switch(args[0])
			{
				case "-r":
					for(var i = 1; i < args.length; i++)
					{
						LOGGER.info("Removing video {}", args[i]);
						config.removeVideo(args[i]);
					}
					break;
				default:
					if(new File(args[0]).exists())
						processFile(new File(args[0]), config);
					else
						LOGGER.error("Wrong arguments");
			}
		}
		config.close();
		System.exit(0);
	}
	
	private static void processFile(final File file, final Configuration config) throws IOException
	{
		//ArrayList<URL> videos = ChromeBookmarks.getBarBookmarks("YTMP3");
		final var videos = JSONIDS.parse(file);
		final var executorService = Executors.newFixedThreadPool(2);
		final var futures = new ArrayList<Future<Pair<String, Boolean>>>();
		for(final var url : videos)
		{
			final String videoID;
			if((videoID = getVideoID(url)) != null && !config.isVideoDone(videoID))
				futures.add(executorService.submit(new DownloaderCallable(videoID, Paths.get(FileUtils.getDesktopFolder("YTMP3").toURI()))));
		}
		executorService.shutdown();
		futures.forEach(f -> {
			try
			{
				final var result = f.get();
				if(result.getValue())
					config.setVideoDone(result.getKey());
				
			}
			catch(final InterruptedException | ExecutionException e)
			{
				LOGGER.warn("Error getting result", e);
			}
		});
	}
	
	private static String getVideoID(final URL url){
		if(!url.getHost().equals("www.youtube.com"))
			return null;
		final var parameters = splitQuery(url);
		return parameters.getOrDefault("v", null);
	}

	private static Map<String, String> splitQuery(final URL url){
		final Map<String, String> query_pairs = new LinkedHashMap<>();
		final var query = url.getQuery();
		final var pairs = query.split("&");
		for (final var pair : pairs)
		{
			final var idx = pair.indexOf("=");
			query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
		}
		return query_pairs;
	}
}
