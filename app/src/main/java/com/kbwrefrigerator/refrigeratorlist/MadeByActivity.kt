package com.kbwrefrigerator.refrigeratorlist

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_made_by.*

class MadeByActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_made_by)

        // toolbar
        var toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false) // 기본 제목을 없애줌
        actionBar.setDisplayHomeAsUpEnabled(true)

        privacyPolicyBtn.setOnClickListener {
            val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kbwplace.tistory.com/30"))
            startActivity(privacyPolicyIntent)
        }

    }
}