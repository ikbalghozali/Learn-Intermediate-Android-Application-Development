package com.ikbalghozali.storyapp.ui.detail

import android.graphics.text.LineBreaker
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.ikbalghozali.storyapp.R
import com.ikbalghozali.storyapp.data.remote.response.ListStoryItem
import com.ikbalghozali.storyapp.databinding.ActivityDetailStoryBinding
import com.ikbalghozali.storyapp.utils.formatDate
import java.util.*

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var story: ListStoryItem
    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel: DetailStoryViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = getString(R.string.story_detail_activity)
            elevation = 0f
            setDisplayHomeAsUpEnabled(true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            run {
                binding.textViewDescription.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }
        }
        story = intent.getParcelableExtra(EXTRA_STORY)!!
        viewModel.setDetailStory(story)
        displayResult()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayResult() {
        with(binding){
            textViewName.text = viewModel.storyItem.name
            textViewCreatedTime.text = getString(R.string.createdAdd, formatDate(viewModel.storyItem.createdAt, TimeZone.getDefault().id ))
            textViewDescription.text = viewModel.storyItem.description
            Glide.with(imageStory)
                .load(viewModel.storyItem.photoUrl)
                .into(imageStory)
        }
    }

    companion object {
        const val EXTRA_STORY = "story"
    }
}