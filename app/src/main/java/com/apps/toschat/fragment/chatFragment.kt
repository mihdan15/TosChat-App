package com.apps.toschat.fragment



import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apps.toschat.adapter.MyChatAdapter
import com.apps.toschat.R
import com.apps.toschat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_chat.*

class chatFragment : Fragment(){
    var users:ArrayList<User>? = null
    var database: FirebaseDatabase? = null
    var user: User? = null
    var presenceList:HashMap<Any, String>? = null
    var myChatAdapter:MyChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        users = ArrayList<User>()
        presenceList = HashMap<Any, String>()
        myChatAdapter = MyChatAdapter(requireActivity(), users!!, presenceList!!)

        database!!.reference.child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {
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
                    Log.d("user", users.toString())
                    if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) users!!.add(user)
                }
                rv_listMychats.layoutManager= LinearLayoutManager(activity)
                rv_listMychats.adapter= MyChatAdapter(requireActivity(), users!!,presenceList!!)

            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

        })
        database!!.reference.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val presenceList = HashMap<Any, String>()
                        val presenceSnapshot = snapshot.child("presence")
                        for (childSnapshot in presenceSnapshot.children){
                            val userId = childSnapshot.key
                            val presence = childSnapshot.value
                            presenceList.put(userId.toString(), presence.toString())
                            }
                        rv_listMychats?.layoutManager= LinearLayoutManager(activity)
                        rv_listMychats?.adapter= MyChatAdapter(requireActivity(), users!!, presenceList)

                    }
               }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
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
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        // Tambahkan kode ini di sini
        val rv_listMychats by lazy {
            view?.findViewById<RecyclerView>(R.id.rv_listMychats)
        }
        rv_listMychats?.layoutManager = LinearLayoutManager(activity)
        rv_listMychats?.adapter = myChatAdapter


        return view
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView(){
        val userList = arrayListOf<User>()
        val presenceList = HashMap<Any, String>()
        val userAdapter = MyChatAdapter(requireActivity(), userList, presenceList)
        rv_listMychats.layoutManager = LinearLayoutManager(activity)
        rv_listMychats.adapter = userAdapter
    }


    override fun onDestroy() {
        super.onDestroy()
        this.clearFindViewByIdCache()
    }



}