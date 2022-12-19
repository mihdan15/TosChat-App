package com.apps.toschat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apps.toschat.databinding.ActivitySetupProfileBinding
import com.apps.toschat.databinding.FragmentProfileBinding
import com.apps.toschat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.internal.InternalTokenProvider
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_setup_profile.*
import java.util.*
import kotlin.collections.HashMap

class ProfileActivityOld : AppCompatActivity() {

    var binding:FragmentProfileBinding? = null
    var auth:FirebaseAuth? = null
    var database:FirebaseDatabase? = null
    var storage:FirebaseStorage? = null
    var selectedImage: Uri? = null
    var dialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = ProgressDialog(this)
        binding = FragmentProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        dialog!!.setMessage("Updating Profile")
        dialog!!.setCancelable(false)
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        binding!!.profileImage.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 45)
        }
        binding!!.editBtn.setOnClickListener{
            val name :String = binding!!.profileName.text.toString()
            dialog!!.show()
            if (selectedImage != null){
                val reference = storage!!.reference.child("Profile")
                    .child(auth!!.uid!!)
                reference.putFile(selectedImage!!)
                    .addOnCompleteListener{task->
                        if (task.isSuccessful){
                            reference.downloadUrl.addOnSuccessListener {uri->
                                val imageUrl = uri.toString()
                                val uid = auth!!.uid
                                val phone = auth!!.currentUser!!.phoneNumber
                                val name :String = binding!!.profileName.text.toString()
                                val user = User(uid,name,phone, imageUrl)
                                database!!.reference
                                    .child("users")
                                    .child(uid!!)
                                    .setValue(user)
                                    .addOnCompleteListener{
                                        dialog!!.dismiss()
                                        val intent = Intent(this,
                                            MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                            }
                        }
                        else{
                            val uid = auth!!.uid
                            val phone = auth!!.currentUser!!.phoneNumber
                            val name :String = binding!!.profileName.text.toString()
                            val user = User(uid,name,phone,"No Image")
                            database!!.reference
                                .child("users")
                                .child(uid!!)
                                .setValue(user)
                                .addOnCanceledListener {
                                    dialog!!.dismiss()
                                    val intent = Intent(this,
                                        MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                        }
                    }
            }
        }
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
                binding!!.profileImage.setImageURI(data.data)
                selectedImage = data.data

            }
        }

    }
}