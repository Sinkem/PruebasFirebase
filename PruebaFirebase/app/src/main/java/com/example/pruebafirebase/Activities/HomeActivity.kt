package com.example.pruebafirebase.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebafirebase.Adapters.ChatAdapter
import com.example.pruebafirebase.Models.Chat
import com.example.pruebafirebase.Models.Usuario
import com.example.pruebafirebase.R
import com.example.pruebafirebase.databinding.ActivityHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*



class HomeActivity : AppCompatActivity() {
    //Aquí creamos las variables globales que usaremos a lo largo de esta actividad
    //Estas dos serán para saber que chat es en el que se ha metido el usuario y que usuario es
    private lateinit var usuario:String;

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var listChats: kotlin.collections.List<Chat>

    lateinit var binding: ActivityHomeBinding

    lateinit var auth: FirebaseAuth

    //Variable que nos permite usar nuestra base de datos en firestore
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Obtenemos el email que nos manda la actividad anterior
        val bundle:Bundle? = intent.extras
        usuario = bundle?.getString("email")!!
        mostrarListaChats(usuario ?: "")

        auth = FirebaseAuth.getInstance()

        val prefs = getSharedPreferences("com.example.pruebafirebase.PREFERENCE_FILE_KEY", MODE_PRIVATE).edit()
        prefs.putString("email", usuario)
        prefs.apply()
    }

    /**
     * Método donde mostraremos el listado de chats que tiene el usuario. Para ello utilizaremos
     * el ChatAdapter que irá bindeado al item mensaje.
     */
    private fun mostrarListaChats(email: String) {
        title = "Inicio"

        binding.tvEmail.text = email

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.btnLogOut.setOnClickListener {
            val prefs = getSharedPreferences("com.example.pruebafirebase.PREFERENCE_FILE_KEY", MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            auth.signOut()
            googleSignInClient.signOut()
            onBackPressed()
        }

        binding.newChatButton.setOnClickListener { crearNuevoChat() }

        binding.listChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.listChatsRecyclerView.adapter =
            ChatAdapter{chat ->
                chatSelected(chat)
            }

        val userRef = db.collection("usuarios").document(usuario)

        userRef.collection("chats")
            .get()
            /*Añadimos un addOnSuccessListener para en caso de que se ejecute correctamente la
            query, el listado de chats se bindeará a la vista y será visible para el usuario*/
            .addOnSuccessListener { chats ->
                val listChats = chats.toObjects(Chat::class.java)
                (binding.listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
            }

        userRef.collection("chats")
            /* Añadimos un SnapshotListener  para detectar a tiempo real de las actualizaciones de
            la base de datos, ya que registra el estado del documento cuando se ejecuta y cada vez
            que detecta un cambio en el documento lo vuelve a actualizar instantaneamente*/
            .addSnapshotListener { chats, error ->
                if(error == null){
                    chats?.let {
                        listChats = it.toObjects(Chat::class.java)
                        (binding.listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
                    }
                }
            }
    }

    /**
     * Método que se ejecutará cada vez que ejecutemos un chat. Mandaremos al usuario a la siguiente
     * actividad, además del id del chat que ha sido seleccionado y el nombre del usuario.
     * Precondición: ninguna
     * Postcondición: ninguna
     */
    private fun chatSelected(chat:Chat){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("id",chat.id)
        intent.putExtra("usuario",usuario)
        startActivity(intent)
    }

    /**
     * Creamos un nuevo chat con el usuario que ha seleccionado el usuario. Crearemos una nueva
     * clase chat con un id, y el nombre tanto del usuario actual como del que este ha introducido
     * para formar el chat. Esta clase chat será introducida en la base de datos para su posterior
     * uso. Por último, mandaremos al usuario al nuevo chat que acaba de crear.
     * Precondición: ninguna
     * Postcondicion: ninguna
     */
    private fun crearNuevoChat(){
        val otherUser = binding.newChatText.text.toString()
        val userRef = db.collection("usuarios").document(otherUser)
        var listaUsuarios = emptyList<Usuario>()
      /*  userRef.get().addOnSuccessListener { usuarios ->
            listaUsuarios = usuarios.toObjects(Usuario::class.java)
        }*/
        val otherUserExists = listChats.find { it.participantes.contains(otherUser) }
        if (!usuario.equals(otherUser) && otherUserExists == null){
            if (userRef.equals(null)){
                val chatId = UUID.randomUUID().toString()
                val users = listOf(usuario, otherUser)

                val chat = Chat(
                    id = chatId,
                    nombre = "Chat con $otherUser",
                    participantes = users
                )

                db.collection("chats").document(chatId).set(chat)
                db.collection("usuarios").document(usuario).collection("chats").document(chatId).set(chat)
                db.collection("usuarios").document(otherUser).collection("chats").document(chatId).set(chat)

            } else {
                Toast.makeText(this, "Error, el usuario no esta registrado", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error, usuario no valido", Toast.LENGTH_SHORT).show()

        }

/*
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chatId)
        startActivity(intent)
        intent.putExtra("usuarios", usuario)
        */
    }
}