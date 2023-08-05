package fr.rakambda.youtubemp3updater.parsers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import fr.rakambda.youtubemp3updater.providers.YoutubeProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class JSonParser implements Parser{
	private static final ObjectMapper mapper;
	
	@NonNull
	private final Path file;
	
	@NotNull
	public Collection<UrlProvider> parse(){
		if(!Files.exists(file)){
			return List.of();
		}
		
		try(var fis = Files.newBufferedReader(file)){
			return mapper.readValue(fis, new TypeReference<Set<String>>(){}).stream()
					.map(YoutubeProvider::new)
					.collect(Collectors.toSet());
		}
		catch(final IOException e){
			log.error("Failed to read ids in {}", file, e);
			return List.of();
		}
	}
	
	static{
		mapper = new ObjectMapper();
		mapper.setVisibility(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
}
