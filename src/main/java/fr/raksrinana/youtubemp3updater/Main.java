package fr.raksrinana.youtubemp3updater;

import fr.raksrinana.utils.base.FileUtils;
import fr.raksrinana.youtubemp3updater.download.DownloaderCallable;
import fr.raksrinana.youtubemp3updater.parsers.JSonParser;
import fr.raksrinana.youtubemp3updater.parsers.Parser;
import fr.raksrinana.youtubemp3updater.providers.UrlProvider;
import fr.raksrinana.youtubemp3updater.utils.Configuration;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
public class Main{
	public static void main(final String[] args){
		final var parameters = new CLIParameters();
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setUnmatchedArgumentsAllowed(true);
		try{
			cli.parseArgs(args);
		}
		catch(final CommandLine.ParameterException e){
			log.error("Failed to parse arguments", e);
			cli.usage(System.out);
			return;
		}
		
		Optional.ofNullable(parameters.getInputPath()).or(FileUtils::askFile).ifPresent(inputFile -> {
			final Parser parser = new JSonParser(inputFile);
			final var providers = parser.parse();
			try(Configuration config = new Configuration(parameters.getDatabasePath().toAbsolutePath())){
				if(parameters.isDeleteInDb()){
					log.info("Removing videos from database {}", providers);
					providers.forEach(config::removeVideo);
				}
				else{
					processFile(config, providers, parameters.getOutputPath().toAbsolutePath());
				}
			}
			catch(ExecutionException | TimeoutException | InterruptedException | ClassNotFoundException e){
				log.error("Failed to process ids", e);
			}
		});
	}
	
	private static void processFile(final Configuration config, final Collection<UrlProvider> providers, final Path outputPath){
		final var executorService = Executors.newFixedThreadPool(2);
		final var futures = providers.stream().filter(provider -> !config.isVideoDone(provider)).map(provider -> executorService.submit(new DownloaderCallable(provider, outputPath))).collect(Collectors.toList());
		executorService.shutdown();
		futures.forEach(f -> {
			try{
				final var result = f.get();
				if(result.isDownloaded()){
					config.setVideoDone(result.getProvider());
				}
			}
			catch(final InterruptedException | ExecutionException e){
				log.warn("Error getting result", e);
			}
		});
	}
}
