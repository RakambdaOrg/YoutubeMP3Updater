package fr.raksrinana.youtubemp3updater.providers;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.StringJoiner;

@Log4j2
public class YoutubeProvider implements UrlProvider{
	public static final String NAME = "YouTube";
	@Getter
	private final String id;
	
	public YoutubeProvider(@NonNull String id){
		this.id = id;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		YoutubeProvider that = (YoutubeProvider) o;
		return id.equals(that.id);
	}
	
	@Override
	public String toString(){
		return new StringJoiner(", ", YoutubeProvider.class.getSimpleName() + "[", "]").add("id='" + id + "'").toString();
	}
	
	@Override
	public String getName(){
		return NAME;
	}
	
	@Override
	public URL getURL(){
		try{
			return new URL("https://www.youtube.com/watch?v=" + getId());
		}
		catch(MalformedURLException e){
			log.error("Failed to create link for video ID {}", getId(), e);
		}
		return null;
	}
}
