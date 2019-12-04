package fr.raksrinana.youtubemp3updater.parsers;

import fr.raksrinana.youtubemp3updater.providers.UrlProvider;
import java.util.Collection;

public interface Parser{
	Collection<UrlProvider> parse();
}
