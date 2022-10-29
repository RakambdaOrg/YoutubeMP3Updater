package fr.rakambda.youtubemp3updater.parsers;

import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import java.util.Collection;

public interface Parser{
	Collection<UrlProvider> parse();
}
