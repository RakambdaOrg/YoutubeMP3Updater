package fr.mrcraftcod.youtubemp3updater.objects;

import fr.mrcraftcod.utils.base.OSUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloaderCallable.class);
	private final String videoID;
	private final Path path;
	
	public DownloaderCallable(final String videoID, final Path path)
	{
		this.videoID = videoID;
		this.path = path;
	}
	
	@Override
	public Pair<String, Boolean> call(){
		return ImmutablePair.of(this.videoID, downloadSong(this.videoID, this.path));
	}
	
	private boolean downloadSong(final String videoID, final Path path)
	{
		try
		{
			return executeCommand("youtube-dl --extract-audio --audio-format mp3 http://www.youtube.com/watch?v=" + videoID, path) == 0;
		}
		catch(final IOException | InterruptedException e)
		{
			LOGGER.warn("Error downloading {}", videoID, e);
		}
		return false;
	}
	
	private int executeCommand(String command, final Path path) throws IOException, InterruptedException
	{
		var beginning = "";
		final var ending = "";
		if(!OSUtils.isMac())
			beginning = "cmd /c start /wait ";
		command = beginning + command + ending;
		LOGGER.info("Executing command: {}", command);
		path.toFile().mkdirs();
		final var proc = Runtime.getRuntime().exec(command, null, path.toFile());
		
		final var stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		final var stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		
		String s;
		while((s = stdInput.readLine()) != null)
			LOGGER.info(s);
		
		while((s = stdError.readLine()) != null)
			LOGGER.warn(s);
		
		return proc.waitFor();
	}
}
