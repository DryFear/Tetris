package ru.unfortunately.school.tetris.room;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigration extends Migration {
    /**
     * С версии 1 на 2 добавился автоинкремент
     * Менять ничего не надо
     *
     */

    private int startVersion;
    private int endVersion;

    public DatabaseMigration(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

    }
}
