package com.nikhilhere.test.consumer.di

import android.content.Context
import android.location.Geocoder
import androidx.core.content.res.TypedArrayUtils.getString
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.nikhilhere.test.consumer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
class LocationModule {


    @ActivityScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext app: Context) =
        FusedLocationProviderClient(app)

    @ActivityScoped
    @Provides
    fun provideGeoCoder(@ApplicationContext app : Context) = Geocoder(app, Locale.getDefault())


    @ActivityScoped
    @Provides
    fun providePlacesClient(@ApplicationContext app : Context) : PlacesClient {
        Places.initialize(app,app.getString(R.string.google_maps_key))
        return Places.createClient(app)
    }
}