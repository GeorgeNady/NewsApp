package com.george.mvvmnewsapp.dp

import androidx.room.TypeConverter
import com.george.mvvmnewsapp.models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String) : Source {
        return Source(name,name)
    }

}