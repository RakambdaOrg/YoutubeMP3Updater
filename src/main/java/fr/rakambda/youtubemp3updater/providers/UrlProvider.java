package fr.rakambda.youtubemp3updater.providers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;

public interface UrlProvider{
	@Nullable
	static UrlProvider get(@NotNull String name, @NotNull String id){
		if(YoutubeProvider.NAME.equals(name)){
			return new YoutubeProvider(id);
		}
		return null;
	}
	
	@NotNull
	String getId();
	
	@NotNull
	String getName();
	
	@Nullable
	URL getURL();
}
