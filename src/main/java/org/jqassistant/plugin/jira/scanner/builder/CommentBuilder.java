package org.jqassistant.plugin.jira.scanner.builder;

import com.atlassian.jira.rest.client.api.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.jqassistant.plugin.jira.cache.CacheEndpoint;
import org.jqassistant.plugin.jira.model.JiraComment;
import org.jqassistant.plugin.jira.model.JiraIssue;

@RequiredArgsConstructor
class CommentBuilder {
    private final CacheEndpoint cacheEndpoint;
    private final UserBuilder userBuilder;

    void handleComment(JiraIssue jiraIssue, Comment comment) {
        JiraComment jiraComment = this.cacheEndpoint.findOrCreateComment(comment);
        if (comment.getAuthor() != null) {
            jiraComment.setAuthor(this.userBuilder.findUserInCacheOrLoadItFromJira(comment.getAuthor()));
        }

        if (comment.getUpdateAuthor() != null) {
            jiraComment.setUpdateAuthor(this.userBuilder.findUserInCacheOrLoadItFromJira(comment.getUpdateAuthor()));
        }

        jiraIssue.getComments().add(jiraComment);
    }
}
