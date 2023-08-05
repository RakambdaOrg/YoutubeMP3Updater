package fr.rakambda.youtubemp3updater.parsers;

import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;

public interface Parser{
	@NotNull
	Collection<UrlProvider> parse();
}
