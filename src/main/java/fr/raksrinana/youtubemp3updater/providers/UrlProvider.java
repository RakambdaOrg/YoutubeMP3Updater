package fr.raksrinana.youtubemp3updater.providers;

import java.net.URL;

public interface UrlProvider{
	static UrlProvider get(String name, String id){
		switch(name){
			case YoutubeProvider.NAME:
				return new YoutubeProvider(id);
		}
		return null;
	}
	
	String getId();
	
	String getName();
	
	URL getURL();
}
