package com.example.kent.hyperdeals

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.*
import com.example.kent.hyperdeals.Model.UserBusinessman
import com.example.kent.hyperdeals.MyAdapters.SelectedSubcategoryAdapterBusiness
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.registrationactivitybusinessman.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class RegistrationActivityBusinessMan : AppCompatActivity() {


    var mDatabaseReference: DatabaseReference? = null
    var mAuth: FirebaseAuth? = null
    var mDatabase: FirebaseDatabase? = null
    var database = FirebaseFirestore.getInstance()
    var promoStoreList = ArrayList<String>()
val TAG = "RegisterBusinessman"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrationactivitybusinessman)
        mAuth = FirebaseAuth.getInstance()


        var loginActivity = Intent(this,LoginActivityBusinessman::class.java)
        val submit = findViewById<View>(R.id.Businessmanbtnsubmit) as Button



        submit.setOnClickListener {


            val tvLastNameBM = findViewById<View>(R.id.Businessmanlastname) as EditText
            val tvFirstNameBM = findViewById<View>(R.id.Businessmanfirstname) as EditText
            val tvEmailBM = findViewById<View>(R.id.Businessmanemail) as EditText
            val tvPasswordBM = findViewById<View>(R.id.Businessmanpassword) as EditText
            val progressbarBM = findViewById<View>(R.id.progressbarBM) as ProgressBar


            var email = tvEmailBM.text.toString()
            var password = tvPasswordBM.text.toString()
            var lastname = tvLastNameBM.text.toString()
            var firstname = tvFirstNameBM.text.toString()
            var allowedToRegister = true
            if(editText2.text.toString()!=password){
                toast("Password doesn't matched")
                allowedToRegister =false
            }
            if (email.isEmpty() || password.isEmpty() || lastname.isEmpty() || firstname.isEmpty()  || editText2.text.isEmpty() ) {
                allowedToRegister = false
                toast("You left an empty field")
            }


            if(allowedToRegister){

                createAuth(email,password,firstname,lastname)
                progressbarBM.visibility = View.VISIBLE

            }

        }
        btn_addStore.setOnClickListener {showAddStore()}



    }
    fun createAuth(email:String,password:String,firstname:String,lastname:String){
    mAuth!!.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) { task ->
                progressbarBM.visibility = View.INVISIBLE
                if (task.isSuccessful) {
                    val userID = mAuth!!.currentUser!!.uid
                    val currentUserDb = mDatabaseReference!!.child(userID)
                    currentUserDb.child("firstName").setValue(firstname)
                    currentUserDb.child("lastName").setValue(lastname)
                    Toast.makeText(this, "Registered Success!", Toast.LENGTH_SHORT).show()


                    doAsync {
                        database.collection("UserBusinessman").document(email).get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        toast("Email already Exist")
                                        progressbarBM.visibility = View.GONE

                                    } else {
                                        var myUserBusinessMan = UserBusinessman(firstname, lastname, email, password, promoStoreList)
                                        database.collection("UserBusinessman").document(email).set(myUserBusinessMan).addOnCompleteListener {
                                            Log.e(TAG,"Successfuly Registered")
                                            uiThread {   this@RegistrationActivityBusinessMan.startActivity(Intent(this@RegistrationActivityBusinessMan,LoginActivityBusinessman::class.java))}
                                        }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                    progressbarBM.visibility = View.GONE

                                }


                    }



                } else {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()

                }
            }

    }
    fun showAddStore() {


        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_store, null)

        dialogBuilder.setCancelable(false)

        dialogBuilder.setView(dialogView)


        val businessStore = dialogView.findViewById(R.id.businessManPromoStore) as TextInputEditText
        val addBtn = dialogView.findViewById(R.id.btn_add_business_store) as Button

        val b = dialogBuilder.create()
        b.show()

        addBtn.setOnClickListener {
            promoStoreList.add(businessStore.text.toString())
            Log.e("AddBtn",promoStoreList.size.toString())
            var simpleAdapter = SelectedSubcategoryAdapterBusiness(this,promoStoreList)
            listView.layoutManager  = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
            listView.adapter = simpleAdapter
            b.dismiss()
        }


    }

}
