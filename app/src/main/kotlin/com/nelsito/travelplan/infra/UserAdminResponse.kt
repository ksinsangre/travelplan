package com.nelsito.travelplan.infra

data class UserAdminResponse(val users: List<UserResponse>)

data class UserResponse(val uid: String, val email:String, val displayName: String, val role: String, val photoUrl: String? = "", val disabled: Boolean)

data class AddUserRequest(val email:String, val displayName: String, val role: String, val password: String)
