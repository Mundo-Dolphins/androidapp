package es.mundodolphins.app.models

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class SocialPostResponseAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(
        gson: Gson,
        type: TypeToken<T>,
    ): TypeAdapter<T>? {
        if (type.rawType != SocialPostResponse::class.java) return null

        @Suppress("UNCHECKED_CAST")
        val delegate = gson.getDelegateAdapter(this, type) as TypeAdapter<SocialPostResponse>

        @Suppress("UNCHECKED_CAST")
        return object : TypeAdapter<SocialPostResponse?>() {
            override fun write(
                out: JsonWriter,
                value: SocialPostResponse?,
            ) {
                delegate.write(out, value)
            }

            override fun read(`in`: JsonReader): SocialPostResponse? =
                if (`in`.peek() == JsonToken.BEGIN_OBJECT) {
                    delegate.read(`in`)
                } else {
                    `in`.skipValue()
                    null
                }
        } as TypeAdapter<T>
    }
}
