package xyz.teamgravity.claudecodetododemo.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.teamgravity.claudecodetododemo.data.AppDatabase
import xyz.teamgravity.claudecodetododemo.data.TodoDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideTodoDao(database: AppDatabase): TodoDao {
        return database.todoDao()
    }
}
