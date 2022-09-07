package com.example.loginklotin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.loginklotin.databinding.ActivityMainBinding
import com.example.loginklotin.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SingUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingUpBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()



        binding.textView.setOnClickListener {
            val intent = Intent(this, SingInActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val nickname = binding.nickNameEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {




                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            val user = firebaseAuth.currentUser
                            val uid = user!!.uid

                            val map = hashMapOf(
                                "NickName" to nickname,
                                "Email" to email,

                            )

                            val db = Firebase.firestore

                            db.collection("user").document(uid).set(map).addOnSuccessListener {
                                Toast.makeText(this, "Usuario Registrado", Toast.LENGTH_SHORT).show()
                            }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Fallo al guardar la informacion",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            val intent = Intent(this, SingInActivity::class.java)
                            startActivity(intent)

                        }else if(pass.length < 6)
                        {
                            Toast.makeText(this, "La  contraseña minimas es de 6 Caracteres", Toast.LENGTH_SHORT).show()

                        }else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Llene todos los datos", Toast.LENGTH_SHORT).show()

            }
        }

    }
}