package com.clebs.celerity.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.databinding.AdapterFeedbackBinding
import com.clebs.celerity.models.QuestionWithOption

class FeedbackQuestionareAdapter(var list: ArrayList<QuestionWithOption>, var context: Context) :
    RecyclerView.Adapter<FeedbackQuestionareAdapter.FeedBackViewHolder>() {
    inner class FeedBackViewHolder(val binding: AdapterFeedbackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionWithOption) {
            Log.d("QuestionWithOption","Item $item")
            binding.heading.text = item.question
            val radio1 = binding.radio1
            val radio2 = binding.radio2
            if (item.selectedOption == "Y") {
                radio1.isChecked = true
                radio2.isChecked = false
                binding.radioLayQ1.visibility = View.GONE
                binding.badgeArrow.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.arrow_left
                    )
                )
                binding.FeedbackmainLayout.setBackgroundResource(R.drawable.shape_green_new)
            }
            else if (item.selectedOption == "N") {
                radio2.isChecked = true
                radio1.isChecked = false
                binding.radioLayQ1.visibility = View.GONE
                binding.badgeArrow.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.arrow_left
                    )
                )
                binding.FeedbackmainLayout.setBackgroundResource(R.drawable.shape_green_new)
            }
            else {
                radio2.isChecked = false
                radio1.isChecked = false
                binding.badgeArrow.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.down_arrow
                    )
                )
                binding.radioLayQ1.visibility = View.VISIBLE
                binding.FeedbackmainLayout.setBackgroundResource(R.drawable.shape_expand_main)
            }


            binding.h1.setOnClickListener {
                if (binding.radioLayQ1.isVisible) {
                    binding.radioLayQ1.visibility = View.GONE
                    binding.badgeArrow.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.arrow_left
                        )
                    )
                } else {
                    binding.radioLayQ1.visibility = View.VISIBLE
                    binding.badgeArrow.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.down_arrow
                        )
                    )
                }
            }

            radio1.setOnClickListener {
                item.selectedOption = "Y"
                binding.radioLayQ1.visibility = View.GONE
                binding.badgeArrow.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.arrow_left
                    )
                )
                binding.FeedbackmainLayout.setBackgroundResource(R.drawable.shape_green_new)
                radio2.isChecked = false
                notifyDataSetChanged()
            }

            radio2.setOnClickListener {
                item.selectedOption = "N"
                binding.radioLayQ1.visibility = View.GONE
                binding.badgeArrow.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.arrow_left
                    )
                )
                notifyDataSetChanged()
                binding.FeedbackmainLayout.setBackgroundResource(R.drawable.shape_green_new)
                radio1.isChecked = false
            }
        }
    }

    fun areAllQuestionsSelected(): Boolean {
        for (question in list) {
            if (question.selectedOption.isEmpty()) {
                return false
            }
        }
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedBackViewHolder {
        val binding =
            AdapterFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedBackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FeedBackViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}