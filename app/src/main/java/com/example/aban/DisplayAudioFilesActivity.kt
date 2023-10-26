package com.example.aban

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aban.databinding.ActivityRecyclerviewBinding
import com.example.aban.model.AudioModelClass
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import java.util.concurrent.CountDownLatch

class DisplayAudioFilesActivity : AppCompatActivity() {
    var binding: ActivityRecyclerviewBinding? = null
    private var adapter: RecyclerviewAdapter? = null
    var list: List<AudioModelClass>? = null
    var progressBar: ProgressBar? = null
    var firestore: FirebaseFirestore? = null
    private var storageRef: StorageReference? = null

    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recyclerview)
        list = ArrayList()
        recyclerView= findViewById<RecyclerView>(R.id.recyclerview)
        progressBar = findViewById(R.id.mainProgressBar)
        firestore = FirebaseFirestore.getInstance()

        val button6 = findViewById<ImageButton>(R.id.back)
        button6.setOnClickListener {
            val intent = Intent(this@DisplayAudioFilesActivity, ResultType::class.java)
            startActivity(intent)
        }
        //Connect profile page
        val profileButton = findViewById<ImageButton>(R.id.account)
        profileButton.setOnClickListener {
            val intent = Intent(this@DisplayAudioFilesActivity, account::class.java)
            startActivity(intent)
        }



 storageRef = FirebaseStorage.getInstance().reference.child("recordings")
        recyclerView.hasFixedSize()
        recyclerView.setLayoutManager(LinearLayoutManager(this))

        // Execute the AsyncTask to fetch Firebase Storage data
        FetchStorageDataTask().execute()
    } //end of onCreate

    private inner class FetchStorageDataTask : AsyncTask<Void?, Void?, List<AudioModelClass>>() {
        private var latch: CountDownLatch? = null
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar!!.visibility = View.VISIBLE
        }

        protected override fun doInBackground(vararg p0: Void?): List<AudioModelClass>? {
            val list: MutableList<AudioModelClass> = ArrayList()
            latch = CountDownLatch(1)
            try {
                val storageRef = FirebaseStorage.getInstance().reference.child("recordings")

                // List all items under the "recordings" directory
                storageRef.listAll().addOnCompleteListener { task: Task<ListResult> ->
                    if (task.isSuccessful) {
                        val listResult = task.result
                        Log.d("TAG", "Success task  : " + task.result.toString())
                        for (item in listResult.items) {
                            Log.d("TAG", "Inside for loop : " + item.name)
                            Log.d("TAG", "Inside for loop url: " + item.downloadUrl)
                            val name = item.name
                            val downloadUrl = item.downloadUrl.toString()
                            firestore!!.collection("recordingsData").document(name)
                                .addSnapshotListener { value: DocumentSnapshot?, error: FirebaseFirestoreException? ->
                                    val time = value!!.getString("time")
                                    val pitch = value.getString("pitch")
                                    val Loudness = value.getString("Loudness")
                                    Log.d("TAG", "Inside for loop  collection: $value")
                                    list.add(
                                        AudioModelClass(
                                            name,
                                            downloadUrl,
                                            time.toString(),
                                            pitch.toString(),
                                            Loudness.toString()
                                        )
                                    )
                                    Log.d(
                                        "TAG",
                                        "Inside for loop  collection list size: " + list.size
                                    )
                                    Log.d(
                                        "TAG", """name $name downloadUrl: $downloadUrl
time $time
pitch$pitch
loudness$Loudness"""
                                    )
                                }
                        }
                        // Once the data is fetched, update the RecyclerView
                        runOnUiThread { Handler().postDelayed({ updateRecyclerView(list) }, 5000) }
                    } else {
                        // Handle task failure
                        Log.d("TAG", "Exception else task  : " + task.exception!!.localizedMessage)
                    }
                }.addOnFailureListener { e: Exception ->
                    // Handle any other errors that may occur during listing
                    Log.d("TAG", "Exception failure : " + e.localizedMessage)
                }
            } catch (e: Exception) {
                Log.d("TAG", "Exception: " + e.localizedMessage)
            }
            try {
                latch!!.await() // Wait for all tasks to complete
            } catch (e: InterruptedException) {
                Log.d("TAG", "Exception:InterruptedException " + e.localizedMessage)
            }
            return list
        }

        private fun updateRecyclerView(list: List<AudioModelClass>) {
            progressBar!!.visibility = View.GONE
            Log.i("TAG", "onPostExecute: List Size : " + list.size)
            adapter = RecyclerviewAdapter(list)
            recyclerView!!.adapter = adapter
            latch!!.countDown() // Signal that all tasks are completed
        } //        @Override
        //        protected void onPostExecute(List<AudioModelClass> list) {
        //            super.onPostExecute(list);
        //
        //            updateRecyclerView(list);
        //            // No need to do anything here; the RecyclerView is updated in updateRecyclerView()
        //        }
    }
} //End of class
