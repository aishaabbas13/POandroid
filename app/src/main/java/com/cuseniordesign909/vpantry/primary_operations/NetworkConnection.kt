package com.cuseniordesign909.vpantry.primary_operations
import com.cuseniordesign909.vpantry.data_representations.SignUpCredentials
import com.cuseniordesign909.vpantry.data_representations.SignUpStatus
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkConnection {
    @POST("signup")
    fun signUp(@Body body : SignUpCredentials) : Call<SignUpStatus>
}