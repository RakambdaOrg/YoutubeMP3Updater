package fr.raksrinana.youtubemp3updater;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import fr.raksrinana.utils.base.FileUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CLIParameters{
	@Parameter(names = {"--databasePath"}, description = "The path to the database containing the list of downloaded ids", converter = PathConverter.class)
	@Getter
	private Path databasePath = FileUtils.getAppDataFolder().resolve("YoutubeMP3Updater").resolve("downloads.db");
	@Parameter(names = {"--input"}, description = "The input file from the website", converter = PathConverter.class)
	@Getter
	private Path inputPath;
	@Parameter(names = {"--output"}, description = "The folder where to put the downloaded mp3", converter = PathConverter.class)
	@Getter
	private Path outputPath = FileUtils.getDesktopFolder().resolve("YTMP3");
	@Parameter(names = {"--delete"}, description = "If set, this will delete IDs in the database instead of downloading them")
	@Getter
	private boolean deleteInDb;
}

