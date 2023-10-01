package ua.polytech.testingtask.db.di

import android.content.Context
import androidx.room.Room
import ua.polytech.testingtask.db.RoomDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.polytech.testingtask.db.LibraryRoomDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provideLibraryDatabase(@ApplicationContext appContext: Context): LibraryRoomDatabase {
        return Room.databaseBuilder(
            appContext,
            LibraryRoomDatabase::class.java,
            LibraryRoomDatabase.LIBRARY_DB,
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideLibraryDao(libraryDatabase: LibraryRoomDatabase): RoomDao {
        return libraryDatabase.libraryDao()
    }


}