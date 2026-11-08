package org.akanework.riviere.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.akanework.riviere.R
import org.akanework.riviere.ui.data.HolderTypes

class TransactionAdapter(
    private val transactions: List<HolderTypes.TransactionEntry>
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.detail_icon)
        val title: TextView = view.findViewById(R.id.detail_title)
        val subtitle: TextView = view.findViewById(R.id.detail_subtitle)
        val amount: TextView = view.findViewById(R.id.detail_amount)
        val currency: TextView = view.findViewById(R.id.detail_currency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.detail_item, parent, false)
        )
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tx = transactions[transactions.size - 1 - position]
        holder.title.text = tx.presetDesc ?: tx.category
        holder.subtitle.text = tx.category
        val prefix = if (tx.isIncome) "+" else "-"
        holder.amount.text = "$prefix${String.format("%.2f", tx.amount)}"
        holder.icon.setImageDrawable(
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_local_cafe)
        )
    }
}
