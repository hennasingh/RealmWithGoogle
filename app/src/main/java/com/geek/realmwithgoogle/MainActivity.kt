package com.geek.realmwithgoogle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.geek.realmwithgoogle.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import io.realm.mongodb.Credentials

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var client: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(getString(R.string.server_client_id))
            .build()

        client = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.signInButton.setOnClickListener{
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let{
            handleSignInResult(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            handleSignInResult(account)
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {
            try{
                Log.d("MainActivity", "${account?.serverAuthCode}")
                //1
                val idToken = account?.serverAuthCode

                //signed in successfully, forward credentials to MongoDB realm
                //2
                val googleCredentials = Credentials.google(idToken)
                //3
                app.loginAsync(googleCredentials){
                    if(it.isSuccess){
                        Log.d("MainActivity", "Successfully authenticated using Google OAuth")
                        //4
                        startActivity(Intent(this, SampleResult::class.java))
                    } else {
                        Log.d("MainActivity", "Failed to Log in to MongoDB Realm: ${it.error.errorMessage}")
                    }
                }
            } catch(exception: ApiException){
                Log.d("MainActivity",  exception.printStackTrace().toString())
            }
    }

    private fun signIn() {
        val signIntent = client.signInIntent
        startActivityForResult(signIntent, 100)
    }
}