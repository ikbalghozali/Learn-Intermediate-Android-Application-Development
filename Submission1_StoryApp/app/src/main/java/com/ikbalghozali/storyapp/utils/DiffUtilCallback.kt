package com.ikbalghozali.storyapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.ikbalghozali.storyapp.data.remote.response.ListStoryItem

class DiffUtilCallback(
    private val oldStoryList: List<ListStoryItem>,
    private val newStoryList: List<ListStoryItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldStoryList.size

    override fun getNewListSize() = newStoryList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldStoryList[oldItemPosition].id == newStoryList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldStory = oldStoryList[oldItemPosition]
        val newStory = newStoryList[newItemPosition]
        return oldStory.id == newStory.id
    }
}