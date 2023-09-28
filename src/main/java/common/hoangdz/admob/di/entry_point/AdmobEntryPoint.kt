package common.hoangdz.admob.di.entry_point

import common.hoangdz.admob.config.remote.AdRemoteConfig
import common.hoangdz.admob.config.shared.AdShared
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AdmobEntryPoint {
    fun adRemoteConfig(): AdRemoteConfig

    fun adsShared():AdShared
}