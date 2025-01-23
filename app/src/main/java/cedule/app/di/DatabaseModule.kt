package cedule.app.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import cedule.app.data.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return databaseBuilder(context, Database::class.java, "app")
            .createFromAsset("app.db")
            .build();
    }
}