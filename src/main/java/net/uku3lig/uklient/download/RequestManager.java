package net.uku3lig.uklient.download;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.uku3lig.uklient.util.converters.InstantConverter;
import net.uku3lig.uklient.util.converters.URLDeserializer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RequestManager {
    private static Gson gson;
    private static final Map<String, Retrofit> retrofits = new HashMap<>();

    public static Retrofit supplyRetrofit(String baseUrl) {
        if (retrofits.containsKey(baseUrl)) return retrofits.get(baseUrl);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JaxbConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .baseUrl(baseUrl)
                .build();

        retrofits.put(baseUrl, retrofit);
        return retrofit;
    }

    public static Gson getGson() {
        if (gson != null) return gson;

        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(TypeToken.get(Instant.class).getType(), new InstantConverter())
                .registerTypeAdapter(TypeToken.get(URL.class).getType(), new URLDeserializer())
        ;

        gson = builder.create();
        return gson;
    }

    private RequestManager() {}
}
