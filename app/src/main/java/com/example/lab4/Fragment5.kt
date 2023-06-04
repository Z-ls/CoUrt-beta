package com.example.lab4


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import it.polito.mad.court.DbCourt
import it.polito.mad.court.dataclass.User


class Fragment5 : Fragment() {

    private var listener: Fragment5Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Fragment5Listener) {
            listener = context
        } else {
            throw IllegalArgumentException("Activity must implement Fragment5Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // Call this method when you want to switch to Fragment1
    private fun switchToFragment4() {
        listener?.switchToFragment4()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.page4_edit, container, false)
        val user: MutableLiveData<User> = MutableLiveData()
        user.observe(viewLifecycleOwner) {
            view.findViewById<EditText>(R.id.profile_fullname).setText(it.nickname)
            view.findViewById<EditText>(R.id.profile_location).setText(it.city)
            view.findViewById<EditText>(R.id.profile_phone_number).setText(it.phone)
            interestSelector(it, view)
        }
        DbCourt().getUserByEmail("test@gmail.com") {
            user.value = it
        }
        val btnSave = view.findViewById<Button>(R.id.edit_save_button)
        btnSave?.setOnClickListener {
            onSave(User(), view)
            switchToNextView()
        }
        val btnCancel = view.findViewById<Button>(R.id.edit_cancel_button)
        btnCancel?.setOnClickListener {
            switchToNextView()
        }
        return view
    }

    private fun switchToNextView() {
        parentFragmentManager.beginTransaction()
            .addToBackStack(null)
            .hide(this)
            .commit()
        switchToFragment4()
    }

    private fun onSave(user: User, view: View) {
        user.email = "test@gmail.com"
        user.nickname = view.findViewById<EditText>(R.id.profile_fullname).text.toString()
        user.city = view.findViewById<EditText>(R.id.profile_location).text.toString()
        user.phone = view.findViewById<EditText>(R.id.profile_phone_number).text.toString()
        user.sportList = getSportList(view)

        DbCourt().updateUser(user)
    }

    private fun getSportList(view: View): List<Pair<String, Int>> {
        val llProfileInterests = view.findViewById<LinearLayout>(R.id.ll_profile_interests)
        val sportList = mutableListOf<Pair<String, Int>>()
        for (i in 0 until llProfileInterests.childCount) {
            val llInterestItem = llProfileInterests.getChildAt(i) as LinearLayout
            val tvInterest = llInterestItem.getChildAt(0) as TextView
            val spSkillLevel = llInterestItem.getChildAt(1) as Spinner
            sportList.add(Pair(tvInterest.text.toString(), spSkillLevel.selectedItemPosition))
        }
        return sportList
    }

    private fun interestSelector(user: User, view: View) {
        val llProfileInterests = view.findViewById<LinearLayout>(R.id.ll_profile_interests)
        llProfileInterests.orientation = LinearLayout.VERTICAL
        listOf("Basketball", "Football", "Tennis", "Badminton")
            .map {
                val llInterestItem = LinearLayout(view.context)
                llInterestItem.orientation = LinearLayout.HORIZONTAL
                val tvInterest = TextView(view.context)
                tvInterest.text = it
                var skillLvl: Int
                val spSkillLevel = Spinner(view.context)
                spSkillLevel.adapter = ArrayAdapter(
                    view.context,
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Not Interested", "Beginner", "Intermediate", "Advanced")
                )
                val skillLevelIndex = user.sportList.firstOrNull { pair -> pair.first == it }?.second ?: 0
                spSkillLevel.setSelection(skillLevelIndex)
                spSkillLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        skillLvl = position
                        if (user.sportList.any { p -> p.first == it }) {
                            user.sportList = (user.sportList.filter { s -> s.first != it })
                            user.sportList = (user.sportList.plus(Pair(it, skillLvl)))
                        } else if (position != 0) {
                            user.sportList = (user.sportList.plus(Pair(it, skillLvl)))
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                llInterestItem.addView(tvInterest)
                llInterestItem.addView(spSkillLevel)
                llProfileInterests.addView(llInterestItem)
            }
    }
}

