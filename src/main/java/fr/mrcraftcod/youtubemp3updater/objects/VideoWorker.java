package fr.mrcraftcod.youtubemp3updater.objects;

import com.mashape.unirest.request.GetRequest;
import fr.mrcraftcod.utils.Log;
import fr.mrcraftcod.utils.http.URLHandler;
import fr.mrcraftcod.youtubemp3updater.utils.Configuration;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import static fr.mrcraftcod.utils.FileUtils.sanitizeFileName;

public class VideoWorker
{
	private final String videoID;
	private final Configuration config;

	public VideoWorker(Configuration config, String videoID)
	{
		this.config = config;
		this.videoID = videoID;
	}

	public void onDone()
	{
		if(downloadVideo())
			config.setVideoDone(videoID);
	}

	private boolean downloadVideo()
	{
		HashMap<String, String> headers = new HashMap<>();
		HashMap<String, String> params = new HashMap<>();
		try
		{
			params.put("format", "JSON");
			params.put("video", "http://www.youtube.com/watch?v=" + videoID);
			GetRequest request = URLHandler.getRequest(new URL("http://www.youtubeinmp3.com/fetch/"), headers, params);
			if(request.asBinary().getStatus() != 200)
				return false;
			JSONObject json = request.asJson().getBody().getObject();
			if(json.has("link"))
			{
				int byteSize = 1024;
				File file = new File("D:\\Documents\\MP3", sanitizeFileName(json.getString("title").replaceAll("\"", "'")) + ".mp3");
				Log.info("Downloading video " + file.getAbsolutePath() + " " + videoID);
				if(!file.getParentFile().exists())
					file.getParentFile().mkdirs();
				byte[] data = new byte[byteSize];
				int i;
				long downloaded = 0;
				URLConnection connection = new URL(json.getString("link")).openConnection();
				try(BufferedInputStream is = new BufferedInputStream(connection.getInputStream()); BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file), byteSize))
				{
					long size = connection.getContentLengthLong();
					while((i = is.read(data, 0, 1024)) >= 0)
					{
						downloaded += i;
						bout.write(data, 0, i);
					}
					if(downloaded != size)
					{
						try
						{
							FileUtils.forceDeleteOnExit(file);
						}
						catch(Exception e){}
						Log.warning("Sizes mismatch (R: " + size + " / D: " + downloaded + ") " + videoID, null);
						return false;
					}
				}
				catch(IOException e)
				{
					Log.warning("Error downloading " + videoID, e);
					return false;
				}
				Log.info("Downloading video " + file.getAbsolutePath() + " " + videoID + " DONE " + downloaded);
				return true;
			}
		}
		catch(Exception e)
		{
			try
			{
				Desktop.getDesktop().browse(new URL("http://www.youtubeinmp3.com/download/?video=http://www.youtube.com/watch?v=" + videoID + "&autostart=1").toURI());
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			Log.warning("Video is probably being converted, please retry later (" + videoID + ")", e);
		}
		return false;
	}
}
