package common.hoangdz.admob.di.module

import android.content.Context
import common.hoangdz.admob.config.shared.AdShared
import common.hoangdz.admob.config.shared.AdShared_Impl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GlobalModule {

    @Provides
    @Singleton
    fun provideAdShared(@ApplicationContext context: Context): AdShared = AdShared_Impl(context)

}