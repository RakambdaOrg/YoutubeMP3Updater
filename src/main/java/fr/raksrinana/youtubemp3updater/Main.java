package fr.raksrinana.youtubemp3updater;

import fr.raksrinana.utils.base.FileUtils;
import fr.raksrinana.youtubemp3updater.download.DownloaderCallable;
import fr.raksrinana.youtubemp3updater.parsers.JSonParser;
import fr.raksrinana.youtubemp3updater.parsers.Parser;
import fr.raksrinana.youtubemp3updater.providers.UrlProvider;
import fr.raksrinana.youtubemp3updater.utils.Configuration;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Log4j2
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
					providers.forEach(provider -> {
						try{
							config.removeVideo(provider);
						}
						catch(SQLException e){
							log.error("Failed to remove video {}", provider, e);
						}
					});
				}
				else{
					try{
						config.fetchWatchedIDs();
						processFile(config, providers, parameters.getOutputPath().toAbsolutePath());
					}
					catch(InterruptedException | TimeoutException | ExecutionException e){
						log.error("Failed to process downloads", e);
					}
				}
			}
			catch(SQLException | IOException e){
				log.error("Failed to process ids", e);
			}
		});
	}
	
	private static void processFile(final Configuration config, final Collection<UrlProvider> providers, final Path outputPath){
		final var executorService = Executors.newFixedThreadPool(2);
		final var futures = providers.stream()
				.filter(provider -> !config.isVideoDone(provider))
				.map(provider -> executorService.submit(new DownloaderCallable(provider, outputPath)))
				.collect(Collectors.toList());
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
