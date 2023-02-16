package com.example.pruebafirebase.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pruebafirebase.databinding.ActivityInsertBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}