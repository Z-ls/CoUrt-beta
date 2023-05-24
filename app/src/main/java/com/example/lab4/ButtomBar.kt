package com.example.lab4

import Fragment3
import android.os.Bundle
import android.view.View
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class ButtomBar : AppCompatActivity() {
    var f1: Fragment1? = null
    var f2: Fragment2? = null
    var f3: Fragment3? = null
    var f4: Fragment4? = null
    var f5: Fragment5? = null
    var b1: Button? = null
    var b2: Button? = null
    var b3: Button? = null
    var b4: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottombar)
        b1 = findViewById(R.id.buttom1) as Button?
        b2 = findViewById(R.id.buttom2) as Button?
        b3 = findViewById(R.id.buttom3) as Button?
        b4 = findViewById(R.id.buttom4) as Button?
        b1?.setOnClickListener { page1(it) }
        b2?.setOnClickListener { page2(it) }
        b3?.setOnClickListener { page3(it) }
        b4?.setOnClickListener { page4(it) }
        page1(null)
    }

    fun page1(v: View?) {
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f1 == null) {
            f1 = Fragment1()
            transaction.add(R.id.frame, f1!!)
        }
        hideFragment(transaction)
        transaction.show(f1!!)
        transaction.commit()
    }

    fun page2(v: View?) {
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f2 == null) {
            f2 = Fragment2()
            transaction.add(R.id.frame, f2!!)
        }
        hideFragment(transaction)
        transaction.show(f2!!)
        transaction.commit()
    }

    fun page3(v: View?) {
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f3 == null) {
            f3 = Fragment3()
            transaction.add(R.id.frame, f3!!)
        }
        hideFragment(transaction)
        transaction.show(f3!!)
        transaction.commit()
    }

    fun page4(v: View?) {
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f4 == null) {
            f4 = Fragment4()
            transaction.add(R.id.frame, f4!!)
        }
        hideFragment(transaction)
        transaction.show(f4!!)
        transaction.commit()
    }

    fun page5(v: View?) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f5 == null) {
            f5 = Fragment5()
            transaction.add(R.id.frame, f5!!)
        }
        hideFragment(transaction)
        transaction.show(f5!!)
        transaction.commit()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        if (f1 != null) {
            transaction.hide(f1!!)
        }
        if (f2 != null) {
            transaction.hide(f2!!)
        }
        if (f3 != null) {
            transaction.hide(f3!!)
        }
        if (f4 != null) {
            transaction.hide(f4!!)
        }
        if (f5 != null) {
            transaction.hide(f5!!)
        }
    }
}
