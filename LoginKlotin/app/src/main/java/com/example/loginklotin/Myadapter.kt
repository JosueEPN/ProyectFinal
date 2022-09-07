package com.example.loginklotin



import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextParams
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.properties.Delegates

class Myadapter( private val userlist: ArrayList<User>) : RecyclerView.Adapter<Myadapter.MyViewHolder>() {


    private var longitud :Double = 0.0
    private var latitud :Double = 0.0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,
        parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val context = holder.nickname.context
        val user : User = userlist[position]
        //val url = "https://www.google.com.ec/maps/dir/-0.2889636,-78.5546184/-0.2898139,-78.5499799"
        val url = "https://maps.google.com/?q=${user.Latitud},${user.Longitud}"


        holder.nickname.text = user.NickName
        holder.latitud.text = user.Latitud.toString()
        holder.longitud.text = user.Longitud.toString()
        holder.button.setOnClickListener {



                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)


        }
    }




    override fun getItemCount(): Int {
        return userlist.size
    }



    public class MyViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nickname : TextView = itemView.findViewById(R.id.tvNickname)
        val latitud : TextView = itemView.findViewById(R.id.tvLatitud)
        val longitud : TextView = itemView.findViewById(R.id.tvLongitud)
        val button : TextView = itemView.findViewById(R.id.VerMap)


    }


}