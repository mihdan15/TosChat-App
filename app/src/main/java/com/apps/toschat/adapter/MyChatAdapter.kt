package com.apps.toschat.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.apps.toschat.ChatActivity
import com.apps.toschat.R
import com.apps.toschat.databinding.MyChatsItemBinding
import com.apps.toschat.model.Message
import com.apps.toschat.model.User
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.my_chats_item.*

class MyChatAdapter(
    private val context: Context,
    var userList:ArrayList<User>,
    var presenceList:HashMap<Any, String>
    ): RecyclerView.Adapter<MyChatAdapter.UserViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
        UserViewHolder{
            val v = LayoutInflater.from(context).inflate(
                R.layout.my_chats_item,
                parent,
                false)
            return UserViewHolder(v)
        }

    fun setPresence(presence: HashMap<Any, String>) {
        this.presenceList = presence
    }

    override fun getItemCount() = userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        presenceList.forEach {
            if (it.key.toString() == user.uid.toString()) {
                if (it.value == "Offline"){
                    holder.check.setColorFilter(Color.DKGRAY)
                }else {
                    holder.txtFriendTextChat.setTypeface(null, Typeface.BOLD)
                }
                holder.txtFriendTextChat.setText(it.value)
                Log.d("for key presence", it.key.toString())
                Log.d("for User id", user.uid.toString())
            }
        }
        holder.bindItem(user)
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", user.name)
            intent.putExtra("image", user.profileImage)
            intent.putExtra("uid", user.uid)
            context.startActivity(intent)
        }


    }


    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val binding = MyChatsItemBinding.bind(itemView)
        val txtFriendTextChat = binding.txtFriendTextChat
        val check = binding.check
            fun bindItem(user: User) {
                binding.txtFriendNameChat.text=user.name

                Glide.with(binding.pp1.context).load(user.profileImage)

                    .placeholder(R.drawable.ic_baseline_person_24)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("TAG", "Error loading image", e)
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


