package com.ifmg.musica

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class iTunesAdapter(private val iTunesList: List<iTunesItem>) :
    RecyclerView.Adapter<iTunesAdapter.iTunesViewHolder>() {

    class iTunesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDetails: TextView = itemView.findViewById(R.id.itemDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): iTunesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itunes, parent, false)
        return iTunesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: iTunesViewHolder, position: Int) {
        val currentItem = iTunesList[position]

        holder.itemName.text = currentItem.name
        holder.itemDetails.text = "${currentItem.kind} - ${currentItem.artist}"
    }

    override fun getItemCount(): Int {
        return iTunesList.size
    }
}


