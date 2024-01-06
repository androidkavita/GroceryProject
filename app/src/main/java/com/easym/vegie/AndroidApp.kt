package com.easym.vegie

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.easym.vegie.di.AppComponent
import com.easym.vegie.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject




class AndroidApp: Application(), HasActivityInjector, HasSupportFragmentInjector {




    companion object{
        @JvmStatic lateinit var appComponent: AppComponent
        var isAppOpen=false


        fun isAppOpenFirstTime():Boolean
        {
            return isAppOpen
        }

        fun setIsAppOpenFirstTime()
        {
            isAppOpen=true
        }
    }



    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>


    @Inject lateinit var  fragmentInjector: DispatchingAndroidInjector<Fragment>



    override fun onCreate() {
        super.onCreate()

        //FirebaseApp.initializeApp(this)
        AppInjector.init(this)
        //FacebookSdk.sdkInitialize(this)
        //Fresco.initialize(this)
        //init mapmyindia

        //init mapmyindia
       /* MapmyIndiaAccountManager.getInstance().restAPIKey = getRestAPIKey()
        MapmyIndiaAccountManager.getInstance().mapSDKKey = getMapSDKKey()
        MapmyIndiaAccountManager.getInstance().atlasGrantType = getAtlasGrantType()
        MapmyIndiaAccountManager.getInstance().atlasClientId = getAtlasClientId()
        MapmyIndiaAccountManager.getInstance().atlasClientSecret = getAtlasClientSecret()
        MapmyIndia.getInstance(applicationContext)*/


    }


    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentInjector
    }


    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }

    fun getAtlasClientId(): String? {
        return "33OkryzDZsIYBl-D-iZLOZIEOfde92jUEsbKzS3hXzUw4kabeEj-65b1vMvTasR09ct7gTnkUwD2abHUeF_9cE0Ldro4VDUAa15A1RneQC-krvEZ25L8dw=="
    }

    fun getAtlasClientSecret(): String? {
        return "lrFxI-iSEg_egVz2vEZdT1rZJQ8022QzITl1xKKpue5TTqlDinugRAzxRNQpZYIHl0zy5E2sq4Y-hlwv4dc49RLw0YIJqicAU0eUZ9cC4NwmHnHyiUD1XPS4vH-AyefT"
    }


    fun getAtlasGrantType(): String? {
        return "client_credentials"
    }

    fun getMapSDKKey(): String? {
        return getString(R.string.map_my_india_sdk_key)
    }

    fun getRestAPIKey(): String? {
        return getString(R.string.map_my_india_rest_api_key)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isAppOpen=false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {

    }




}