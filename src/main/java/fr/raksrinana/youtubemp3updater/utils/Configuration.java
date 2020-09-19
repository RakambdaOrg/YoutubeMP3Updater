package fr.raksrinana.youtubemp3updater.utils;

import fr.raksrinana.utils.config.PreparedStatementFiller;
import fr.raksrinana.utils.config.SQLValue;
import fr.raksrinana.utils.config.SQLiteManager;
import fr.raksrinana.youtubemp3updater.providers.UrlProvider;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import static fr.raksrinana.utils.config.SQLValue.Type.STRING;

@Slf4j
public class Configuration extends SQLiteManager{
	private final Set<UrlProvider> watchedIDS = new HashSet<>();
	
	public Configuration(Path databaseURL) throws IOException, SQLException{
		super(databaseURL);
		this.sendUpdateRequest("CREATE TABLE IF NOT EXISTS Downloads(Provider varchar(32), VideoID varchar(32), PRIMARY KEY (Provider, VideoID))");
		this.sendCompletableQueryRequest("SELECT * FROM Downloads", result -> {
			final var providerName = result.getString("Provider");
			final var videoID = result.getString("VideoID");
			return UrlProvider.get(providerName, videoID);
		}).thenAccept(watchedIDS::addAll);
	}
	
	public boolean isVideoDone(final UrlProvider provider){
		return watchedIDS.contains(provider);
	}
	
	public void removeVideo(final UrlProvider provider) throws SQLException{
		if(watchedIDS.remove(provider)){
			this.sendPreparedUpdateRequest("DELETE FROM Downloads WHERE Provider = ? AND VideoID = ?", new PreparedStatementFiller(
					new SQLValue(STRING, provider.getName()),
					new SQLValue(STRING, provider.getId())));
		}
	}
	
	public void setVideoDone(final UrlProvider provider){
		watchedIDS.add(provider);
		this.sendCompletablePreparedUpdateRequest("INSERT INTO Downloads VALUES(?, ?)", new PreparedStatementFiller(
				new SQLValue(STRING, provider.getName()),
				new SQLValue(STRING, provider.getId()))
		).thenAccept(integer -> log.info("Marked {} as done", provider));
	}
}
