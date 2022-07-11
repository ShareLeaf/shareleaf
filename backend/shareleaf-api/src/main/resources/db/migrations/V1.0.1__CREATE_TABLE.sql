CREATE TABLE IF NOT EXISTS public.cookie_jar (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    c_key varchar,
    c_path varchar,
    c_domain varchar,
    c_name varchar,
    c_value varchar,
    c_username varchar,
    c_host_only boolean,
    c_http_only boolean,
    c_persistent boolean,
    c_secure boolean,
    c_expires_at bigint,
    updated_dt timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_dt timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(c_name)
);
ALTER TABLE public.cookie_jar OWNER TO shareleaf;
comment on table public.cookie_jar is 'Persists request cookies';

-- Create table indices
CREATE INDEX IF NOT EXISTS cookie_jar_idx
    ON cookie_jar (c_key, c_username);