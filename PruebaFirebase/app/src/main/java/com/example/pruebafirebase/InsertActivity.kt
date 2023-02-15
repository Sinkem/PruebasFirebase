package com.example.pruebafirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pruebafirebase.databinding.ActivityInsertBinding

class InsertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}