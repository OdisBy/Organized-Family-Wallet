package com.odisby.organizedfw.features.login.utils

import androidx.core.util.PatternsCompat

object LoginUtil {
    /**
     * The input is not valid if...
     * ...the local part length < 3
     */
    fun validateEmailInput(
        email: String
    ): InputResource {
        if(email.isEmpty()){
            return InputResource.Error.IsEmpty
        }

        val parts = email.split("@")
        if (parts.size != 2) {
            return InputResource.Error.IsInvalid
        }
        if (parts[0].length < 3) {
            return InputResource.Error.lessCharacters(3)
        }

        if(!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
            return InputResource.Error.IsInvalid
        }
        return InputResource.Success
    }

    /**
     * The input is not valid if...
     * ...the password length < 6
     */
    fun validatePasswordInput(
        password: String
    ): InputResource {
        if(password.isEmpty()){
            return InputResource.Error.IsEmpty
        }
        if(password.length < 6){
            return InputResource.Error.lessCharacters(6)
        }
        return InputResource.Success
    }


    /**
     * The input is not valid if...
     * ...the password are not the same
     */
    fun validateConfirmPasswordInput(
        password: String,
        confirmPassword: String
    ): InputResource {
        if(password.contentEquals(confirmPassword)){
            return InputResource.Success
        }
        return InputResource.Error.customError("Senhas não conferem")
    }
}