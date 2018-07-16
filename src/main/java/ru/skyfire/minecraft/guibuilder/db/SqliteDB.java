package ru.skyfire.minecraft.guibuilder.db;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;

import static ru.skyfire.minecraft.guibuilder.GuiBuilder.logger;

public class SqliteDB {
    private static final String TABLE_NAME = "bingo";
    private Path databasePath;
    private Connection connection;

    public SqliteDB(Path dbPath) {
        initPath(dbPath);
        initConnection();
        initDb();
    }

    private void initPath(Path dbPath) {
        databasePath = dbPath.resolve("bingo.db");
        if(!databasePath.toFile().exists()) {
            try {
                databasePath.toFile().createNewFile();
            } catch (IOException e) {
                logger.error("Can not create file for database: " + databasePath.toString());
            }
        }
    }

    private void initConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath.toString());
        } catch (ClassNotFoundException e) {
            logger.error("Can not load sqlite driver");
        } catch (SQLException e) {
            logger.error("Can not create connection to database");
        }
    }

    private void initDb() {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME, null);
            if (!tables.next()) {
                createNewTable();
            }
        } catch (SQLException e) {
            logger.error("Failed to load database meta data");
        }
    }

    private void createNewTable() {
        String sql1 = "CREATE TABLE quest_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "uuid CHARACTER(36) NOT NULL, " +
                "group_id VARCHAR(255), " +
                "quest_id VARCHAR(255), " +
                "status VARCHAR(255), " +
                "time INTEGER" +
                ");";
        String sql2 = "CREATE UNIQUE INDEX quest_log_id_uindex ON quest_log (id);";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            stmt.close();
        } catch (SQLException e) {
            logger.error("Failed to create table");
            e.printStackTrace();
        }
    }
}
