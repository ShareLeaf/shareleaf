CREATE SCHEMA IF NOT EXISTS public;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public.metadata (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    content_id varchar(12) NOT NULL,
    invalid_url boolean NOT NULL,
    processed boolean NOT NULL,
    has_audio boolean NOT NULL,
    encoding varchar(16),
    media_type varchar(16),
    description varchar,
    category varchar,
    title varchar,
    canonical_url varchar NOT NULL,
    view_count bigint NOT NULL,
    share_count bigint NOT NULL,
    like_count bigint NOT NULL,
    dislike_count bigint NOT NULL,
    updated_dt timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_dt timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(canonical_url)
);
ALTER TABLE public.metadata OWNER TO shareleaf;
comment on table public.metadata is 'Content metadata';

-- Create table indices
CREATE INDEX IF NOT EXISTS metadata_idx
    ON metadata (content_id, canonical_url);