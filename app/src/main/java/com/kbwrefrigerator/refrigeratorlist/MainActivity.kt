package com.kbwrefrigerator.refrigeratorlist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var fragmentMain = MainFragment()
    private val fragmentNote by lazy { NoteFragment() }
    private val fragmentRecipe by lazy { RecipeFragment() }
    private val fragmentSetting by lazy { SettingFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Admob Initialize
        MobileAds.initialize(this) {}
        mainBanner.loadAd(AdRequest.Builder().build())

        changeFragment(fragmentMain)
        initNavigationBar()
    }

    private fun initNavigationBar() {
        bottomNavi.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.first -> {
                        changeFragment(fragmentMain)
                    }
                    R.id.second -> {
                        changeFragment(fragmentNote)
                    }
                    R.id.third -> {
                        changeFragment(fragmentRecipe)
                    }
                    R.id.fourth -> {
                        changeFragment(fragmentSetting)
                    }
                }
                true
            }
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
    }

    // 액티비티 재실행시 갱신
    override fun onRestart() {
        super.onRestart()

        // 현재 프래그먼트를 구해서 만약 레시피 프래그먼트면 새로고침 안함
        var currentFragment = supportFragmentManager.findFragmentById(R.id.mainContainer)
        if (currentFragment !is RecipeFragment && currentFragment !is SettingFragment) {
            fragmentMain.refreshAdapter()
        }
    }


}
