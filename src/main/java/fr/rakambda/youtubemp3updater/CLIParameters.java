package fr.rakambda.youtubemp3updater;

import fr.raksrinana.utils.base.FileUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import picocli.CommandLine;
import java.nio.file.Path;

@NoArgsConstructor
@Getter
@CommandLine.Command(name = "youtubemp3updater", mixinStandardHelpOptions = true)
public class CLIParameters{
	@CommandLine.Option(names = {"--databasePath"}, description = "The path to the database containing the list of downloaded ids")
	private final Path databasePath = FileUtils.getAppDataFolder().resolve("YoutubeMP3Updater").resolve("downloads.db");
	@CommandLine.Option(names = {"--output"}, description = "The folder where to put the downloaded mp3")
	private final Path outputPath = FileUtils.getDesktopFolder().resolve("YTMP3");
	@CommandLine.Option(names = {"--input"}, description = "The input file from the website")
	private Path inputPath;
	@CommandLine.Option(names = {"--delete"}, description = "If set, this will delete IDs in the database instead of downloading them")
	private boolean deleteInDb;
}

