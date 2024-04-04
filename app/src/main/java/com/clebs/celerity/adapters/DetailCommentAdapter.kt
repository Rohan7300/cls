package com.clebs.celerity.adapters

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.databinding.AdapterDetailCommentBinding
import com.clebs.celerity.models.response.DocXX
import com.clebs.celerity.ui.CommentDetailActivity
import com.clebs.celerity.utils.showToast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailCommentAdapter(
    var arrayList: ArrayList<DocXX>,
    var commentDetailActivity: CommentDetailActivity
) :
    RecyclerView.Adapter<DetailCommentAdapter.DetailCommentViewHolder>() {

    inner class DetailCommentViewHolder(var binding: AdapterDetailCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DocXX, posEven: Boolean) {
            var tv = binding.tvComment1
            var date = binding.t1
            var imgName = binding.imgName1
            var linkView = binding.link1
            if(posEven){
                binding.c2.visibility = View.GONE
            }else{
                binding.c1.visibility = View.GONE
                tv = binding.tvComment2
                imgName = binding.imgName2
                linkView = binding.link2
                date = binding.t2
            }
            linkView.visibility= View.GONE
            if(item.HasAttachment){
                linkView.visibility= View.VISIBLE
                imgName.text = item.ActivityFileName
                linkView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.FilePath.Result))
                    if (intent.resolveActivity(commentDetailActivity.packageManager) != null) {
                        commentDetailActivity.startActivity(intent)
                    } else {
                        showToast("Unable to open link",commentDetailActivity)
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv.text =
                    Html.fromHtml(item.CommentWithDateAndTime, Html.FROM_HTML_MODE_COMPACT)
            } else {
                @Suppress("DEPRECATION")
                tv.text = Html.fromHtml(item.CommentWithDateAndTime)
            }
            var formattedDate = item.ActionOn
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

                val dateTime = LocalDateTime.parse(item.ActionOn, formatter)


                val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                formattedDate = dateTime.format(outputFormatter)

            } catch (_: Exception) {
            }
            date.text = formattedDate

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailCommentViewHolder {
        var binding = AdapterDetailCommentBinding.inflate(LayoutInflater.from(parent.context))
        return DetailCommentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: DetailCommentViewHolder, position: Int) {
        if (position % 2 == 0)
            holder.bind(arrayList[position], true)
        else
            holder.bind(arrayList[position], false)
    }
}