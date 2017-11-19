package fr.mrcraftcod.youtubemp3updater.objects;

import fr.mrcraftcod.youtubemp3updater.utils.Configuration;
import java.io.File;
import java.io.IOException;

public class DownloadWorker
{
	private final static File downloadFolder = new File(fr.mrcraftcod.utils.base.FileUtils.getDesktopFolder(), "YTMP3");
	private final String videoID;
	private final Configuration config;
	
	public DownloadWorker(Configuration config, String videoID)
	{
		this.config = config;
		this.videoID = videoID;
		downloadFolder.mkdirs();
	}
	
	public void onDone()
	{
		try
		{
			if(downloadVideo())
				config.setVideoDone(videoID);
		}
		catch(IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	private boolean downloadVideo() throws IOException, InterruptedException
	{
		return new ProcessBuilder("youtube-dl --extract-audio --audio-format mp3 'http://www.youtube.com/watch?v=" + videoID + "'").directory(downloadFolder).start().waitFor() == 0;
	}
}
