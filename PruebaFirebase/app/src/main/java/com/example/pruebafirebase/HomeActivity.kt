package com.example.pruebafirebase

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pruebafirebase.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var binding: ActivityHomeBinding

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle:Bundle? = intent.extras
        val email:String? = bundle?.getString("email")
        val provider:String? = bundle?.getString("provider")
        setUp(email ?: "", provider ?: "")

        auth = FirebaseAuth.getInstance()

        val prefs = getSharedPreferences("com.example.pruebafirebase.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setUp(email: String, provider: String) {
        title = "Inicio"

        binding.tvEmail.text = email
        binding.tvPassword.text = provider

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.btnLogOut.setOnClickListener {
            val prefs = getSharedPreferences("com.example.pruebafirebase.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            auth.signOut()
            googleSignInClient.signOut()
            onBackPressed()
        }

        binding.btnInsert.setOnClickListener() {

        }
    }
}