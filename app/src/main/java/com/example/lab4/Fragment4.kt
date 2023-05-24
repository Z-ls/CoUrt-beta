package com.example.lab4


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import it.polito.mad.court.DbCourt
import it.polito.mad.court.dataclass.User
import androidx.fragment.app.FragmentTransaction


class Fragment4 : Fragment(
) {
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.page4, null)
        val user: MutableLiveData<User> = MutableLiveData()
        user.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.profile_fullname).text = it.nickname
            view.findViewById<TextView>(R.id.profile_location).text = it.city
            view.findViewById<TextView>(R.id.profile_phone_number).text = it.phone
            rowInterest(it, view)
        }
        DbCourt().getUserByEmail("test@gmail.com") {
            user.value = it
        }
        val btn: ImageView = view.findViewById(R.id.profile_image)
        btn.setOnClickListener {
            switchToNextView()
        }
        return view
    }

    private fun switchToNextView() {
        val nextFragment = Fragment5() // Replace with the fragment you want to switch to
        parentFragmentManager.beginTransaction()
            .add(R.id.frame, nextFragment)
            .addToBackStack(null)
            .hide(this)
            .show(nextFragment)
            .commit()
    }

    private fun rowInterest(user: User, view: View) {
        val layout = view.findViewById<LinearLayout>(R.id.ll_profile_interests)
        for (interest in user.sportList) {
            if (interest.second == 0) {
                continue
            }
            val tvInterest = TextView(context)
            tvInterest.text = interest.first
            tvInterest.background = ContextCompat.getDrawable(requireContext(), R.drawable.chips)
            tvInterest.setPadding(7, 7, 7, 7)
            tvInterest.setBackgroundResource(R.drawable.chips)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(5, 5, 5, 5)
            tvInterest.layoutParams = params
            when (interest.second) {
                1 -> {
                    tvInterest.background = Color.GREEN.toDrawable()
                }

                2 -> {
                    tvInterest.background = Color.CYAN.toDrawable()
                }

                3 -> {
                    tvInterest.background = Color.YELLOW.toDrawable()
                }
            }
            layout.addView(tvInterest)
        }
    }
}

