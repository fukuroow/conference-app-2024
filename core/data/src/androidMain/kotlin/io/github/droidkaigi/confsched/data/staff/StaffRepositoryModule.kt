package io.github.droidkaigi.confsched.data.staff

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.droidkaigi.confsched.model.StaffRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public class StaffRepositoryModule {

    @Provides
    @Singleton
    public fun provideStaffRepository(
        staffApi: StaffApiClient,
    ): StaffRepository {
        return DefaultStaffRepository(
            staffApi = staffApi,
        )
    }
}
