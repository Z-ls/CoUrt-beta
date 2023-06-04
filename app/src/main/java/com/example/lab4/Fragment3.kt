package com.example.lab4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import it.polito.mad.court.DbCourt
import it.polito.mad.court.NavHost
import it.polito.mad.court.dataclass.User
import kotlinx.coroutines.runBlocking


class Fragment3 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.page3, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var user: User?
        super.onViewCreated(view, savedInstanceState)
        runBlocking {
            DbCourt().getUserByEmail("user1@gmail.com") {
                user = it
                val composeView = view.findViewById<ComposeView>(R.id.composeViewInvitations)
                composeView.setContent {
                    NavHost(user = user ?: User())
                }
            }
        }
    }
}