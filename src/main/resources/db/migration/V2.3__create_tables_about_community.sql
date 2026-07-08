CREATE TABLE community_posts (
                                 community_post_id BIGINT NOT NULL AUTO_INCREMENT,
                                 user_id BIGINT NOT NULL,
                                 type VARCHAR(30) NOT NULL,
                                 title VARCHAR(100) NOT NULL,
                                 content TEXT NOT NULL,
                                 view_count BIGINT NOT NULL DEFAULT 0,
                                 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                 updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                 deleted_at DATETIME(6) NULL,

                                 CONSTRAINT pk_community_posts
                                     PRIMARY KEY (community_post_id),

                                 INDEX idx_community_posts_type
                                     (type),

                                 INDEX idx_community_posts_created_at
                                     (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE community_comments (
                                    community_comment_id BIGINT NOT NULL AUTO_INCREMENT,
                                    community_post_id BIGINT NOT NULL,
                                    user_id BIGINT NOT NULL,
                                    content TEXT NOT NULL,
                                    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                    deleted_at DATETIME(6) NULL,

                                    CONSTRAINT pk_community_comments
                                        PRIMARY KEY (community_comment_id),

                                    INDEX idx_community_comments_post
                                        (community_post_id),

                                    INDEX idx_community_comments_post_created_at
                                        (community_post_id, created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE community_post_likes (
                                      community_post_like_id BIGINT NOT NULL AUTO_INCREMENT,
                                      community_post_id BIGINT NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                                      CONSTRAINT pk_community_post_likes
                                          PRIMARY KEY (community_post_like_id),

                                      CONSTRAINT uk_community_post_likes_post_user
                                          UNIQUE (community_post_id, user_id),

                                      INDEX idx_community_post_likes_post
                                          (community_post_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;