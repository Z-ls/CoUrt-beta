package com.example.lab4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.Nullable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import it.polito.mad.court.PageViewReservations
import it.polito.mad.court.ViewReservations


class Fragment1 : Fragment() {
    var btn1: Button? = null
    var btn2: Button? = null
    var btn3: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.page1, null)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view.findViewById<ComposeView>(R.id.composeView)
        composeView.setContent {
            PageViewReservations()
        }
    }
}