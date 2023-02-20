package com.example.pruebafirebase.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.pruebafirebase.R
import com.example.pruebafirebase.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {
    //Variables para autentificar y una para autentificar con Google
    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Intregracion realizada")
        analytics.logEvent("InitScreen", bundle)

        setUp()
        session()
    }

    /**
     * Funcion para iniciar sesion con Google una vez googleSignInCliente ha sido inicializada
     */
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent


        launcher.launch(signInIntent)
    }

    /**
     * Constante launcher inicializada segun el resultado del siguiente proceso
     */
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    /**
     * Funcion que comprobara si el intento de obtener la cuenta de Google del usuario ha sido correcto, llamara a otro metodo
     * para pasar de Activity. Si sale mal mostrara un toast.
     */
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        } else {
            Toast.makeText(this, "Error al intentar iniciar sesion", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Funcion que, una vez obtenidos todos los datos necesarios de Google, cogera los datos de la cuenta y navegara a
     * la siguiente Activity, llevando el email.
     */
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                showHome(it.result?.user?.email ?: "")
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Funcion para que el layout del Activity este visible si se ejecuta la Activity
     */
    override fun onStart() {
        super.onStart()

        binding.authLayout.visibility = View.VISIBLE
    }

    /**
     * Funcion para comprobar si ya hay una sesion iniciada de antes al iniciar la aplicacion.
     * En caso de que si, hara invisible el layout de la vista actual y navegara a la siguiente.
     */
    private fun session() {
        val prefs = getSharedPreferences(
            "com.example.pruebafirebase.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE
        )
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            binding.authLayout.visibility = View.INVISIBLE
            showHome(email)
        }
    }

    /**
     * Funcion para iniciar los distintos componentes de la aplicacion
     */
    private fun setUp() {
        title = "Autenthication"

        //Opciones de Google para su inicio de sesion
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //Creamos la variable para poder iniciar sesion con Google
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Al clicar en el boton de registrarse, comprobara si los campos estan llenos e intentara crear el usuario.
        //En caso de que se pueda realizar, se creara y, en caso contrario, se mostrara una alerta diciendo que no fue posible
        binding.btnSingUp.setOnClickListener {
            if (binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "")
                    } else {
                        Log.e("Firebase Auth", "Sign-in failed", it.exception);
                        showAlert()
                    }
                }
            }
        }


        //Igual que el anterior pero para iniciar sesion
        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "")
                    } else {
                        showAlert()
                    }
                }
            }
        }

        binding.btnGoogle.setOnClickListener{
            signInGoogle()
        }

/*
        binding.btnGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
        */
    }

    /**
     * Funcion para mostrar una alerta con un mensaje de error
     */
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error registrando/autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /**
     * Funcion para navegar a la Actiivity Home, pasando tambien el email del usuario
     */
    private fun showHome(email: String) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "")
                            } else {
                                Log.e("ErroGoogle", "Erro",it.exception)
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert()
            }

        }
    }*/
}