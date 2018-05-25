package fr.mrcraftcod.youtubemp3updater.objects;

import fr.mrcraftcod.utils.base.OSUtils;
import javafx.util.Pair;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 17/02/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-02-17
 */
public class DownloaderCallable implements Callable<Pair<String, Boolean>>
{
	private final String videoID;
	private final Path path;
	
	public DownloaderCallable(String videoID, Path path)
	{
		this.videoID = videoID;
		this.path = path;
	}
	
	@Override
	public Pair<String, Boolean> call() throws Exception
	{
		return new Pair<>(this.videoID, downloadSong(this.videoID, this.path));
	}
	
	private boolean downloadSong(String videoID, Path path)
	{
		
		try
		{
			return executeCommand("youtube-dl --extract-audio --audio-format mp3 http://www.youtube.com/watch?v=" + videoID, path) == 0;
		}
		catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	private int executeCommand(String command, Path path) throws IOException, InterruptedException
	{
		String beginning = "";
		String ending = "";
		if(!OSUtils.isMac())
			beginning = "cmd /c start /wait ";
		command = beginning + command + ending;
		System.out.println("Executing command: " + command);
		path.toFile().mkdirs();
		Process proc = Runtime.getRuntime().exec(command, null, path.toFile());
		
		boolean print = true;
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		
		String s;
		while((s = stdInput.readLine()) != null)
			if(print)
				System.out.println(s);
		
		while((s = stdError.readLine()) != null)
			System.out.println(s);
		
		return proc.waitFor();
	}
}
