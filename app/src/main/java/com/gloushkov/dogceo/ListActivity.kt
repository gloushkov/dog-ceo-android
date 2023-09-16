package com.gloushkov.dogceo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gloushkov.dogceo.ui.main.ListFragment

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        intent.extras?.getInt(ListFragment.PARAM_NUMBER_OF_IMAGES)?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListFragment.newInstance(it))
                .commitNow()
        }
    }
}