package fr.raksrinana.youtubemp3updater.parsers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.raksrinana.youtubemp3updater.providers.UrlProvider;
import fr.raksrinana.youtubemp3updater.providers.YoutubeProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JSonParser implements Parser{
	private static final ObjectMapper mapper;
	private final Path file;
	
	public JSonParser(@NonNull final Path file){
		this.file = file;
	}
	
	public Collection<UrlProvider> parse(){
		if(file.toFile().exists()){
			try(final var fis = Files.newBufferedReader(file)){
				return mapper.readValue(fis, new TypeReference<Set<String>>(){}).stream()
						.map(YoutubeProvider::new)
						.collect(Collectors.toSet());
			}
			catch(final IOException e){
				log.error("Failed to read ids in {}", file, e);
			}
		}
		return Collections.emptySet();
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
