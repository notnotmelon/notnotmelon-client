package net.fabricmc.notnotmelonclient.api;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class RepoRequest extends ApiRequest {
	public RepoRequest(URL url, Path outputPath) throws IOException, GitAPIException {

	}

	@Override
	protected void run(int index) {
		if (Files.isDirectory(outputPath)) {
			try (Git git = Git.open(outputPath.toFile())) {
				git.pull().call();
			}
			return;
		}

		Git.cloneRepository()
			.setURI(url.toString())
			.setDirectory(outputPath.toFile())
			.setBranchesToClone(List.of("refs/heads/master"))
			.setBranch("refs/heads/master")
			.call().close();
	}
}
