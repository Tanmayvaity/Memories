package com.example.memories.core.data.data_source.room.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.memories.core.domain.model.Type


class MediaTypeConverter {

    @TypeConverter
    fun fromType(type : Type) : String{
        return type.mimeType
    }

    @TypeConverter
    fun toType(mimeType : String): Type {
        return Type.fromMimeType(mimeType)
    }
}