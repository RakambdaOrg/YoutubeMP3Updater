package fr.rakambda.youtubemp3updater.storage.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Log4j2
public class H2Storage extends BaseDatabase{
	private final Set<UrlProvider> watchedIDS;
	
	public H2Storage(@NonNull HikariDataSource dataSource){
		super(dataSource);
		watchedIDS = new HashSet<>();
	}
	
	@Override
	public void initDatabase() throws SQLException{
		execute("""
				CREATE TABLE IF NOT EXISTS Downloads(
				    Provider VARCHAR(32) NOT NULL,
				    VideoID VARCHAR(32),
				    PRIMARY KEY(Provider, VideoID)
				)""");
	}
	
	@Override
	public void fetchWatchedIDs(){
		try(var conn = getConnection();
				var statement = conn.prepareStatement("SELECT * FROM Downloads")){
			
			try(var result = statement.executeQuery()){
				while(result.next()){
					final var providerName = result.getString("Provider");
					final var videoID = result.getString("VideoID");
					watchedIDS.add(UrlProvider.get(providerName, videoID));
				}
			}
		}
		catch(SQLException e){
			log.error("Failed to fetch watched ids", e);
		}
	}
	
	@Override
	public boolean isVideoDone(@NotNull UrlProvider provider){
		return watchedIDS.contains(provider);
	}
	
	@Override
	public void removeVideo(@NotNull UrlProvider provider){
		if(watchedIDS.remove(provider)){
			try(var conn = getConnection();
					var statement = conn.prepareStatement("""
							DELETE FROM Downloads
							WHERE Provider = ? AND VideoID = ?""")){
				
				statement.setString(1, provider.getName());
				statement.setString(2, provider.getId());
				
				statement.execute();
			}
			catch(SQLException e){
				log.error("Failed to set delete {}", provider, e);
			}
		}
	}
	
	@Override
	public void setVideoDone(@NotNull UrlProvider provider){
		try(var conn = getConnection();
				var statement = conn.prepareStatement("""
						MERGE INTO Downloads(Provider, VideoID)
						VALUES(?,?)""")){
			
			statement.setString(1, provider.getName());
			statement.setString(2, provider.getId());
			
			statement.execute();
			log.info("Marked {} as done", provider);
		}
		catch(SQLException e){
			log.error("Failed to set element {} done", provider, e);
		}
	}
}
