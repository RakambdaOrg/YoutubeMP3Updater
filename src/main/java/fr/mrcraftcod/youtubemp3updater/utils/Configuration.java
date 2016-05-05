package fr.mrcraftcod.youtubemp3updater.utils;

import fr.mrcraftcod.utils.config.SQLiteManager;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Configuration extends SQLiteManager
{
	private static final String KEY_LABEL = "Name";
	private static final String TABLE_DB_FILE = "Config";
	private final List<String> watchedIDS = new ArrayList<>();

	public Configuration(File databaseURL, boolean log) throws ClassNotFoundException, InterruptedException
	{
		super(databaseURL, log);
		this.sendUpdateRequest("CREATE TABLE IF NOT EXISTS " + TABLE_DB_FILE + "(" + KEY_LABEL + " varchar(75), PRIMARY KEY (" + KEY_LABEL + "));").waitSafely();
		this.sendQueryRequest("SELECT * FROM " + TABLE_DB_FILE).done(result -> {
			try
			{
				while(result.next())
					watchedIDS.add(result.getString(KEY_LABEL));
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}).waitSafely();
	}

	public boolean isVideoDone(String videoID)
	{
		return watchedIDS.contains(videoID);
	}

	public void setVideoDone(String videoID)
	{
		watchedIDS.add(videoID);
		this.sendUpdateRequest("INSERT INTO " + TABLE_DB_FILE + " VALUES(\"" + videoID + "\");");
	}
}
