package fr.rakambda.youtubemp3updater;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.rakambda.youtubemp3updater.download.DownloaderCallable;
import fr.rakambda.youtubemp3updater.parsers.JSonParser;
import fr.rakambda.youtubemp3updater.providers.UrlProvider;
import fr.rakambda.youtubemp3updater.storage.IStorage;
import fr.rakambda.youtubemp3updater.storage.database.H2Storage;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@Log4j2
public class Main{
	public static void main(final String[] args){
		var parameters = new CLIParameters();
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setUnmatchedArgumentsAllowed(true);
		try{
			cli.parseArgs(args);
		}
		catch(CommandLine.ParameterException e){
			log.error("Failed to parse arguments", e);
			cli.usage(System.out);
			return;
		}
		
		Optional.ofNullable(parameters.getInputPath())
				.ifPresent(inputFile -> {
					var parser = new JSonParser(inputFile);
					var providers = parser.parse();
					try(var storage = getStorage(parameters)){
						if(parameters.isDeleteInDb()){
							log.info("Removing videos from database {}", providers);
							providers.forEach(storage::removeVideo);
						}
						else{
							storage.fetchWatchedIDs();
							processFile(storage, providers, parameters.getOutputPath().toAbsolutePath());
						}
					}
					catch(Exception e){
						log.error("Failed to process ids", e);
					}
				});
	}
	
	private static void processFile(@NotNull IStorage config, @NotNull Collection<UrlProvider> providers, @NotNull Path outputPath){
		try(var executorService = Executors.newFixedThreadPool(2)){
			var futures = providers.stream()
					.filter(provider -> !config.isVideoDone(provider))
					.map(provider -> executorService.submit(new DownloaderCallable(provider, outputPath)))
					.toList();
			
			futures.forEach(f -> {
				try{
					var result = f.get();
					if(result.isDownloaded()){
						config.setVideoDone(result.getProvider());
					}
				}
				catch(InterruptedException | ExecutionException e){
					log.warn("Error getting result", e);
				}
			});
		}
	}
	
	@NotNull
	private static IStorage getStorage(@NotNull CLIParameters parameters) throws SQLException{
		var h2 = new H2Storage(createH2Datasource(parameters.getDatabasePath()));
		h2.initDatabase();
		return h2;
	}
	
	@NotNull
	private static HikariDataSource createH2Datasource(@NotNull Path path){
		var config = new HikariConfig();
		config.setDriverClassName("org.h2.Driver");
		config.setJdbcUrl("jdbc:h2:" + path.toAbsolutePath());
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.setMaximumPoolSize(1);
		return new HikariDataSource(config);
	}
}
