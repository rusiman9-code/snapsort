package com.snapsort.app.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.snapsort.app.data.dao.CollectionDao;
import com.snapsort.app.data.dao.CollectionDao_Impl;
import com.snapsort.app.data.dao.ScreenshotDao;
import com.snapsort.app.data.dao.ScreenshotDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CollectionDao _collectionDao;

  private volatile ScreenshotDao _screenshotDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `collections` (`id` TEXT NOT NULL, `name` TEXT NOT NULL COLLATE NOCASE, `created_at` INTEGER NOT NULL, `color` INTEGER NOT NULL, `is_default` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_collections_name` ON `collections` (`name`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_collections_created_at` ON `collections` (`created_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `screenshots` (`id` TEXT NOT NULL, `file_uri` TEXT NOT NULL, `file_name` TEXT NOT NULL, `file_size` INTEGER NOT NULL, `width` INTEGER NOT NULL, `height` INTEGER NOT NULL, `taken_at` INTEGER NOT NULL, `collection_id` TEXT NOT NULL DEFAULT 'default_unsorted', `ocr_status` TEXT NOT NULL DEFAULT 'PENDING', PRIMARY KEY(`id`), FOREIGN KEY(`collection_id`) REFERENCES `collections`(`id`) ON UPDATE NO ACTION ON DELETE SET DEFAULT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_screenshots_collection_id` ON `screenshots` (`collection_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_screenshots_taken_at` ON `screenshots` (`taken_at`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_screenshots_ocr_status` ON `screenshots` (`ocr_status`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `screenshot_contents` (`screenshot_id` TEXT NOT NULL, `raw_text` TEXT NOT NULL, `blocks_json` TEXT NOT NULL, `indexed_at` INTEGER NOT NULL, PRIMARY KEY(`screenshot_id`), FOREIGN KEY(`screenshot_id`) REFERENCES `screenshots`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '913e1a0a6b49f804cfbe5a6ab49fd63b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `collections`");
        db.execSQL("DROP TABLE IF EXISTS `screenshots`");
        db.execSQL("DROP TABLE IF EXISTS `screenshot_contents`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCollections = new HashMap<String, TableInfo.Column>(5);
        _columnsCollections.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollections.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollections.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollections.put("color", new TableInfo.Column("color", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollections.put("is_default", new TableInfo.Column("is_default", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCollections = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCollections = new HashSet<TableInfo.Index>(2);
        _indicesCollections.add(new TableInfo.Index("index_collections_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        _indicesCollections.add(new TableInfo.Index("index_collections_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        final TableInfo _infoCollections = new TableInfo("collections", _columnsCollections, _foreignKeysCollections, _indicesCollections);
        final TableInfo _existingCollections = TableInfo.read(db, "collections");
        if (!_infoCollections.equals(_existingCollections)) {
          return new RoomOpenHelper.ValidationResult(false, "collections(com.snapsort.app.data.model.CollectionEntity).\n"
                  + " Expected:\n" + _infoCollections + "\n"
                  + " Found:\n" + _existingCollections);
        }
        final HashMap<String, TableInfo.Column> _columnsScreenshots = new HashMap<String, TableInfo.Column>(9);
        _columnsScreenshots.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshots.put("file_uri", new TableInfo.Column("file_uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshots.put("file_name", new TableInfo.Column("file_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshots.put("file_size", new TableInfo.Column("file_size", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshots.put("width", new TableInfo.Column("width", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshots.put("height", new TableInfo.Column("height", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshots.put("taken_at", new TableInfo.Column("taken_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshots.put("collection_id", new TableInfo.Column("collection_id", "TEXT", true, 0, "'default_unsorted'", TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshots.put("ocr_status", new TableInfo.Column("ocr_status", "TEXT", true, 0, "'PENDING'", TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScreenshots = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysScreenshots.add(new TableInfo.ForeignKey("collections", "SET DEFAULT", "NO ACTION", Arrays.asList("collection_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesScreenshots = new HashSet<TableInfo.Index>(3);
        _indicesScreenshots.add(new TableInfo.Index("index_screenshots_collection_id", false, Arrays.asList("collection_id"), Arrays.asList("ASC")));
        _indicesScreenshots.add(new TableInfo.Index("index_screenshots_taken_at", false, Arrays.asList("taken_at"), Arrays.asList("ASC")));
        _indicesScreenshots.add(new TableInfo.Index("index_screenshots_ocr_status", false, Arrays.asList("ocr_status"), Arrays.asList("ASC")));
        final TableInfo _infoScreenshots = new TableInfo("screenshots", _columnsScreenshots, _foreignKeysScreenshots, _indicesScreenshots);
        final TableInfo _existingScreenshots = TableInfo.read(db, "screenshots");
        if (!_infoScreenshots.equals(_existingScreenshots)) {
          return new RoomOpenHelper.ValidationResult(false, "screenshots(com.snapsort.app.data.model.ScreenshotEntity).\n"
                  + " Expected:\n" + _infoScreenshots + "\n"
                  + " Found:\n" + _existingScreenshots);
        }
        final HashMap<String, TableInfo.Column> _columnsScreenshotContents = new HashMap<String, TableInfo.Column>(4);
        _columnsScreenshotContents.put("screenshot_id", new TableInfo.Column("screenshot_id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshotContents.put("raw_text", new TableInfo.Column("raw_text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshotContents.put("blocks_json", new TableInfo.Column("blocks_json", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsScreenshotContents.put("indexed_at", new TableInfo.Column("indexed_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysScreenshotContents = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysScreenshotContents.add(new TableInfo.ForeignKey("screenshots", "CASCADE", "NO ACTION", Arrays.asList("screenshot_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesScreenshotContents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoScreenshotContents = new TableInfo("screenshot_contents", _columnsScreenshotContents, _foreignKeysScreenshotContents, _indicesScreenshotContents);
        final TableInfo _existingScreenshotContents = TableInfo.read(db, "screenshot_contents");
        if (!_infoScreenshotContents.equals(_existingScreenshotContents)) {
          return new RoomOpenHelper.ValidationResult(false, "screenshot_contents(com.snapsort.app.data.model.ScreenshotContentEntity).\n"
                  + " Expected:\n" + _infoScreenshotContents + "\n"
                  + " Found:\n" + _existingScreenshotContents);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "913e1a0a6b49f804cfbe5a6ab49fd63b", "299151846e65f648b93323d432e62fb5");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "collections","screenshots","screenshot_contents");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `collections`");
      _db.execSQL("DELETE FROM `screenshots`");
      _db.execSQL("DELETE FROM `screenshot_contents`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CollectionDao.class, CollectionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ScreenshotDao.class, ScreenshotDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public CollectionDao collectionDao() {
    if (_collectionDao != null) {
      return _collectionDao;
    } else {
      synchronized(this) {
        if(_collectionDao == null) {
          _collectionDao = new CollectionDao_Impl(this);
        }
        return _collectionDao;
      }
    }
  }

  @Override
  public ScreenshotDao screenshotDao() {
    if (_screenshotDao != null) {
      return _screenshotDao;
    } else {
      synchronized(this) {
        if(_screenshotDao == null) {
          _screenshotDao = new ScreenshotDao_Impl(this);
        }
        return _screenshotDao;
      }
    }
  }
}
