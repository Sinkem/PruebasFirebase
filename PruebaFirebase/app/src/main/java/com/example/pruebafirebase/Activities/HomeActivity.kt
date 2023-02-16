package com.example.pruebafirebase.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebafirebase.Adapters.ChatAdapter
import com.example.pruebafirebase.Models.Chat
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
    private lateinit var usuario:String;

    private lateinit var googleSignInClient: GoogleSignInClient

    lateinit var binding: ActivityHomeBinding

    lateinit var auth: FirebaseAuth

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle:Bundle? = intent.extras
        usuario = bundle?.getString("email")!!
        setUp(usuario ?: "")

        auth = FirebaseAuth.getInstance()

        val prefs = getSharedPreferences("com.example.pruebafirebase.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE).edit()
        prefs.putString("email", usuario)
        prefs.apply()
    }

    private fun setUp(email: String) {
        title = "Inicio"

        binding.tvEmail.text = email

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

        binding.newChatButton.setOnClickListener { newChat() }

        binding.listChatsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.listChatsRecyclerView.adapter =
            ChatAdapter{chat ->
                chatSelected(chat)
            }

        val userRef = db.collection("users").document(usuario)

        userRef.collection("chats")
            .get()
            .addOnSuccessListener { chats ->
                val listChats = chats.toObjects(Chat::class.java)

                (binding.listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
            }

        userRef.collection("chats")
            .addSnapshotListener { chats, error ->
                if(error == null){
                    chats?.let {
                        val listChats = it.toObjects(Chat::class.java)

                        (binding.listChatsRecyclerView.adapter as ChatAdapter).setData(listChats)
                    }
                }
            }

    }

    private fun chatSelected(chat:Chat){
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId",chat.id)
        intent.putExtra("participantes",usuario)
        startActivity(intent)
    }

    private fun newChat(){
        val chatId = UUID.randomUUID().toString()
        val otherUser = binding.newChatText.text.toString()
        val users = listOf(usuario, otherUser)

        val chat = Chat(
            id = chatId,
            nombre = "Chat con $otherUser",
            partcipantes = users
        )

        db.collection("chats").document(chatId).set(chat)
        db.collection("users").document(usuario).collection("chats").document(chatId).set(chat)
        db.collection("users").document(otherUser).collection("chats").document(chatId).set(chat)

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chatId)
        intent.putExtra("user", usuario)
        startActivity(intent)

    }
}