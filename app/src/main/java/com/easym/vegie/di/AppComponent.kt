package com.easym.vegie.di

import android.app.Application
import com.easym.vegie.AndroidApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules =  [AndroidInjectionModule::class, AppModule::class, BuildersModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: AndroidApp)


}


