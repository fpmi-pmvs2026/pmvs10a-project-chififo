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
}