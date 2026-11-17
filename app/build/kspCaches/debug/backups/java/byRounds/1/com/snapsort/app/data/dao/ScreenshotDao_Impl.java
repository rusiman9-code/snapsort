package com.snapsort.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.snapsort.app.data.model.ScreenshotContentEntity;
import com.snapsort.app.data.model.ScreenshotEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ScreenshotDao_Impl implements ScreenshotDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScreenshotEntity> __insertionAdapterOfScreenshotEntity;

  private final EntityInsertionAdapter<ScreenshotContentEntity> __insertionAdapterOfScreenshotContentEntity;

  private final EntityDeletionOrUpdateAdapter<ScreenshotEntity> __updateAdapterOfScreenshotEntity;

  public ScreenshotDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScreenshotEntity = new EntityInsertionAdapter<ScreenshotEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `screenshots` (`id`,`file_uri`,`file_name`,`file_size`,`width`,`height`,`taken_at`,`collection_id`,`ocr_status`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScreenshotEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getFileUri());
        statement.bindString(3, entity.getFileName());
        statement.bindLong(4, entity.getFileSize());
        statement.bindLong(5, entity.getWidth());
        statement.bindLong(6, entity.getHeight());
        statement.bindLong(7, entity.getTakenAt());
        statement.bindString(8, entity.getCollectionId());
        statement.bindString(9, entity.getOcrStatus());
      }
    };
    this.__insertionAdapterOfScreenshotContentEntity = new EntityInsertionAdapter<ScreenshotContentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `screenshot_contents` (`screenshot_id`,`raw_text`,`blocks_json`,`indexed_at`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScreenshotContentEntity entity) {
        statement.bindString(1, entity.getScreenshotId());
        statement.bindString(2, entity.getRawText());
        statement.bindString(3, entity.getBlocksJson());
        statement.bindLong(4, entity.getIndexedAt());
      }
    };
    this.__updateAdapterOfScreenshotEntity = new EntityDeletionOrUpdateAdapter<ScreenshotEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `screenshots` SET `id` = ?,`file_uri` = ?,`file_name` = ?,`file_size` = ?,`width` = ?,`height` = ?,`taken_at` = ?,`collection_id` = ?,`ocr_status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScreenshotEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getFileUri());
        statement.bindString(3, entity.getFileName());
        statement.bindLong(4, entity.getFileSize());
        statement.bindLong(5, entity.getWidth());
        statement.bindLong(6, entity.getHeight());
        statement.bindLong(7, entity.getTakenAt());
        statement.bindString(8, entity.getCollectionId());
        statement.bindString(9, entity.getOcrStatus());
        statement.bindString(10, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final ScreenshotEntity screenshot,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfScreenshotEntity.insertAndReturnId(screenshot);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertContent(final ScreenshotContentEntity content,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfScreenshotContentEntity.insert(content);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ScreenshotEntity screenshot,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfScreenshotEntity.handle(screenshot);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScreenshotEntity>> getAllScreenshots() {
    final String _sql = "SELECT * FROM screenshots ORDER BY taken_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"screenshots"}, new Callable<List<ScreenshotEntity>>() {
      @Override
      @NonNull
      public List<ScreenshotEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "file_uri");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "file_name");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfTakenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "taken_at");
          final int _cursorIndexOfCollectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "collection_id");
          final int _cursorIndexOfOcrStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "ocr_status");
          final List<ScreenshotEntity> _result = new ArrayList<ScreenshotEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScreenshotEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFileUri;
            _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final long _tmpTakenAt;
            _tmpTakenAt = _cursor.getLong(_cursorIndexOfTakenAt);
            final String _tmpCollectionId;
            _tmpCollectionId = _cursor.getString(_cursorIndexOfCollectionId);
            final String _tmpOcrStatus;
            _tmpOcrStatus = _cursor.getString(_cursorIndexOfOcrStatus);
            _item = new ScreenshotEntity(_tmpId,_tmpFileUri,_tmpFileName,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpTakenAt,_tmpCollectionId,_tmpOcrStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ScreenshotEntity>> getRecentScreenshots(final int limit) {
    final String _sql = "SELECT * FROM screenshots ORDER BY taken_at DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"screenshots"}, new Callable<List<ScreenshotEntity>>() {
      @Override
      @NonNull
      public List<ScreenshotEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "file_uri");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "file_name");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfTakenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "taken_at");
          final int _cursorIndexOfCollectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "collection_id");
          final int _cursorIndexOfOcrStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "ocr_status");
          final List<ScreenshotEntity> _result = new ArrayList<ScreenshotEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScreenshotEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFileUri;
            _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final long _tmpTakenAt;
            _tmpTakenAt = _cursor.getLong(_cursorIndexOfTakenAt);
            final String _tmpCollectionId;
            _tmpCollectionId = _cursor.getString(_cursorIndexOfCollectionId);
            final String _tmpOcrStatus;
            _tmpOcrStatus = _cursor.getString(_cursorIndexOfOcrStatus);
            _item = new ScreenshotEntity(_tmpId,_tmpFileUri,_tmpFileName,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpTakenAt,_tmpCollectionId,_tmpOcrStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ScreenshotEntity>> getScreenshotsByCollection(final String collectionId) {
    final String _sql = "SELECT * FROM screenshots WHERE collection_id = ? ORDER BY taken_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, collectionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"screenshots"}, new Callable<List<ScreenshotEntity>>() {
      @Override
      @NonNull
      public List<ScreenshotEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "file_uri");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "file_name");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfTakenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "taken_at");
          final int _cursorIndexOfCollectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "collection_id");
          final int _cursorIndexOfOcrStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "ocr_status");
          final List<ScreenshotEntity> _result = new ArrayList<ScreenshotEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScreenshotEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFileUri;
            _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final long _tmpTakenAt;
            _tmpTakenAt = _cursor.getLong(_cursorIndexOfTakenAt);
            final String _tmpCollectionId;
            _tmpCollectionId = _cursor.getString(_cursorIndexOfCollectionId);
            final String _tmpOcrStatus;
            _tmpOcrStatus = _cursor.getString(_cursorIndexOfOcrStatus);
            _item = new ScreenshotEntity(_tmpId,_tmpFileUri,_tmpFileName,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpTakenAt,_tmpCollectionId,_tmpOcrStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getScreenshotById(final String id,
      final Continuation<? super ScreenshotEntity> $completion) {
    final String _sql = "SELECT * FROM screenshots WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ScreenshotEntity>() {
      @Override
      @Nullable
      public ScreenshotEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "file_uri");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "file_name");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfTakenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "taken_at");
          final int _cursorIndexOfCollectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "collection_id");
          final int _cursorIndexOfOcrStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "ocr_status");
          final ScreenshotEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFileUri;
            _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final long _tmpTakenAt;
            _tmpTakenAt = _cursor.getLong(_cursorIndexOfTakenAt);
            final String _tmpCollectionId;
            _tmpCollectionId = _cursor.getString(_cursorIndexOfCollectionId);
            final String _tmpOcrStatus;
            _tmpOcrStatus = _cursor.getString(_cursorIndexOfOcrStatus);
            _result = new ScreenshotEntity(_tmpId,_tmpFileUri,_tmpFileName,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpTakenAt,_tmpCollectionId,_tmpOcrStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScreenshotEntity>> searchByText(final String query) {
    final String _sql = "\n"
            + "        SELECT s.* FROM screenshots s\n"
            + "        INNER JOIN screenshot_contents sc ON s.id = sc.screenshot_id\n"
            + "        WHERE sc.raw_text LIKE '%' || ? || '%'\n"
            + "        ORDER BY s.taken_at DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"screenshots",
        "screenshot_contents"}, new Callable<List<ScreenshotEntity>>() {
      @Override
      @NonNull
      public List<ScreenshotEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFileUri = CursorUtil.getColumnIndexOrThrow(_cursor, "file_uri");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "file_name");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "file_size");
          final int _cursorIndexOfWidth = CursorUtil.getColumnIndexOrThrow(_cursor, "width");
          final int _cursorIndexOfHeight = CursorUtil.getColumnIndexOrThrow(_cursor, "height");
          final int _cursorIndexOfTakenAt = CursorUtil.getColumnIndexOrThrow(_cursor, "taken_at");
          final int _cursorIndexOfCollectionId = CursorUtil.getColumnIndexOrThrow(_cursor, "collection_id");
          final int _cursorIndexOfOcrStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "ocr_status");
          final List<ScreenshotEntity> _result = new ArrayList<ScreenshotEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScreenshotEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpFileUri;
            _tmpFileUri = _cursor.getString(_cursorIndexOfFileUri);
            final String _tmpFileName;
            _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final int _tmpWidth;
            _tmpWidth = _cursor.getInt(_cursorIndexOfWidth);
            final int _tmpHeight;
            _tmpHeight = _cursor.getInt(_cursorIndexOfHeight);
            final long _tmpTakenAt;
            _tmpTakenAt = _cursor.getLong(_cursorIndexOfTakenAt);
            final String _tmpCollectionId;
            _tmpCollectionId = _cursor.getString(_cursorIndexOfCollectionId);
            final String _tmpOcrStatus;
            _tmpOcrStatus = _cursor.getString(_cursorIndexOfOcrStatus);
            _item = new ScreenshotEntity(_tmpId,_tmpFileUri,_tmpFileName,_tmpFileSize,_tmpWidth,_tmpHeight,_tmpTakenAt,_tmpCollectionId,_tmpOcrStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getContent(final String screenshotId,
      final Continuation<? super ScreenshotContentEntity> $completion) {
    final String _sql = "SELECT * FROM screenshot_contents WHERE screenshot_id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, screenshotId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ScreenshotContentEntity>() {
      @Override
      @Nullable
      public ScreenshotContentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfScreenshotId = CursorUtil.getColumnIndexOrThrow(_cursor, "screenshot_id");
          final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "raw_text");
          final int _cursorIndexOfBlocksJson = CursorUtil.getColumnIndexOrThrow(_cursor, "blocks_json");
          final int _cursorIndexOfIndexedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "indexed_at");
          final ScreenshotContentEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpScreenshotId;
            _tmpScreenshotId = _cursor.getString(_cursorIndexOfScreenshotId);
            final String _tmpRawText;
            _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
            final String _tmpBlocksJson;
            _tmpBlocksJson = _cursor.getString(_cursorIndexOfBlocksJson);
            final long _tmpIndexedAt;
            _tmpIndexedAt = _cursor.getLong(_cursorIndexOfIndexedAt);
            _result = new ScreenshotContentEntity(_tmpScreenshotId,_tmpRawText,_tmpBlocksJson,_tmpIndexedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object moveToCollection(final List<String> ids, final String targetCollectionId,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("UPDATE screenshots SET collection_id = ");
        _stringBuilder.append("?");
        _stringBuilder.append(" WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        _stmt.bindString(_argIndex, targetCollectionId);
        _argIndex = 2;
        for (String _item : ids) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          final Integer _result = _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByIds(final List<String> ids,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM screenshots WHERE id IN (");
        final int _inputSize = ids.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : ids) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          final Integer _result = _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
