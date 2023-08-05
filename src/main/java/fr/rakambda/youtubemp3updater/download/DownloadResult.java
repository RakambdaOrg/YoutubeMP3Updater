package fr.rakambda.youtubemp3updater.download;

import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DownloadResult{
	@NonNull
	private final UrlProvider provider;
	private final boolean downloaded;
}
