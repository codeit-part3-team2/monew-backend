-- ===== Clean drop (drop children first) =====
DROP TABLE IF EXISTS article_keyword_logs CASCADE;
DROP TABLE IF EXISTS comment_likes CASCADE;
DROP TABLE IF EXISTS article_views CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS subscribes CASCADE;
DROP TABLE IF EXISTS interest_keywords CASCADE;
DROP TABLE IF EXISTS interest_articles CASCADE;
DROP TABLE IF EXISTS keywords CASCADE;
DROP TABLE IF EXISTS articles CASCADE;
DROP TABLE IF EXISTS interests CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ======================================================
-- Users
-- ======================================================
CREATE TABLE users
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(100) NOT NULL,
    password   VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMP    NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ======================================================
-- Articles
-- ======================================================
CREATE TABLE articles
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    source        VARCHAR(20)  NOT NULL,
    source_url    VARCHAR(500) NOT NULL UNIQUE,
    title         VARCHAR(200) NOT NULL,
    publish_date  TIMESTAMP    NOT NULL,
    summary       VARCHAR(200) NOT NULL,
    comment_count INT          NOT NULL DEFAULT 0,
    view_count    INT          NOT NULL DEFAULT 0,
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ======================================================
-- Article Views (per-user view tracking)
-- ======================================================
CREATE TABLE article_views
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    article_id BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_article_views UNIQUE (user_id, article_id),
    CONSTRAINT fk_article_views_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_article_views_article
        FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);
CREATE INDEX ix_article_views_user ON article_views (user_id);
CREATE INDEX ix_article_views_article ON article_views (article_id);

-- ======================================================
-- Interests
-- ======================================================
CREATE TABLE interests
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name             VARCHAR(100) NOT NULL UNIQUE,
    subscriber_count INT          NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ======================================================
-- Keywords
-- ======================================================
CREATE TABLE keywords
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    keyword    VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ======================================================
-- Interests <-> Keywords (M:N)
-- ======================================================
CREATE TABLE interest_keywords
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    interest_id BIGINT    NOT NULL,
    keyword_id  BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_interest_keywords UNIQUE (interest_id, keyword_id),
    CONSTRAINT fk_interest_keywords_interest
        FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE,
    CONSTRAINT fk_interest_keywords_keyword
        FOREIGN KEY (keyword_id) REFERENCES keywords (id) ON DELETE CASCADE
);
CREATE INDEX ix_interest_keywords_interest ON interest_keywords (interest_id);
CREATE INDEX ix_interest_keywords_keyword ON interest_keywords (keyword_id);

-- ======================================================
-- Interests <-> Articles (M:N)
-- ======================================================
CREATE TABLE interest_articles
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    interest_id BIGINT    NOT NULL,
    article_id  BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_interest_articles UNIQUE (interest_id, article_id),
    CONSTRAINT fk_interest_articles_interest
        FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE,
    CONSTRAINT fk_interest_articles_article
        FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);
CREATE INDEX ix_interest_articles_interest ON interest_articles (interest_id);
CREATE INDEX ix_interest_articles_article ON interest_articles (article_id);

-- ======================================================
-- Subscribes (user follows interest)
-- ======================================================
CREATE TABLE subscribes
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT    NOT NULL,
    interest_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_subscribes UNIQUE (user_id, interest_id),
    CONSTRAINT fk_subscribes_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_subscribes_interest
        FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE
);
CREATE INDEX ix_subscribes_user ON subscribes (user_id);
CREATE INDEX ix_subscribes_interest ON subscribes (interest_id);

-- ======================================================
-- Comments
-- ======================================================
CREATE TABLE comments
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    article_id BIGINT       NOT NULL,
    content    VARCHAR(500) NOT NULL,
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    like_count INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_article
        FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);
CREATE INDEX ix_comments_user ON comments (user_id);
CREATE INDEX ix_comments_article ON comments (article_id);

-- ======================================================
-- Comment Likes
-- ======================================================
CREATE TABLE comment_likes
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    comment_id BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_comment_likes UNIQUE (user_id, comment_id),
    CONSTRAINT fk_comment_likes_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_likes_comment
        FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE
);
CREATE INDEX ix_comment_likes_user ON comment_likes (user_id);
CREATE INDEX ix_comment_likes_comment ON comment_likes (comment_id);

-- ======================================================
-- Notifications
-- ======================================================
CREATE TABLE notifications
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    content       VARCHAR(100) NOT NULL,
    resource_type VARCHAR(30)  NOT NULL,
    resource_id   BIGINT       NOT NULL,
    confirmed     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- ======================================================
-- Article Keyword Logs (뉴스가 어떤 관심사·키워드로 수집됐는지 추적)
-- ======================================================
CREATE TABLE article_keyword_logs
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    article_id    BIGINT    NOT NULL,
    keyword_id    BIGINT    NOT NULL,
    interest_id   BIGINT    NOT NULL,
    collected_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_article_keyword_logs UNIQUE (article_id, keyword_id, interest_id),

    CONSTRAINT fk_article_keyword_logs_article
        FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE,

    CONSTRAINT fk_article_keyword_logs_keyword
        FOREIGN KEY (keyword_id) REFERENCES keywords (id) ON DELETE CASCADE,

    CONSTRAINT fk_article_keyword_logs_interest
        FOREIGN KEY (interest_id) REFERENCES interests (id) ON DELETE CASCADE
);

CREATE INDEX ix_article_keyword_logs_article ON article_keyword_logs (article_id);
CREATE INDEX ix_article_keyword_logs_keyword ON article_keyword_logs (keyword_id);
CREATE INDEX ix_article_keyword_logs_interest ON article_keyword_logs (interest_id);
