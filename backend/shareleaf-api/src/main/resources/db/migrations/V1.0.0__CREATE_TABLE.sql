CREATE SCHEMA IF NOT EXISTS public;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public.metadata (
    id UUID PRIMARY KEY NOT NULL,
    invalid_url boolean NOT NULL,
    processed boolean NOT NULL,
    encoding varchar(16),
    media_type varchar(16),
    description varchar,
    category varchar,
    title varchar,
    canonical_url varchar NOT NULL,
    view_count bigint NOT NULL,
    share_count boolean NOT NULL,
    like_count boolean NOT NULL,
    dislike_count boolean NOT NULL,
    updated_dt timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_dt timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE public.metadata OWNER TO root;
comment on table public.metadata is 'Content metadata';

-- Create table indices
CREATE INDEX IF NOT EXISTS metadata_idx
    ON metadata (id, canonical_url);