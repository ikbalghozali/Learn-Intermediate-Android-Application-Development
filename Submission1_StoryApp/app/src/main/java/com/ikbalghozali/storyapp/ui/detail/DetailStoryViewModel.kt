package com.ikbalghozali.storyapp.ui.detail


import androidx.lifecycle.ViewModel
import com.ikbalghozali.storyapp.data.remote.response.ListStoryItem

class DetailStoryViewModel: ViewModel() {
  lateinit var storyItem: ListStoryItem

  fun setDetailStory(story: ListStoryItem) : ListStoryItem{
    storyItem = story
    return storyItem
  }

}