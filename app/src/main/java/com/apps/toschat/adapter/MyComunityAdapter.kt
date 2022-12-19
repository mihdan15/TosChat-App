package com.apps.toschat.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.apps.toschat.ChatActivity
import com.apps.toschat.R
import com.apps.toschat.databinding.MyComunityItemBinding
import com.apps.toschat.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

class  MyComunityAdapter(private var context: Context, var userList:ArrayList<User>):
    RecyclerView.Adapter<MyComunityAdapter.UserViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
        UserViewHolder {
            val v = LayoutInflater.from(context).inflate(
                R.layout.my_comunity_item,
                parent,
                false
            )
        return UserViewHolder(v)
        }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bindItem(user)
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", user.name)
            intent.putExtra("image", user.profileImage)
            intent.putExtra("uid", user.uid)
            context.startActivity(intent)
        }
//        holder.binding.txtCommunityName.text = user.name
//        Glide.with(context).load(user.profileImage)
//            .placeholder(R.drawable.ic_baseline_person_24)
//            .into(holder.binding.pp1)
    }

//    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val binding :MyComunityItemBinding = MyComunityItemBinding.bind(itemView)
//    }

    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val binding = MyComunityItemBinding.bind(itemView)
        fun bindItem(user: User) {
            binding.txtCommunityName.text=user.name
            Glide.with(binding.pp1.context).load(user.profileImage)
                .placeholder(R.drawable.ic_baseline_person_24)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("eror pp kontak", "${user.profileImage}", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .error(R.drawable.ic_baseline_person_24)
                .into(binding.pp1)


        }
    }


}