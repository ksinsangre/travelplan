package com.nelsito.travelplan

import com.google.firebase.auth.FirebaseUser
import com.nelsito.travelplan.domain.UserRepository
import com.nelsito.travelplan.domain.users.*
import com.nelsito.travelplan.user.UserNavigationView
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.mockito.Mockito.*

class UserNavigationPresenterShould {

    @Test
    fun `open login when user is anonymous`() = runBlockingTest {
        //given
        val userNavigationView = mock(UserNavigationView::class.java)
        val userRepository = mock(UserRepository::class.java)
        `when`(userRepository.loadUser()).thenReturn(AnonymousUser())
        val presenter = UserNavigationPresenter(
            userNavigationView,
            userRepository
        )
        //when
        presenter.startNavigation()
        //then
        verify(userNavigationView).openLogin()
    }

    @Test
    fun `open user list when user is admin`() = runBlockingTest {
        //given
        val userNavigationView = mock(UserNavigationView::class.java)
        val userRepository = mock(UserRepository::class.java)
        `when`(userRepository.loadUser()).thenReturn(Admin(mock(FirebaseUser::class.java)))
        val presenter = UserNavigationPresenter(
            userNavigationView,
            userRepository
        )
        //when
        presenter.startNavigation()
        //then
        verify(userNavigationView).openUserList()
    }

    @Test
    fun `open user list when user is manager`() = runBlockingTest {
        //given
        val userNavigationView = mock(UserNavigationView::class.java)
        val userRepository = mock(UserRepository::class.java)
        `when`(userRepository.loadUser()).thenReturn(TravelManager(mock(FirebaseUser::class.java)))
        val presenter = UserNavigationPresenter(
            userNavigationView,
            userRepository
        )
        //when
        presenter.startNavigation()
        //then
        verify(userNavigationView).openUserList()
    }

    @Test
    fun `open trip list when is a regular user`() = runBlockingTest {
        //given
        val userNavigationView = mock(UserNavigationView::class.java)
        val userRepository = mock(UserRepository::class.java)
        `when`(userRepository.loadUser()).thenReturn(VerifiedUser(mock(FirebaseUser::class.java)))
        val presenter = UserNavigationPresenter(
            userNavigationView,
            userRepository
        )
        //when
        presenter.startNavigation()
        //then
        verify(userNavigationView).openTripList()
    }

    @Test
    fun `open wait for verification when email is not verified`() = runBlockingTest {
        //given
        val userNavigationView = mock(UserNavigationView::class.java)
        val userRepository = mock(UserRepository::class.java)
        `when`(userRepository.loadUser()).thenReturn(NotVerifiedUser(mock(FirebaseUser::class.java)))
        val presenter = UserNavigationPresenter(
            userNavigationView,
            userRepository
        )
        //when
        presenter.startNavigation()
        //then
        verify(userNavigationView).openWaitForVerificationScreen()
    }
}