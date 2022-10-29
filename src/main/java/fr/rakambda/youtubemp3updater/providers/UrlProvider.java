package fr.rakambda.youtubemp3updater.providers;

import java.net.URL;

public interface UrlProvider{
	static UrlProvider get(String name, String id){
		if(YoutubeProvider.NAME.equals(name)){
			return new YoutubeProvider(id);
		}
		return null;
	}
	
	String getId();
	
	String getName();
	
	URL getURL();
}
