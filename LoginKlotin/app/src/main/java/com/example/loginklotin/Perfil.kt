package com.example.loginklotin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.loginklotin.databinding.ActivityMainBinding
import com.example.loginklotin.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Perfil : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        val user = firebaseAuth.currentUser
        val uid = user!!.uid

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        db.collection("user").document(uid).get()
            .addOnSuccessListener {
                binding.NickName.setText("NickName: "+it.get("NickName") as String?)
                binding.Correo.setText("Correo: "+it.get("Email") as String?)
            }

        binding.singout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SingInActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.Back.setOnClickListener {
            val intent : Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}