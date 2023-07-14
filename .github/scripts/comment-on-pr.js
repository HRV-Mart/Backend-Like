const prNumber = process.argv[2];
const comment = '@dependabot squash and merge';

const github = require('@actions/github');
const octokit = github.getOctokit(process.env.GITHUB_TOKEN);

octokit.rest.issues.createComment({
  owner: process.env.GITHUB_REPOSITORY_OWNER,
  repo: process.env.GITHUB_REPOSITORY,
  issue_number: prNumber,
  body: comment
});
