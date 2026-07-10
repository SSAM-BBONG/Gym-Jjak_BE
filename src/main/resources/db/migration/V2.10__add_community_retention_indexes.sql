CREATE INDEX idx_community_posts_deleted_at_id
    ON community_posts (deleted_at, community_post_id);

CREATE INDEX idx_community_comments_deleted_at_id
    ON community_comments (deleted_at, community_comment_id);
