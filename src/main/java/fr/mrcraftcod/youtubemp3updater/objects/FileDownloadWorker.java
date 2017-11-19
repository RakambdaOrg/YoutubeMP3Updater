package fr.mrcraftcod.youtubemp3updater.objects;

import fr.mrcraftcod.youtubemp3updater.utils.Configuration;
import java.io.IOException;
import java.io.PrintWriter;

public class FileDownloadWorker
{
	private final String videoID;
	private final Configuration config;
	private final PrintWriter pw;
	
	public FileDownloadWorker(Configuration config, PrintWriter pw, String videoID)
	{
		this.config = config;
		this.videoID = videoID;
		this.pw = pw;
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
		pw.println("youtube-dl --extract-audio --audio-format mp3 'http://www.youtube.com/watch?v=" + videoID + "'");
		return true;
	}
}
