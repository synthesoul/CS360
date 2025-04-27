package com.example.proj3f

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InventoryAdapter(private val itemList: List<InventoryItem>) :
    RecyclerView.Adapter<InventoryAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.textItemName)
        val itemQty: TextView = itemView.findViewById(R.id.textItemQty)

        init {
            itemView.setOnClickListener {
                val item = itemList[adapterPosition]
                val context = itemView.context
                val intent = Intent(context, AddEditItemActivity::class.java).apply {
                    putExtra("ITEM_ID", item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.name
        holder.itemQty.text = "Qty: ${item.quantity}"
    }

    override fun getItemCount(): Int = itemList.size
}
