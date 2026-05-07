package es.mundodolphins.app.di

import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.mundodolphins.app.client.ArticlesService
import es.mundodolphins.app.client.FeedService
import es.mundodolphins.app.client.HistoricalService
import es.mundodolphins.app.client.SocialService
import es.mundodolphins.app.client.VideosService
import es.mundodolphins.app.data.AppDatabase
import es.mundodolphins.app.data.InstantConverter
import es.mundodolphins.app.data.episodes.EpisodeDao
import es.mundodolphins.app.models.SocialPostResponseAdapterFactory
import es.mundodolphins.app.viewmodel.player.PlayerServiceHelper
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://mundodolphins.es/api/"
    private const val HISTORICAL_CACHE_MAX_AGE_SECONDS = 7 * 24 * 60 * 60
    private const val HISTORICAL_CACHE_SIZE_BYTES = 10L * 1024L * 1024L

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val gson =
            GsonBuilder()
                .registerTypeAdapterFactory(SocialPostResponseAdapterFactory())
                .create()
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("historical")
    fun provideHistoricalRetrofit(
        @ApplicationContext context: Context,
    ): Retrofit {
        val cache =
            Cache(
                directory = File(context.cacheDir, "historical_http_cache"),
                maxSize = HISTORICAL_CACHE_SIZE_BYTES,
            )
        val client =
            OkHttpClient
                .Builder()
                .cache(cache)
                .addNetworkInterceptor { chain ->
                    chain
                        .proceed(chain.request())
                        .newBuilder()
                        .header("Cache-Control", "public, max-age=$HISTORICAL_CACHE_MAX_AGE_SECONDS")
                        .build()
                }.build()

        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFeedService(retrofit: Retrofit): FeedService = retrofit.create(FeedService::class.java)

    @Provides
    @Singleton
    fun provideArticlesService(retrofit: Retrofit): ArticlesService = retrofit.create(ArticlesService::class.java)

    @Provides
    @Singleton
    fun provideVideosService(retrofit: Retrofit): VideosService = retrofit.create(VideosService::class.java)

    @Provides
    @Singleton
    fun provideSocialService(retrofit: Retrofit): SocialService = retrofit.create(SocialService::class.java)

    @Provides
    @Singleton
    fun provideHistoricalService(
        @Named("historical") retrofit: Retrofit,
    ): HistoricalService = retrofit.create(HistoricalService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "mundodolphins_database",
            ).addTypeConverter(InstantConverter())
            .build()

    @Provides
    fun provideEpisodeDao(db: AppDatabase): EpisodeDao = db.episodeDao()

    @Provides
    @Singleton
    fun providePlayerServiceHelper(): PlayerServiceHelper = PlayerServiceHelper(PlayerServiceHelper.IntentBuilder())
}
