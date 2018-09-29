package fr.mrcraftcod.youtubemp3updater.utils;

import fr.mrcraftcod.utils.config.SQLiteManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Configuration extends SQLiteManager
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
	
	private static final String KEY_LABEL = "Name";
	private static final String TABLE_DB_FILE = "Config";
	private final List<String> watchedIDS = new ArrayList<>();

	public Configuration(final File databaseURL) throws ClassNotFoundException, InterruptedException
	{
		super(databaseURL);
		this.sendUpdateRequest("CREATE TABLE IF NOT EXISTS " + TABLE_DB_FILE + "(" + KEY_LABEL + " varchar(75), PRIMARY KEY (" + KEY_LABEL + "));").waitSafely();
		this.sendQueryRequest("SELECT * FROM " + TABLE_DB_FILE).done(result -> {
			try
			{
				while(result.next())
					watchedIDS.add(result.getString(KEY_LABEL));
			}
			catch(SQLException e)
			{
				LOGGER.error("Error getting downloaded videos", e);
			}
		}).waitSafely();
	}

	public boolean isVideoDone(final String videoID)
	{
		return watchedIDS.contains(videoID);
	}

	public void setVideoDone(final String videoID)
	{
		watchedIDS.add(videoID);
		this.sendUpdateRequest("INSERT INTO " + TABLE_DB_FILE + " VALUES('" + videoID + "');").done(integer -> LOGGER.info("Marked {} as done", videoID)).fail(error -> LOGGER.error("Failed to mark {} as done", videoID, error));
	}

	public void removeVideo(final String videoID)
	{
		if(watchedIDS.remove(videoID))
			this.sendUpdateRequest("DELETE FROM " + TABLE_DB_FILE + " WHERE " + KEY_LABEL + "= '" + videoID + "';");
	}
}
