package fr.rakambda.youtubemp3updater.providers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.StringJoiner;

@Getter
@Log4j2
@EqualsAndHashCode
@RequiredArgsConstructor
public class YoutubeProvider implements UrlProvider{
	public static final String NAME = "YouTube";
	
	@NonNull
	private final String id;
	
	@Override
	public String toString(){
		return new StringJoiner(", ", YoutubeProvider.class.getSimpleName() + "[", "]").add("id='" + id + "'").toString();
	}
	
	@Override
	@NotNull
	public String getName(){
		return NAME;
	}
	
	@Override
	@Nullable
	public URL getURL(){
		try{
			return URI.create("https://www.youtube.com/watch?v=" + getId()).toURL();
		}
		catch(MalformedURLException e){
			log.error("Failed to create link for video ID {}", getId(), e);
			return null;
		}
	}
}
