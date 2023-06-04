package net.fabricmc.notnotmelonclient.api;

import net.fabricmc.notnotmelonclient.util.Util;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.LOGGER;

public class RepoRequest extends ApiRequest {
	public Path outputPath;
	public RepoRequest(URL endpoint, Path outputPath) {
		super(endpoint, null);
		this.outputPath = outputPath;
	}

	@Override
	protected void doRequest(URL url) throws Exception {
		File file = outputPath.toFile();
		if (file.isDirectory()) {
			LOGGER.info("[nnc] Attempting repo pull. Endpoint: " + url.toString());
			try (Git git = Git.open(file)) {
				git.pull().call();
			} catch (RepositoryNotFoundException e) {
				LOGGER.info("[nnc] could not find repository at " + outputPath + ". Attempting clone.");
				if (Util.deleteFile(file))
					cloneRepository(url);
				else throw new FileSystemException(outputPath.toString());
			}
			return;
		}
		cloneRepository(url);
	}

	protected void cloneRepository(URL url) throws GitAPIException {
		LOGGER.info("[nnc] Attempting repo clone. Endpoint: " + url.toString());
		Git.cloneRepository()
			.setURI(url.toString())
			.setDirectory(outputPath.toFile())
			.setBranchesToClone(List.of("refs/heads/master"))
			.setBranch("refs/heads/master")
			.call();
	}
}
