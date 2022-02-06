package net.uku3lig.uklient.util.converters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.uku3lig.uklient.util.Util;

import java.lang.reflect.Type;
import java.net.URL;

public class URLDeserializer implements JsonDeserializer<URL> {
    @Override
    public URL deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String encoded = json.getAsString().replaceAll("\\s", "%20");
        return Util.url(encoded);
    }
}
