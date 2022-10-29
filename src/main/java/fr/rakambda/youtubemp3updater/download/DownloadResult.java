package fr.rakambda.youtubemp3updater.download;

import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import lombok.Getter;

@Getter
public class DownloadResult{
	private final UrlProvider provider;
	private final boolean downloaded;
	
	public DownloadResult(UrlProvider provider, boolean downloaded){
		this.provider = provider;
		this.downloaded = downloaded;
	}
}
