package com.apps.toschat.fragment

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.apps.toschat.MainActivity

import com.apps.toschat.R
import com.apps.toschat.VerificationActivity
import com.apps.toschat.databinding.FragmentProfileBinding
import com.apps.toschat.model.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*
import kotlin.collections.HashMap

class ProfileFragment : Fragment() {

    var binding: FragmentProfileBinding? = null
    var database: FirebaseDatabase? = null
    var storage: FirebaseStorage? = null
    var selectedImage: Uri? = null
    var auth: FirebaseAuth? = null
    var dialog: AlertDialog? =null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding?.let {
            dialog = AlertDialog.Builder(requireContext()).create()
            dialog!!.setMessage("Mengupdate Profil...")
            dialog!!.setCancelable(true)
            database = FirebaseDatabase.getInstance()
            storage = FirebaseStorage.getInstance()
            auth = FirebaseAuth.getInstance()
            val title = it.title
            val profileName = it.profileName
            val profileImage = it.profileImage
            val editBtn = it.editBtn

            val userReference = database!!.reference.child("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Ambil data dari snapshot
                    val name = snapshot.child("name").getValue(String::class.java)
                    val profileImageUrl =
                        snapshot.child("profileImage").getValue(String::class.java)

                    // Tampilkan nama dan foto profil di aplikasi
                    profileName.setHint(name)
                    title.setText(name)
                    Glide.with(this@ProfileFragment)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(profileImage)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle Error
                }
            })
            profileImage.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 45)
            }
            editBtn.setOnClickListener {
                val name: String = binding!!.profileName.text.toString()
                dialog!!.show()
                if (selectedImage != null) {
                    val reference = storage!!.reference.child("Profile")
                        .child(auth!!.uid!!)
                    reference.putFile(selectedImage!!)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener { uri ->
                                    val imageUrl = uri.toString()
                                    val uid = auth!!.uid
                                    val phone = auth!!.currentUser!!.phoneNumber
                                    val name: String = binding!!.profileName.text.toString()
                                    val user = User(uid, name, phone, imageUrl)
                                    database!!.reference
                                        .child("users")
                                        .child(uid!!)
                                        .setValue(user)
                                        .addOnCompleteListener{
                                        dialog!!.dismiss()
                                    }
                                }
                            } else {
                                val uid = auth!!.uid
                                val phone = auth!!.currentUser!!.phoneNumber
                                val name: String = binding!!.profileName.text.toString()
                                val user = User(uid, name, phone, profileImage.toString())
                                database!!.reference
                                    .child("users")
                                    .child(uid!!)
                                    .setValue(user)
                                    .addOnCanceledListener {dialog!!.dismiss()}

                            }
                        }
                }
            }




            // kode lainnya
        }
        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){
            if (data.data != null){
                val uri = data.data //filepath
                val storage = FirebaseStorage.getInstance()
                val time = Date().time
                val reference = storage.reference
                    .child("Profile")
                    .child(time.toString()+"")
                reference.putFile(uri!!).addOnCompleteListener{task->
                    if (task.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener { uri->
                            val filePath = uri.toString()
                            val obj = HashMap<String,Any>()
                            obj["image"] = filePath
                            database!!.reference
                                .child("users")
                                .child(FirebaseAuth.getInstance().uid!!)
                                .updateChildren(obj).addOnSuccessListener { }
                        }
                    }
                }
                profileImage.setImageURI(data.data)
                selectedImage = data.data

            }
        }

    }


    }