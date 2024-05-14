package com.clebs.celerity.adapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.databinding.AdapterQuestionBinding
import com.clebs.celerity.models.QuestionWithOption

class QuestionAdapter(var list: ArrayList<QuestionWithOption>,var context:Context) :
    RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(val binding: AdapterQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuestionWithOption) {
            binding.heading.text = item.question
            val radio1 = binding.radio1
            val radio2 = binding.radio2
            val radio3 = binding.radio3

            radio1.isChecked = false
            radio2.isChecked = false
            radio3.isChecked = false

            binding.h1.setOnClickListener {
                if (binding.radioLayQ1.isVisible) {
//                    binding.radioLayQ1.visibility = View.GONE
                    viewGoneAnimator(binding.radioLayQ1)
                    binding.badgeArrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.arrow_left))
                } else {
//                    binding.radioLayQ1.visibility = View.VISIBLE
                    viewVisibleAnimator(binding.radioLayQ1)
                    binding.badgeArrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.down_arrow))
                }
            }

            radio1.setOnClickListener {
                visibilityCollapse(binding)
                item.selectedOption = "C"
                radio2.isChecked = false
                radio3.isChecked = false
            }

            radio2.setOnClickListener {
                visibilityCollapse(binding)
                item.selectedOption = "DR"
                radio1.isChecked = false
                radio3.isChecked = false
            }

            radio3.setOnClickListener {
                visibilityCollapse(binding)
                item.selectedOption = "BS"
                radio1.isChecked = false
                radio2.isChecked = false
            }
        }

    }

    fun visibilityCollapse(binding:AdapterQuestionBinding){
        viewGoneAnimator(binding.radioLayQ1)
        binding.badgeArrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.arrow_left))
        binding.mainLayout.setBackgroundResource(R.drawable.shape_green_new)
    }
    fun areAllQuestionsSelected(): Boolean {
        for (question in list) {
            if (question.selectedOption.isEmpty()) {
                return false
            }
        }

        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = AdapterQuestionBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return QuestionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val item = list[position]

        holder.bind(item)
    }
    private fun viewGoneAnimator(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }
    private fun viewVisibleAnimator(view: View) {
        view.animate()
            .alpha(1f)
            .setDuration(100)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.VISIBLE
                }
            })
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}