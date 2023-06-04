package com.example.lab4

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class BottomBar : AppCompatActivity(), Fragment5Listener, Fragment4Listener {
    private var f1: Fragment1? = null
    private var f2: Fragment2? = null
    private var f3: Fragment3? = null
    private var f4: Fragment4? = null
    private var f5: Fragment5? = null
    private var b1: Button? = null
    private var b2: Button? = null
    private var b3: Button? = null
    private var b4: Button? = null

    override fun switchToFragment4() {
        page4(null)
    }

    override fun switchToFragment5() {
        page5(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottombar)
        b1 = findViewById(R.id.buttom1)
        b2 = findViewById(R.id.buttom2)
        b3 = findViewById(R.id.buttom3)
        b4 = findViewById(R.id.buttom4)
        b1?.setOnClickListener { page1() }
        b2?.setOnClickListener { page2() }
        b3?.setOnClickListener { page3() }
        b4?.setOnClickListener { page4() }
        page2()
    }

    fun page1(v: View? = null) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f1 == null) {
            f1 = Fragment1()
            transaction.add(R.id.frame, f1!!)
        }
        hideFragment(transaction)
        transaction.show(f1!!)
        transaction.commit()
    }

    fun page2(v: View? = null) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f2 == null) {
            f2 = Fragment2()
            transaction.add(R.id.frame, f2!!)
        }
        hideFragment(transaction)
        transaction.show(f2!!)
        transaction.commit()
    }

    fun page3(v: View? = null) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f3 == null) {
            f3 = Fragment3()
            transaction.add(R.id.frame, f3!!)
        }
        hideFragment(transaction)
        transaction.show(f3!!)
        transaction.commit()
    }

    fun page4(v: View? = null) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        if (f4 == null) {
            f4 = Fragment4()
            transaction.add(R.id.frame, f4!!)
        }
        hideFragment(transaction)
        transaction.show(f4!!)
        transaction.commit()
    }

    private fun page5(v: View? = null) {
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

interface Fragment5Listener {
    fun switchToFragment4()
}

interface Fragment4Listener {
    fun switchToFragment5()
}
