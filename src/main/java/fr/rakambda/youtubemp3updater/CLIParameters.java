package fr.rakambda.youtubemp3updater;

import lombok.Getter;
import lombok.NoArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.nio.file.Path;
import java.nio.file.Paths;

@NoArgsConstructor
@Getter
@Command(name = "youtubemp3updater", mixinStandardHelpOptions = true)
public class CLIParameters{
	@Option(names = {"--databasePath"}, description = "The path to the database containing the list of downloaded ids")
	private final Path databasePath = Paths.get("downloads.db");
	@Option(names = {"--output"}, description = "The folder where to put the downloaded mp3")
	private final Path outputPath = Paths.get("output");
	@Option(names = {"--input"}, description = "The input file from the website")
	private Path inputPath;
	@Option(names = {"--delete"}, description = "If set, this will delete IDs in the database instead of downloading them")
	private boolean deleteInDb;
}

