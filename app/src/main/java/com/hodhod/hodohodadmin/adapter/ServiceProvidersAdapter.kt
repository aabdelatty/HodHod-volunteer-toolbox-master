package com.hodhod.hodohodadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hodhod.hodohodadmin.R
import com.hodhod.hodohodadmin.dto.ServiceProviderItem


class ServiceProvidersAdapter(private val items: List<ServiceProviderItem> = emptyList()) : RecyclerView.Adapter<ServiceProvidersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.service_provider_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.serviceNameTextView.text = item.getProviderType()
        holder.serviceNumberTextView.text = item.getProviderNumber()
        holder.serviceProviderImageView.setImageResource(item.type.icon)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var serviceNameTextView: TextView = v.findViewById(R.id.serviceNameTextView)
        var serviceNumberTextView: TextView = v.findViewById(R.id.serviceNumberTextView)
        var serviceProviderImageView: ImageView = v.findViewById(R.id.serviceProviderImageView)
    }
}