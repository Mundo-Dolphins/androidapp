package es.mundodolphins.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.mundodolphins.app.client.ArticlesService
import es.mundodolphins.app.client.FeedService
import es.mundodolphins.app.client.SocialService
import es.mundodolphins.app.client.VideosService
import es.mundodolphins.app.data.AppDatabase
import es.mundodolphins.app.data.InstantConverter
import es.mundodolphins.app.data.episodes.EpisodeDao
import es.mundodolphins.app.viewmodel.player.PlayerServiceHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://mundodolphins.es/api/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

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
    fun provideInstantConverter(): InstantConverter = InstantConverter()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        instantConverter: InstantConverter,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "mundodolphins_database",
            ).addTypeConverter(instantConverter)
            .build()

    @Provides
    fun provideEpisodeDao(db: AppDatabase): EpisodeDao = db.episodeDao()

    @Provides
    @Singleton
    fun providePlayerServiceHelper(): PlayerServiceHelper =
        PlayerServiceHelper(PlayerServiceHelper.IntentBuilder())
}
