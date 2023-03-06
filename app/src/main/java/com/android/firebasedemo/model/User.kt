package com.android.firebasedemo.model

import java.io.Serializable

class User : Serializable {
    var uid: String? = null
    var name: String? = null
    var email: String? = null

    constructor() {}
    constructor(uid: String?, name: String?, email: String?) {
        this.uid = uid
        this.name = name
        this.email = email
    }
}