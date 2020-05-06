package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.cuseniordesign909.vpantry.R
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.data_representations.SignUpandSignInCredentials
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class UserInfo : Fragment(), View.OnClickListener {
    private var userInfo : View? = null
    private var userInfoEmail : TextView? = null
    private var userInfoFirstName : TextView? = null
    private var userInfoLastName : TextView? = null
    private var userInfoNickName : TextView? = null
    private var editUserInfoButton : Button? = null
    private var info : SignUpandSignInCredentials? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userInfo = inflater.inflate(R.layout.userinfo, container, false)
        initializeTextViews()
        initializeButtons()
        info = SignUpandSignInCredentials("", "", "", "", "")
        CoroutineScope(Dispatchers.IO).async {
            connect()
        }
        return userInfo
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu?.findItem(R.id.app_bar_create_pantry).isVisible = false
        menu?.findItem(R.id.app_bar_sign_out).isVisible = false
        menu?.findItem(R.id.app_bar_user_info).isVisible = false
    }
    private fun connect(){
            var api = PantriesSignedInActivity.retrofit?.create(NetworkConnection::class.java)
            var call = api?.getUserInfo()
            call?.enqueue(object : Callback<SignUpandSignInCredentials> {
                override fun onResponse(
                    call: Call<SignUpandSignInCredentials>,
                    response: Response<SignUpandSignInCredentials>
                ) {
                    info = response.body()
                    setTextViews(info)
                }

                override fun onFailure(call: Call<SignUpandSignInCredentials>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }
    private fun setTextViews(info : SignUpandSignInCredentials?){
        userInfoEmail?.text = info?.email
        userInfoFirstName?.text = info?.given_name
        if(info?.family_name == "")
            userInfoLastName?.text = "N/A"
        else
            userInfoLastName?.text = info?.family_name
        if(info?.nickname == "")
            userInfoNickName?.text = "N/A"
        else
            userInfoNickName?.text = info?.nickname
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.title = "User Info"
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.editUserInfoButton -> {
                var updateUserInfo = UpdateUserInfo()
                var b = Bundle()
                b.putString("given_name", info?.given_name as String)
                b.putString("family_name", info?.family_name as String)
                b.putString("nickname", info?.nickname as String)
                b.putString("email", info?.email as String)
                updateUserInfo?.arguments = b
                updateUserInfo?.setTargetFragment(this, 1)
                activity!!.supportFragmentManager.beginTransaction().add(R.id.signedInFragmentManager, updateUserInfo, "UpdateUserInfo").addToBackStack("UpdateUserInfo").commit()
            }
        }
    }
    private fun initializeButtons(){
        editUserInfoButton = userInfo?.findViewById(R.id.editUserInfoButton)
        editUserInfoButton?.setOnClickListener(this)
    }
    override fun onResume(){
        super.onResume()
        (activity as AppCompatActivity)?.supportActionBar?.title = "User Info"
        CoroutineScope(Dispatchers.IO).async {
            connect()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        (activity as AppCompatActivity)?.supportActionBar?.title = "User Info"
        if(requestCode == 1){
            info?.family_name = data?.getStringExtra("family_name") as String
            info?.given_name = data?.getStringExtra("given_name") as String
            info?.nickname = data?.getStringExtra("nickname") as String
            info?.email = data?.getStringExtra("email") as String
            setTextViews(info)
        }
    }
    private fun initializeTextViews(){
        userInfoEmail = userInfo?.findViewById(R.id.userInfoEmail)
        userInfoFirstName = userInfo?.findViewById(R.id.userInfoFirstName)
        userInfoLastName = userInfo?.findViewById(R.id.userInfoLastName)
        userInfoNickName = userInfo?.findViewById(R.id.userInfoNickName)
    }
}