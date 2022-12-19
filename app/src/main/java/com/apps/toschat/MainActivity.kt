package com.apps.toschat

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.apps.toschat.adapter.FragmentAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
//    private var REQUEST_CAMERA_IMAGE = 1
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    val auth = FirebaseAuth.getInstance().currentUser
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //sembunyikan action bar
//        getSupportActionBar()?.hide();

        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPager2)

        val adapter = FragmentAdapter(supportFragmentManager, lifecycle)

        viewPager2.adapter = adapter
        viewPager2.setCurrentItem(1, true)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Kontak"
                }
                1 -> {
                    tab.text = "Chats"
                }
                2 -> {
                    tab.text = "Profil"
                }
            }
        }.attach()
        val cameraButton = findViewById<ImageView>(R.id.ic_camera)
        // Menambahkan listener untuk tombol camera
        cameraButton.setOnClickListener {
            // Membuat intent ke aplikasi kamera
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//          Menjalankan intent
            startActivity(cameraIntent)
//            startActivityForResult(intent, REQUEST_CAMERA_IMAGE)
        }

        val pProfil = findViewById<ImageView>(R.id.ic_more)

        val profileImageReferenceMain = FirebaseStorage.getInstance().getReference("/Profile/${FirebaseAuth.getInstance().currentUser!!.uid}")
        profileImageReferenceMain.downloadUrl.addOnSuccessListener {
            // URL foto profil sudah didapatkan, gunakan Glide untuk menampilkannya di ImageView
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.ic_placeholder) // Tampilan sementara jika foto belum selesai di-load
                .into(pProfil)
        }

        pProfil.setOnClickListener {
            val database = FirebaseDatabase.getInstance()
            val profileImageReference = database.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("profileImage")
            val nameReference = database.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("name")

            // Mengambil nilai dari database
            profileImageReference.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImageUrl = snapshot.getValue(String::class.java)
                    Log.d("Poto", "onDataChange: ${profileImageUrl}")

                    // Membuat alert dialog
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setTitle("Foto Profil")

                    // Mengambil nilai nama dari database
                    nameReference.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val name = snapshot.getValue(String::class.java)
                            builder.setMessage(name)

                            // Tambahkan ImageView ke dalam AlertDialog
                            val imageView = ImageView(this@MainActivity)
                            Glide.with(this@MainActivity).load(profileImageUrl)
                                .placeholder(R.drawable.pphacker)
                                .into(imageView)
//                            val textView = TextView(this@MainActivity)
//                            textView.text = "       Istiqomah di jalan dakwah"

                            val layout = LinearLayout(this@MainActivity)
                            layout.orientation = LinearLayout.VERTICAL
                            layout.addView(imageView)
//                            layout.addView(textView)

                            // Tampilkan tampilan dalam builder
                            builder.setView(layout)

                            // Tambahkan tombol OK untuk menutup AlertDialog
                            builder.setPositiveButton("OK") { dialog, which ->
                                dialog.dismiss()
                            }
                            // Tambahkan tombol Log Out untuk logout akun
//                            builder.setNegativeButton("Log Out") { dialog, which ->
//
//                                FirebaseAuth.getInstance().signOut()
//                                // Arahkan ke halaman login
//                                val i = Intent(this@MainActivity, VerificationActivity::class.java)
//                                startActivity(i)
//                                finish()
//                            }

                            // Tampilkan AlertDialog
                            val dialog = builder.create()
                            dialog.show()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }






















//        pProfil.setOnClickListener {
//            val profileImageReference = database.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("profileImage")
//            val namaReference = database.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("name")
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//            val builder = AlertDialog.Builder(this)
//            builder.setTitle("Foto Profil")
//            builder.setMessage("salah")
//
//            // Tambahkan ImageView ke dalam AlertDialog
//            val imageView = ImageView(this)
//            Glide.with(this).load(profileImageReference)
//                .placeholder(R.drawable.ic_placeholder)
//                .into(imageView)
//            val textView = TextView(this)
//            textView.text = "       Istiqomah di jalan dakwah"
//
//
//            val layout = LinearLayout(this)
//            layout.orientation = LinearLayout.VERTICAL
//            layout.addView(imageView)
//            layout.addView(textView)
//
//            // Tampilkan tampilan dalam builder
//            builder.setView(layout)
//
//            // Tambahkan tombol OK untuk menutup AlertDialog
//            builder.setPositiveButton("OK") { dialog, which ->
//                dialog.dismiss()
//            }
//
//            // Tampilkan AlertDialog
//            val dialog = builder.create()
//            dialog.show()
//        }

    }
}

