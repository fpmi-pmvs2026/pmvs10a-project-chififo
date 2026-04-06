package com.example.musicapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "music_app.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Создаем простую таблицу с треками
        val createTableQuery = "CREATE TABLE tracks (id INTEGER PRIMARY KEY AUTOINCREMENT, artist TEXT, title TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tracks")
        onCreate(db)
    }

    // Простая функция для сохранения трека
    fun addTrack(artist: String, title: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("artist", artist)
            put("title", title)
        }
        db.insert("tracks", null, values)
        db.close()
    }

    fun getAllTracks(): List<Pair<Int, String>> {
        val list = mutableListOf<Pair<Int, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, artist, title FROM tracks", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val artist = cursor.getString(1)
                val title = cursor.getString(2)
                list.add(Pair(id, "$artist — $title"))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    // Удалить трек по ID
    fun deleteTrack(id: Int) {
        val db = this.writableDatabase
        db.delete("tracks", "id=?", arrayOf(id.toString()))
        db.close()
    }
}