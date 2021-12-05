package net.uku3lig.uklient.converters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.uku3lig.uklient.Downloader;

import java.lang.reflect.Type;
import java.net.URL;

public class URLDeserializer implements JsonDeserializer<URL> {
    @Override
    public URL deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Downloader.url(json.getAsString());
    }
}
