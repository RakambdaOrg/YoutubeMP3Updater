package fr.rakambda.youtubemp3updater.storage;

import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import org.jetbrains.annotations.NotNull;

public interface IStorage extends AutoCloseable{
	void fetchWatchedIDs();
	
	boolean isVideoDone(@NotNull UrlProvider provider);
	
	void removeVideo(@NotNull UrlProvider provider);
	
	void setVideoDone(@NotNull UrlProvider provider);
}
