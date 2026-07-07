CREATE TABLE community_post_views (
                                      community_post_view_id BIGINT NOT NULL AUTO_INCREMENT,
                                      community_post_id BIGINT NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                                      CONSTRAINT pk_community_post_views
                                          PRIMARY KEY (community_post_view_id),

                                      CONSTRAINT uk_community_post_views_post_user
                                          UNIQUE (community_post_id, user_id),

                                      INDEX idx_community_post_views_post
                                          (community_post_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;