package com.example.lab4


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import it.polito.mad.court.DbCourt
import it.polito.mad.court.SharedPreferencesHelper
import it.polito.mad.court.dataclass.User


class Fragment4 : Fragment() {

    private var listener: Fragment4Listener? = null
    private var user = MutableLiveData<User>()

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            DbCourt().getUserByEmail(SharedPreferencesHelper.getUserData(requireContext()).email) {
                user.value = it
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Fragment4Listener) {
            listener = context
        } else {
            throw IllegalArgumentException("Activity must implement Fragment5Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun switchToFragment5() {
        listener?.switchToFragment5()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.page4, null)
        DbCourt().getUserByEmail(SharedPreferencesHelper.getUserData(requireContext()).email) {
            user.value = it
        }
        user.observe(viewLifecycleOwner) {
            if (it.image.isNotEmpty())
                DbCourt().getUserImagine(it.email) {file ->
                    view.findViewById<ImageView>(R.id.profile_image).setImageURI(file.toUri())
                }
            view.findViewById<TextView>(R.id.profile_username).text = it.email
            view.findViewById<TextView>(R.id.profile_fullName).text = it.nickname.uppercase()
            view.findViewById<TextView>(R.id.profile_location).text = it.city
            view.findViewById<TextView>(R.id.profile_phone_number).text = it.phone
            view.findViewById<TextView>(R.id.profile_joined_date).text = it.birthdate.toString()
            rowInterest(it, view)
        }
        val btn: ImageView = view.findViewById(R.id.profile_image)
        btn.setOnClickListener {
            switchToNextView()
        }
        return view
    }

    private fun switchToNextView() {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .hide(this)
            .commit()
        switchToFragment5()
    }

    private fun rowInterest(user: User, view: View) {
        val layout = view.findViewById<LinearLayout>(R.id.ll_profile_interests)
        layout.removeAllViews()
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

