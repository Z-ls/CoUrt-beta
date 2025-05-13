package com.example.lab4


import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import it.polito.mad.court.DbCourt
import it.polito.mad.court.SharedPreferencesHelper
import it.polito.mad.court.dataclass.User


class Fragment5 : Fragment() {

    private var listener: Fragment5Listener? = null
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val profilePic = requireView().findViewById<ImageView>(R.id.profile_image)
                profilePic?.setImageURI(imageUri)
            }
        }
    private var imageUri: Uri = Uri.EMPTY

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

    private fun switchToFragment4() {
        listener?.switchToFragment4()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val user = SharedPreferencesHelper.getUserData(requireContext())
        val view: View = inflater.inflate(R.layout.page4_edit, null)!!
        if (user.image.isNotEmpty())
            view.findViewById<ImageView>(R.id.profile_image).setImageURI(user.image.toUri())
        view.findViewById<TextView>(R.id.profile_username).text = user.email
        view.findViewById<EditText>(R.id.profile_fullName).setText(user.nickname)
        view.findViewById<EditText>(R.id.profile_location).setText(user.city)
        view.findViewById<EditText>(R.id.profile_phone_number).setText(user.phone)

        interestSelector(user, view)
        val btnSave = view.findViewById<Button>(R.id.edit_save_button)
        btnSave?.setOnClickListener {
            onSave(user, view)
        }
        val btnCancel = view.findViewById<Button>(R.id.edit_cancel_button)
        btnCancel?.setOnClickListener {
            switchToNextView()
        }
        val btnEditPic = view.findViewById<ImageButton>(R.id.image_button)
        btnEditPic.setOnClickListener {
            registerForContextMenu(btnEditPic)
            requireActivity().openContextMenu(btnEditPic)
        }
        val profilePic = view.findViewById<ImageView>(R.id.profile_image)
        cameraActivityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    profilePic?.setImageURI(imageUri)
                }
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
        user.email = user.email
        user.image = if (imageUri !== Uri.EMPTY) imageUri.toString() else user.image
        user.nickname = view.findViewById<EditText>(R.id.profile_fullName).text.toString()
        user.city = view.findViewById<EditText>(R.id.profile_location).text.toString()
        user.phone = view.findViewById<EditText>(R.id.profile_phone_number).text.toString()
        user.sportList = getSportList(view)
        DbCourt().updateUser(user) {
            switchToNextView()
        }
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
                val skillLevelIndex =
                    user.sportList.firstOrNull { pair -> pair.first == it }?.second ?: 0
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


    private fun checkPerm(cb: () -> Any?) {
        val cameraPermission = android.Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                cameraPermission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(
                cameraPermission
            )
            requestPermissions(permission, 112)
        } else {
            cb()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(cameraIntent)
    }


    private val galleryActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        {
            val profilePic = view?.findViewById<ImageView>(R.id.profile_image)
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                imageUri = it.data?.data!!
                profilePic?.setImageURI(imageUri)
            }
        }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
//        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.photo_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.take_photo -> {
                checkPerm { openCamera() }
            }

            R.id.choose_from_gallery -> {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryActivityResultLauncher.launch(galleryIntent)
            }
        }
        return super.onContextItemSelected(item)
    }
}

