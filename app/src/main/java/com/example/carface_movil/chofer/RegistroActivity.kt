package com.example.carface_movil.chofer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.carface_movil.R

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        if (savedInstanceState == null) {
            val fragment = fragment_primer_paso()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}