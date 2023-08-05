package fr.rakambda.youtubemp3updater.download;

import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

@Log4j2
@RequiredArgsConstructor
public class DownloaderCallable implements Callable<DownloadResult>{
	@NonNull
	private final UrlProvider provider;
	@NonNull
	private final Path path;
	
	@Override
	public DownloadResult call(){
		try{
			var commandResult = executeCommand(List.of("youtube-dl", "--extract-audio", "--audio-format", "mp3", this.provider.getURL().toString()), path);
			var downloaded = Objects.equals(0, commandResult);
			return new DownloadResult(this.provider, downloaded);
		}
		catch(IOException | InterruptedException e){
			log.warn("Error downloading {}", provider, e);
		}
		return new DownloadResult(this.provider, false);
	}
	
	private int executeCommand(@NonNull List<String> command, @NonNull Path path) throws IOException, InterruptedException{
		log.info("Executing command: {}", command);
		Files.createDirectories(path);
		
		var proc = Runtime.getRuntime().exec(command.toArray(String[]::new), null, path.toFile());
		
		String s;
		try(var stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()))){
			while((s = stdInput.readLine()) != null){
				log.debug(s);
			}
		}
		
		try(var stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()))){
			while((s = stdError.readLine()) != null){
				log.warn(s);
			}
		}
		return proc.waitFor();
	}
}
