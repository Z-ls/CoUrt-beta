package com.example.lab4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import it.polito.mad.court.NavHost
import it.polito.mad.court.SharedPreferencesHelper


class Fragment3 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.page3, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = SharedPreferencesHelper.getUserData(requireContext())
        val composeView = view.findViewById<ComposeView>(R.id.composeViewInvitations)
        composeView.setContent {
            NavHost(user = user)
        }
    }
}