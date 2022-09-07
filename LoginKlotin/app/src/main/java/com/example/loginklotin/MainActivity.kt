package com.example.loginklotin

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginklotin.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var myadapter: Myadapter
    private lateinit var db: FirebaseFirestore
    private var Errores : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = Firebase.auth

        setContentView(binding.root)


        binding.AccesPerfil.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)
            finish()

        }

        binding.ObtUbica.setOnClickListener {
            Toast.makeText(this, "Encendio la Geolocalizacion",Toast.LENGTH_SHORT).show()
            Errores = 0
            leerubicacionactual()
            Repetir()

            if (allPermissionGrantedGPS() ){
                val intent = Intent(this, Service::class.java)
                startService(intent)
                Toast.makeText(this, "Se inicio la aplicacion en segundo plano", Toast.LENGTH_SHORT).show()
            } else {
                requestPermission()
            }

        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()
        myadapter = Myadapter(userArrayList)

        recyclerView.adapter = myadapter

        EventChangeListener()


        binding.Cancelar.setOnClickListener {

            val intent = Intent(this, Service::class.java)

            binding.lbllatitud.text = "Latitud: "
            binding.lbllongitud.text = "Longitud: "
            Errores = 5
            stopService(intent)
            Toast.makeText(this, "Se cancelor la localizacion en segundo plano", Toast.LENGTH_SHORT).show()
        }







    }
    private fun Repetir(){
        val myHandler = Handler(Looper.getMainLooper())

        myHandler.post(object : Runnable {
            override fun run() {
                leerubicacionactual()
                userArrayList.clear()
                EventChangeListener()
                myHandler.postDelayed(this, 5000 /*5 segundos*/)
            }
        })

        if(Errores == 5) {
            myHandler.removeCallbacksAndMessages(null);
            Toast.makeText(this, "Se cancelo la geolozalizacion",Toast.LENGTH_SHORT).show()
        }

    }


    private fun EventChangeListener() {

        db = FirebaseFirestore.getInstance()
        db.collection("coord").addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null)
                {
                    Log.e("Firestore Error" , error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!)
                {
                    if(dc.type == DocumentChange.Type.ADDED)
                    {
                        userArrayList.add(dc.document.toObject(User::class.java))
                    }
                }
                myadapter.notifyDataSetChanged()
            }
        })


    }


    private fun leerubicacionactual() {


                if (allPermissionGrantedGPS() ){
                    getLocations()
                } else {
                    requestPermission()
                }



    }

    @SuppressLint("MissingPermission")
    private fun getLocations() {

        fusedLocationClient.lastLocation?.addOnSuccessListener{
            if(it==null)
            {
                Toast.makeText(this, "No pudimos obtener localizacion",Toast.LENGTH_SHORT).show()
                Errores += 1
            }else it.apply {
                val latitud = it.latitude
                val longitud = it.longitude

                binding.lbllatitud.text = "Latitud: $latitud"
                binding.lbllongitud.text = "Longitud: $longitud"

                almacenardatos(latitud, longitud)





            }

        }
    }

    private fun almacenardatos(latitud: Double, longitud: Double) {
        val user = auth.currentUser
        val uid = user!!.uid
        var nickname: String


        val db = Firebase.firestore

        val  docRef = db.collection("user").document(uid)

        docRef.get().addOnSuccessListener { document ->
            if(document != null)
            {
                nickname = "${document.get("NickName") as String?}"


                db.collection("coord").document(uid).set(
                    hashMapOf(
                        "NickName" to nickname,
                        "Latitud" to latitud,
                        "Longitud" to longitud,
                    )
                ).addOnSuccessListener {
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Fallo al guardar la informacion",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        } .addOnFailureListener { exception ->
            Log.w(TAG, "Error al leer la base de datos.", exception)
        }


    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUIRED_PERMISSIONS_ACCESS_LOCATION
        )
    }


    private fun allPermissionGrantedGPS(): Boolean{
        if(ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }


    companion object{
        private const val REQUIRED_PERMISSIONS_ACCESS_LOCATION = 100
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUIRED_PERMISSIONS_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Permiso Consedido", Toast.LENGTH_SHORT).show()
                    getLocations()
                }else
                {
                    Toast.makeText(this, "Permiso Denegado", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }




}



