package com.example.pruebafirebase.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebafirebase.Adapters.MensajeAdapter
import com.example.pruebafirebase.Models.Mensaje
import com.example.pruebafirebase.R
import com.example.pruebafirebase.databinding.ActivityChatBinding
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatActivity : AppCompatActivity() {
    //Aquí creamos las variables globales que usaremos a lo largo de esta actividad
    //Estas dos serán para saber que chat es en el que se ha metido el usuario y que usuario es
    private var chatId = ""
    private var usuario = ""

    //Variable que nos permite usar nuestra base de datos en firestore
    private var db = Firebase.firestore

    //Binding con el que podremos vincular los controles de nuestra vista
    lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //Vinculamos la vista chat
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*Obtenemos los dos string que nos manda la actividad Home, los cuales son el id del chat
        * y el usuario que se ha introducido en dicho chat*/
        intent.getStringExtra("id")?.let { chatId = it }
        intent.getStringExtra("usuario")?.let { usuario = it }

        //En caso de que el el id del chat y el usuario no sean nulos inicializaremos las vistas
        if(chatId.isNotEmpty() && usuario.isNotEmpty()) {
            mostrarMensajesChat()
        }
    }

    /**
     * Método donde mostraremos los mensajes del respectivo chat. Para ello utilizaremos el
     * MensajeAdapter que irá bindeado al item chat. Además haremos que el listado de mensajes
     * se vaya actualizando según se envíen los mensajes, para que así sea a tiempo real.
     * Precondición: ninguna
     * Postcondición: ninguna
     */
    private fun mostrarMensajesChat(){

        binding.messagesRecylerView.layoutManager = LinearLayoutManager(this)
        binding.messagesRecylerView.adapter = MensajeAdapter(usuario)

        binding.sendMessageButton.setOnClickListener { enviarMensaje() }

        //Documento que obtiene la información de la colección chats de la base de datos, segúin el id del chat
        val chatRef = db.collection("chats").document(chatId)

        chatRef.collection("mensajes").orderBy("fechaEnvio", Query.Direction.ASCENDING)
            .get()
            /*Añadimos un addOnSuccessListener para en caso de que se ejecute correctamente la
             query, el listado de mensajes se bindeará a la vista y será visible para el usuario*/
            .addOnSuccessListener { messages ->
                val listMessages = messages.toObjects(Mensaje::class.java)
                (binding.messagesRecylerView.adapter as MensajeAdapter).setData(listMessages)
            }


        chatRef.collection("mensajes").orderBy("fechaEnvio", Query.Direction.ASCENDING)
            /* Añadimos un SnapshotListener  para detectar a tiempo real de las actualizaciones de
            la base de datos, ya que registra el estado del documento cuando se ejecuta y cada vez
            que detecta un cambio en el documento lo vuelve a actualizar instantaneamente*/
            .addSnapshotListener { messages, error ->
                if(error == null){
                    messages?.let {
                        val listMessages = it.toObjects(Mensaje::class.java)
                        (binding.messagesRecylerView.adapter as MensajeAdapter).setData(listMessages)
                    }
                }
            }
    }

    /**
     * Enviamos el mensaje al chat. Instanciamos una variable mensaje con los respectivos datos de
     * usuario, contenido y fecha, la cual será lo que enviaremos a la base de datos.
     * Precondición: ninguna
     * Postcondición: ninguna
     */
    private fun enviarMensaje(){
        val message = Mensaje(
            mensaje = binding.messageTextField.text.toString(),
            emisor = usuario,
            fechaEnvio = Date()
        )
        db.collection("chats").document(chatId).collection("mensajes").document().set(message)
        /* Vaciamos el textView una vez enviado el mensaje, tal como cualquier otra aplicación de
         mensajería */
        binding.messageTextField.setText("")
    }
}