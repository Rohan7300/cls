package com.clebs.celerity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.AdapterQuestionBinding
import com.clebs.celerity.models.QuestionWithOption

class QuestionAdapter(var list:ArrayList<QuestionWithOption>):RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>(){

    inner class QuestionViewHolder(val binding:AdapterQuestionBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item: QuestionWithOption){
            binding.heading.text =item.question
            val radio1 = binding.radio1
            val radio2 = binding.radio2
            val radio3 = binding.radio3

            radio1.isChecked = false
            radio2.isChecked = false
            radio3.isChecked = false

            binding.h1.setOnClickListener {
                if(binding.radioLayQ1.isVisible)
                binding.radioLayQ1.visibility = View.GONE
                else
                    binding.radioLayQ1.visibility = View.VISIBLE
            }

            radio1.setOnClickListener {
                item.selectedOption = "C"
                radio2.isChecked = false
                radio3.isChecked = false
            }

            radio2.setOnClickListener {
                item.selectedOption = "DR"
                radio1.isChecked = false
                radio3.isChecked = false
            }

            radio3.setOnClickListener {
                item.selectedOption = "BS"
                radio1.isChecked = false
                radio2.isChecked = false
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = AdapterQuestionBinding.inflate(LayoutInflater.from(parent.context))
        return QuestionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }
}