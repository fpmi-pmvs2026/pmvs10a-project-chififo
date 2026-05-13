package com.example.musicapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "music_app.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE tracks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                artist TEXT NOT NULL,
                title TEXT NOT NULL,
                artwork_url TEXT,
                audio_url TEXT,
                lyrics TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tracks")
        onCreate(db)
    }

    fun addTrack(track: Track) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("artist", track.artist)
            put("title", track.title)
            put("artwork_url", track.artworkUrl)
            put("audio_url", track.audioUrl)
            put("lyrics", track.lyrics)
        }
        db.insert("tracks", null, values)
        db.close()
    }

    fun getAllTracks(): List<Track> {
        val list = mutableListOf<Track>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, artist, title, artwork_url, audio_url, lyrics FROM tracks ORDER BY id DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Track(
                        id = cursor.getInt(0),
                        artist = cursor.getString(1),
                        title = cursor.getString(2),
                        artworkUrl = cursor.getString(3) ?: "",
                        audioUrl = cursor.getString(4) ?: "",
                        lyrics = cursor.getString(5) ?: ""
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }

    fun deleteTrack(id: Int) {
        val db = writableDatabase
        db.delete("tracks", "id=?", arrayOf(id.toString()))
        db.close()
    }
}