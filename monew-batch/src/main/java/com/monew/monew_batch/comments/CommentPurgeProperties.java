package com.monew.monew_batch.comments;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "monew.comments.purge")
public class CommentPurgeProperties {

	private int retentionMinutes = 5;
	public int getRetentionMinutes() { return retentionMinutes; }
	public void setRetentionMinutes(int retentionMinutes) { this.retentionMinutes = retentionMinutes; }
}
