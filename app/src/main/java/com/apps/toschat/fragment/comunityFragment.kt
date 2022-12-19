package com.apps.toschat.fragment

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import com.apps.toschat.adapter.MyComunityAdapter
import com.apps.toschat.R
import com.apps.toschat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_comunity.*

class comunityFragment : Fragment(){

    var users:ArrayList<User>? = null
    var database: FirebaseDatabase? = null
    var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        users = ArrayList<User>()
//        usersAdapter = MyComunityAdapter(requireActivity(), users!!)
        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) { }
            })
        database!!.reference.child("users").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                users!!.clear()
                for (snapshot1 in snapshot.children){
                    val user:User? = snapshot1.getValue(User::class.java)
                    if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) users!!.add(user)
                }
                rv_listMyCommunity.layoutManager= LinearLayoutManager(activity)
                rv_listMyCommunity.adapter= MyComunityAdapter(requireActivity(), users!!)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })

    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference.child("presence")
            .child(currentId!!).setValue("Offline")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comunity, container, false)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        val userList = arrayListOf<User>()
        val userAdapter = MyComunityAdapter(requireActivity(), userList)
        rv_listMyCommunity.layoutManager = LinearLayoutManager(activity)
        rv_listMyCommunity.adapter = userAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        this.clearFindViewByIdCache()
    }
}