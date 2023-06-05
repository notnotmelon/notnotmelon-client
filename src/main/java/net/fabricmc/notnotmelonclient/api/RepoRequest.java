package net.fabricmc.notnotmelonclient.api;

import net.fabricmc.notnotmelonclient.util.Util;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.merge.ContentMergeStrategy;
import org.eclipse.jgit.merge.MergeStrategy;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.util.List;

import static net.fabricmc.notnotmelonclient.Main.LOGGER;

public class RepoRequest extends ApiRequest {
	public interface RepoCallback {
		void afterDownload();
	}

	public Path outputPath;
	public RepoCallback callback;
	public RepoRequest(URL endpoint, Path outputPath, RepoCallback callback) {
		super(endpoint, null);
		this.outputPath = outputPath;
		this.callback = callback;
	}

	@Override
	protected void doRequest(URL endpoint) throws Exception {
		File file = outputPath.toFile();
		if (file.isDirectory()) {
			LOGGER.info("[nnc] Attempting repo pull. Endpoint: " + endpoint.toString());
			try (Git git = Git.open(file)) {
				git.pull()
					.setContentMergeStrategy(ContentMergeStrategy.THEIRS)
					.setStrategy(MergeStrategy.THEIRS)
					.call();
				finish(endpoint);
			} catch (RepositoryNotFoundException e) {
				LOGGER.info("[nnc] could not find repository at " + outputPath + ". Attempting clone.");
				if (Util.deleteFile(file))
					cloneRepository(endpoint);
				else throw new FileSystemException(outputPath.toString());
			}
			return;
		}
		cloneRepository(endpoint);
	}

	protected void cloneRepository(URL endpoint) throws GitAPIException {
		LOGGER.info("[nnc] Attempting repo clone. Endpoint: " + endpoint.toString());
		Git.cloneRepository()
			.setURI(endpoint.toString())
			.setDirectory(outputPath.toFile())
			.setBranchesToClone(List.of("refs/heads/master"))
			.setBranch("refs/heads/master")
			.call()
			.close();
		finish(endpoint);
	}

	protected void finish(URL endpoint) {
		callback.afterDownload();
		LOGGER.info("[nnc] Successfully completed repo update. Endpoint: " + endpoint.toString());
	}
}
