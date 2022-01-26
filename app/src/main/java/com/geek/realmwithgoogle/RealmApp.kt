package com.geek.realmwithgoogle

import android.app.Application
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration

const val appId ="realmsignin-abyof" //Enter your AppID here
lateinit var app: App

class RealmApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        app = App(AppConfiguration.Builder(appId).build())

    }
}