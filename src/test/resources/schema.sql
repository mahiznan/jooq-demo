-- Master tables for multi tenancy
CREATE TABLE cs_instance
(
    instance_id BIGINT NOT NULL PRIMARY KEY
);

CREATE TABLE cs_datagrid
(
    grid_id       BIGINT       NOT NULL PRIMARY KEY,
    instance_id   BIGINT       NOT NULL,
    datagrid_name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_dg_instance FOREIGN KEY (instance_id) REFERENCES
        cs_instance (instance_id),
    CONSTRAINT uk_dg_datagridname UNIQUE (instance_id, datagrid_name)
);

CREATE TABLE cs_datacluster
(
    cluster_id  BIGINT       NOT NULL PRIMARY KEY,
    instance_id BIGINT       NOT NULL,
    ip          VARCHAR(100) NOT NULL,
    grid_id     BIGINT       NOT NULL,
    CONSTRAINT fk_dc_instance FOREIGN KEY (instance_id) REFERENCES
        cs_instance (instance_id),
    CONSTRAINT fk_dc_datagrid FOREIGN KEY (grid_id) REFERENCES cs_datagrid (
                                                                            grid_id),
    CONSTRAINT uk_dc_ip UNIQUE (instance_id, ip)
);

CREATE TABLE cs_schemagroup
(
    schema_group_id    BIGINT       NOT NULL PRIMARY KEY,
    grid_id            BIGINT       NOT NULL,
    instance_id        BIGINT       NOT NULL,
    schema_group_name  VARCHAR(100) NOT NULL,
    is_dedicated_group BOOLEAN      NOT NULL,
    CONSTRAINT fk_sg_instance FOREIGN KEY (instance_id) REFERENCES
        cs_instance (instance_id),
    CONSTRAINT fk_sg_datagrid FOREIGN KEY (grid_id) REFERENCES cs_datagrid (
                                                                            grid_id),
    CONSTRAINT uk_sg_schemagroupname UNIQUE (instance_id, schema_group_name)
);

CREATE TABLE cs_schema
(
    schema_id       BIGINT       NOT NULL PRIMARY KEY,
    schema_group_id BIGINT       NOT NULL,
    cluster_id      BIGINT       NOT NULL,
    instance_id     BIGINT       NOT NULL,
    schema_name     VARCHAR(100) NOT NULL,
    CONSTRAINT fk_sc_instance FOREIGN KEY (instance_id) REFERENCES
        cs_instance (instance_id),
    CONSTRAINT fk_sc_schemagroup FOREIGN KEY (schema_group_id) REFERENCES
        cs_schemagroup (schema_group_id),
    CONSTRAINT fk_sc_datacluster FOREIGN KEY (cluster_id) REFERENCES
        cs_datacluster (cluster_id),
    CONSTRAINT uk_sc_schemaname UNIQUE (instance_id, schema_name)
);

CREATE TABLE cs_dataspace
(
    dataspace_id    BIGINT  NOT NULL PRIMARY KEY,
    schema_group_id BIGINT  NOT NULL,
    range_number    INT     NOT NULL,
    schema_id       BIGINT  NOT NULL,
    is_reserved     BOOLEAN NOT NULL,
    CONSTRAINT fk_ds_schemagroup FOREIGN KEY (schema_group_id) REFERENCES
        cs_schemagroup (schema_group_id),
    CONSTRAINT fk_ds_schema FOREIGN KEY (schema_id) REFERENCES cs_schema (
                                                                          schema_id),
    CONSTRAINT uk_ds_range UNIQUE (schema_group_id, range_number)
);

CREATE TABLE cs_account
(
    account_id    BIGINT NOT NULL PRIMARY KEY,
    instance_id   BIGINT NOT NULL,
    account_name  INT    NOT NULL,
    dataspace_id  BIGINT NOT NULL,
    status        INT    NOT NULL,
    reserved_time BIGINT,
    CONSTRAINT fk_a_instance FOREIGN KEY (instance_id) REFERENCES
        cs_instance (instance_id),
    CONSTRAINT fk_a_dataspace FOREIGN KEY (dataspace_id) REFERENCES
        cs_dataspace (dataspace_id),
    CONSTRAINT uk_a_account_name UNIQUE (instance_id, account_name)
);

-- For learning

CREATE TABLE language
(
    id          BIGINT(7) NOT NULL PRIMARY KEY,
    cd          CHAR(2) NOT NULL,
    description VARCHAR(50)
);

CREATE TABLE author
(
    id            BIGINT(7) NOT NULL PRIMARY KEY,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    year_of_birth BIGINT(7),
    distinguished BIGINT(1)
);

CREATE TABLE book
(
    id           BIGINT(7) NOT NULL PRIMARY KEY,
    author_id    BIGINT(7) NOT NULL,
    title        VARCHAR(400) NOT NULL,
    published_in BIGINT(7) NOT NULL,
    language_id  BIGINT(7) NOT NULL,
    CONSTRAINT fk_book_author FOREIGN KEY (author_id) REFERENCES author (id),
    CONSTRAINT fk_book_language FOREIGN KEY (language_id) REFERENCES language (id)
);

CREATE TABLE book_store
(
    name VARCHAR(400) NOT NULL UNIQUE
);

CREATE TABLE book_to_book_store
(
    name    VARCHAR(400) NOT NULL,
    book_id BIGINT       NOT NULL,
    stock   BIGINT,
    PRIMARY KEY (name,
                 book_id),
    CONSTRAINT fk_b2bs_book_store FOREIGN KEY (name) REFERENCES book_store (name) ON
        DELETE
        CASCADE,
    CONSTRAINT fk_b2bs_book FOREIGN KEY (book_id) REFERENCES book (id) ON
        DELETE
        CASCADE
);