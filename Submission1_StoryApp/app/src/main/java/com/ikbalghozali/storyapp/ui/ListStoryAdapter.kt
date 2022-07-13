package com.ikbalghozali.storyapp.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ikbalghozali.storyapp.R
import com.ikbalghozali.storyapp.data.remote.response.ListStoryItem
import com.ikbalghozali.storyapp.databinding.ListItemBinding
import com.ikbalghozali.storyapp.ui.detail.DetailStoryActivity
import com.ikbalghozali.storyapp.utils.DiffUtilCallback

class ListStoryAdapter : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {
    private val listStory = ArrayList<ListStoryItem>()

    inner class ViewHolder(private var binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            with(binding) {
                Glide.with(imageViewStory)
                    .load(story.photoUrl)
                    .into(imageViewStory)
                textViewName.text = story.name
                textViewDescription.text = story.description
                textViewCreatedTime.text = binding.root.resources.getString(R.string.createdAdd, story.createdAt)
                imageViewStory.setOnClickListener {
                    val intentDetails = Intent(it.context, DetailStoryActivity::class.java)
                    intentDetails.putExtra(DetailStoryActivity.EXTRA_STORY, story)
                    it.context.startActivity(intentDetails)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount() = listStory.size

    fun setList(itemStory: List<ListStoryItem>) {
        val diffCallback = DiffUtilCallback(this.listStory, itemStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listStory.clear()
        this.listStory.addAll(itemStory)
        diffResult.dispatchUpdatesTo(this)
    }
}