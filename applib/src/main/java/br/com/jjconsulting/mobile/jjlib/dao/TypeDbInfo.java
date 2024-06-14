package br.com.jjconsulting.mobile.jjlib.dao;


/**
 * Tipo de dados do SQLite
 * @author Lucio Pelinson
 * @since 2018-02-27
 */
public enum TypeDbInfo {
    /**
     * INTEGER The Same Of:
     * INT; TINYINT; SMALLINT; MEDIUMINT; BIGINT; UNSIGNED BIG INT;
     */
    INTEGER,

    /**
     * TEXT The Same Of:
     * CHARACTER(20); VARCHAR(255); NCHAR(55); NVARCHAR(100); TEXT; CLOB;
     */
    TEXT,

    /**
     * BLOB The Same Of:
     * NONE (no datatype specified)
     */
    BLOB,

    /**
     * REAL The Same Of:
     * DOUBLE; DOUBLE PRECISION; FLOAT;
     */
    REAL,

    /**
     * NUMERIC The Same Of:
     * DECIMAL(10,5); BOOLEAN; DATE; DATETIME;
     */
    NUMERIC


}
