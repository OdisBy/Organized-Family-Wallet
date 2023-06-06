package com.odisby.organizedfw.features.login.login_screen

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.odisby.organizedfw.core.data.repository.AuthRepository
import com.odisby.organizedfw.core.data.repository.AvatarRepository
import com.odisby.organizedfw.features.login.R
import com.odisby.organizedfw.features.login.launchFragmentInHiltContainer
import com.odisby.organizedfw.features.login.repositories.FakeAuthRepository
import com.odisby.organizedfw.features.login.repositories.FakeAvatarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
//@UninstallModules(RepositoryModule::class)
class LoginFragmentTest {

    /**
     * Not working because RepositoryModuleInterface is internal...
     * so I can't uninstallModules, them duplicate binding error occur
     */

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()

    }

    @Module
    @InstallIn(SingletonComponent::class) // 1
    object TestAppModule {
        @Provides
        fun provideAuthRepository(): AuthRepository { // 2
            return FakeAuthRepository()
        }

        @Provides
        fun providesAvatarRepository(): AvatarRepository {
            return FakeAvatarRepository()
        }
    }

    @Test
    fun clickRegisterAccountButton_navigateToSignUpFragment() {
        val navController = mock(NavController::class.java)
       launchFragmentInHiltContainer<LoginFragment> {
           Navigation.setViewNavController(requireView(), navController)
        }

        onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click())

        verify(navController).navigate(
            com.odisby.organizedfw.core.ui.R.id.action_login_to_sign_up
        )
    }
}