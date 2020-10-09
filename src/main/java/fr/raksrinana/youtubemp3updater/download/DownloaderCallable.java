package fr.raksrinana.youtubemp3updater.download;

import fr.raksrinana.youtubemp3updater.providers.UrlProvider;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
public class DownloaderCallable implements Callable<DownloadResult>{
	private final UrlProvider provider;
	private final Path path;
	
	public DownloaderCallable(final UrlProvider provider, final Path path){
		this.provider = provider;
		this.path = path;
	}
	
	@Override
	public DownloadResult call(){
		try{
			return new DownloadResult(this.provider, Objects.equals(0, executeCommand("youtube-dl --extract-audio --audio-format mp3 " + this.provider.getURL().toString(), path)));
		}
		catch(final IOException | InterruptedException e){
			log.warn("Error downloading {}", provider, e);
		}
		return new DownloadResult(this.provider, false);
	}
	
	private int executeCommand(String command, final Path path) throws IOException, InterruptedException{
		log.info("Executing command: {}", command);
		Files.createDirectories(path);
		final var proc = Runtime.getRuntime().exec(command, null, path.toFile());
		String s;
		try(final var stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()))){
			while((s = stdInput.readLine()) != null){
				log.debug(s);
			}
		}
		
		try(final var stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()))){
			while((s = stdError.readLine()) != null){
				log.warn(s);
			}
		}
		return proc.waitFor();
	}
}
