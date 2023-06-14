package com.ruliam.organizedfw.features.login.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginUtilTest {

    @Test
    fun `Correct email input returns Success`(){
        val result = LoginUtil.validateEmailInput("unit.test@test.com")
        assertThat(result).isEqualTo(InputResource.Success)
    }

    @Test
    fun `Empty email input returns IsEmpty`(){
        val result = LoginUtil.validateEmailInput("")
        assertThat(result).isEqualTo(InputResource.Error.IsEmpty)
    }

    @Test
    fun `2 char username part email input returns lessCharacters`(){
        val result = LoginUtil.validateEmailInput("un@test.com")
        assertThat(result).isEqualTo(InputResource.Error.lessCharacters(3))
    }

    @Test
    fun `3 char local part email input returns Success`(){
        val result = LoginUtil.validateEmailInput("uni@test.com")
        assertThat(result).isEqualTo(InputResource.Success)
    }

    @Test
    fun `Incorrect email input returns IsInvalid`(){
        val result = LoginUtil.validateEmailInput("unit.test.com")
        assertThat(result).isEqualTo(InputResource.Error.IsInvalid)
    }

    @Test
    fun `Correct password input returns Success`(){
        val result = LoginUtil.validatePasswordInput("randomPassword")
        assertThat(result).isEqualTo(InputResource.Success)
    }

    @Test
    fun `Empty password input returns IsEmpty`(){
        val result = LoginUtil.validatePasswordInput("")
        assertThat(result).isEqualTo(InputResource.Error.IsEmpty)
    }

    @Test
    fun `if password less than 6 returns lessCharacters`(){
        val result = LoginUtil.validatePasswordInput("passw")
        assertThat(result).isEqualTo(InputResource.Error.lessCharacters(6))
    }

    @Test
    fun `Confirm password match password returns Success`(){
        val result = LoginUtil.validateConfirmPasswordInput("password", "password")
        assertThat(result).isEqualTo(InputResource.Success)
    }

    @Test
    fun `if confirm password doesn't match password returns customError`(){
        val result = LoginUtil.validateConfirmPasswordInput("password", "passw0rd")
        assertThat(result).isEqualTo(InputResource.Error.customError("Senhas não conferem"))
    }
}