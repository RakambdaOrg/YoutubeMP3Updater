package fr.mrcraftcod.youtubemp3updater.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
public class JSONIDS
{
	public static ArrayList<URL> parse(File file) throws IOException
	{
		if(file == null || !file.exists())
			return new ArrayList<>();
		ArrayList<URL> urls = new ArrayList<>();
		StringBuilder stringBuilder = new StringBuilder();
		Files.readAllLines(Paths.get(file.toURI())).forEach(stringBuilder::append);
		for(String id : stringBuilder.toString().replace("\"", "").replace(" ", "").replace("[", "").replace("]", "").split(","))
			urls.add(new URL("https://www.youtube.com/watch?v=" + id));
		return urls;
	}
}
