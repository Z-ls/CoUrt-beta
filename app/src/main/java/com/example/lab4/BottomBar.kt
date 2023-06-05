package com.example.lab4

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import it.polito.mad.court.ViewUserLogin


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
        b4?.setOnClickListener {
            registerForContextMenu(b4)
            openContextMenu(b4)
        }
        page4()
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

    private fun logout(v: View? = null) {
        val intent = Intent(this, ViewUserLogin::class.java)
        startActivity(intent)
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putInt(
            "page",
            if (f1?.isVisible() == true) {
                1
            } else if (f2?.isVisible() == true) {
                2
            } else if (f3?.isVisible() == true) {
                3
            } else if (f4?.isVisible() == true) {
                4
            } else if (f5?.isVisible() == true) {
                5
            } else {
                4
            }
        )
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        when (savedInstanceState?.getInt("page")) {
            1 -> page1()
            2 -> page2()
            3 -> page3()
            4 -> page4()
            5 -> page5()
        }
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.user_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.view_profile -> page4()
            R.id.edit_profile -> page5()
            R.id.logout -> logout()
        }
        return super.onContextItemSelected(item)
    }
}

interface Fragment5Listener {
    fun switchToFragment4()
}

interface Fragment4Listener {
    fun switchToFragment5()
}
