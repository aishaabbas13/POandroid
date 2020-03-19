package com.cuseniordesign909.vpantry.primary_operations
import com.cuseniordesign909.vpantry.data_representations.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface NetworkConnection {
    @POST("signup")
    fun signUp(@Body body : SignUpandSignInCredentials) : Call<ResponseStatus>
    @POST("signin")
    fun signIn(@Body body : SignUpandSignInCredentials) : Call<ResponseStatus>
    @GET("users/")
    fun getUserInfo() : Call<SignUpandSignInCredentials>
    @PUT("users/")
    fun updateUserInfo(@Body body : SignUpandSignInCredentials) : Call<ResponseStatus>
    @PUT("users/changepassword")
    fun changePassword(@Body body : SignUpandSignInCredentials) : Call<ResponseStatus>
    @POST("groups/")
    fun createPantry(@Body body : Pantry) : Call<ResponseStatus>
    @GET("groups/")
    fun getPantries() : Call<ResponseStatus>
    @GET("groups/")
    fun getPantry(@Query("group_id") group_id: String) : Call<ResponseStatus>
    @GET("items/")
    fun getItems(@Query("group_id") group_id : String) : Call<ResponseStatus>
    @PUT("groups/")
    fun updatePantry(@Body body : Pantry) : Call<ResponseStatus>
    @POST("items/")
    fun addItem(@Body body : Item) : Call<Item>
    @PUT("items/")
    fun updateItem(@Body body : Item) : Call<Item>
    @DELETE("items/")
    fun deleteItem(@Query("item_id") item_id: String) : Call<ResponseStatus>
    @DELETE("users/")
    fun deleteUser() : Call<ResponseStatus>
}