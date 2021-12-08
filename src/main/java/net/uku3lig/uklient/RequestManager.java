package net.uku3lig.uklient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.uku3lig.uklient.converters.InstantDeserializer;
import net.uku3lig.uklient.converters.URLDeserializer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import java.lang.reflect.Type;
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
                .registerTypeAdapter(TypeToken.get(Instant.class).getType(), new InstantDeserializer())
                .registerTypeAdapter(TypeToken.get(URL.class).getType(), new URLDeserializer())
        ;

        gson = builder.create();
        return gson;
    }

    public static Type getParametrized(Class<?> main, Class<?> parameter) {
        Type mainType = TypeToken.get(main).getType();
        Type parameterType = TypeToken.get(parameter).getType();
        return TypeToken.getParameterized(mainType, parameterType).getType();
    }

    private RequestManager() {}
}
