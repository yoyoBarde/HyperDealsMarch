package com.example.kent.hyperdeals

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kent.hyperdeals.NavigationBar.DashboardActivity
import com.example.kent.hyperdeals.NavigationBar.DrawerActivityBusinessman
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
companion object {
    var userLog = false
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)

        btnregister.setOnClickListener {
//            startActivity(Intent(this,InitializeCategory::class.java))

            startActivity(Intent(this, RegistrationActivity::class.java))
        }

       homelogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))

        }

        btnregisterbusinessman.setOnClickListener{
            startActivity(Intent (this, RegistrationActivityBusinessMan::class.java))

        }

        btnloginasbusinessman.setOnClickListener{
            startActivity(Intent (this,LoginActivityBusinessman::class.java))
        }
    }
}
