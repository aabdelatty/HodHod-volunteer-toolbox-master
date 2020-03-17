package com.revolutan.hodhodclint.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.revolutan.hodhodclint.R
import com.revolutan.hodhodclint.dto.Issue
import com.revolutan.hodhodclint.dto.Problems


class IssuesAdapter(private val items: List<Issue> = emptyList(), val onItemClick: OnItemClick) : RecyclerView.Adapter<IssuesAdapter.ViewHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.issues_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.issueImageView.setImageResource(Problems.fromString(item.type).icon)
        holder.issueIdTextView.text = item.type

        holder.itemView.setOnClickListener {
            onItemClick.onClick(item)
        }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var issueImageView: ImageView = v.findViewById(R.id.issueImageView)
        var issueIdTextView: TextView = v.findViewById(R.id.issueIdTextView)
    }

    interface OnItemClick {

        fun onClick(item: Issue)
    }
}