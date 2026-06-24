package com.example.pb.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    @Mock
    private lateinit var authResult: AuthResult

    @Mock
    private lateinit var task: Task<AuthResult>

    private lateinit var authViewModel: AuthViewModel

    @Mock
    private lateinit var authStateObserver: Observer<AuthViewModel.AuthState>

    @Mock
    private lateinit var currentUserObserver: Observer<Any?>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
        authViewModel = AuthViewModel(firebaseAuth)
        authViewModel.authState.observeForever(authStateObserver)
        authViewModel.currentUser.observeForever(currentUserObserver)
        clearInvocations(authStateObserver, currentUserObserver)
    }

    @Test
    fun testLoginSuccess() {
        // Given
        val email = "test@example.com"
        val password = "password123"
        whenever(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(task)
        whenever(task.isSuccessful).thenReturn(true)
        whenever(task.result).thenReturn(authResult)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        whenever(task.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(task)
            task
        }

        // When
        authViewModel.loginWithEmail(email, password)

        // Then
        verify(authStateObserver).onChanged(AuthViewModel.AuthState.Loading)
        verify(authStateObserver).onChanged(AuthViewModel.AuthState.Success("Bienvenido"))
        assertEquals(firebaseUser, authViewModel.currentUser.value)
    }

    @Test
    fun testLoginFailure() {
        // Given
        val email = "test@example.com"
        val password = "wrong_password"
        val exceptionMessage = "Credenciales incorrectas"
        val mockException = Exception(exceptionMessage)

        whenever(firebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(task)
        whenever(task.isSuccessful).thenReturn(false)
        whenever(task.exception).thenReturn(mockException)

        whenever(task.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(task)
            task
        }

        // When
        authViewModel.loginWithEmail(email, password)

        // Then
        verify(authStateObserver).onChanged(AuthViewModel.AuthState.Loading)
        verify(authStateObserver).onChanged(AuthViewModel.AuthState.Error(exceptionMessage))
    }

    @Test
    fun testRegisterSuccess() {
        // Given
        val email = "new@example.com"
        val password = "password123"
        whenever(firebaseAuth.createUserWithEmailAndPassword(email, password)).thenReturn(task)
        whenever(task.isSuccessful).thenReturn(true)
        whenever(task.result).thenReturn(authResult)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        whenever(task.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            listener.onComplete(task)
            task
        }

        // When
        authViewModel.registerWithEmail(email, password)

        // Then
        verify(authStateObserver).onChanged(AuthViewModel.AuthState.Loading)
        verify(authStateObserver).onChanged(AuthViewModel.AuthState.Success("Cuenta creada correctamente"))
        assertEquals(firebaseUser, authViewModel.currentUser.value)
    }

    @Test
    fun testLogout() {
        // When
        authViewModel.logout()

        // Then
        verify(firebaseAuth).signOut()
        assertNull(authViewModel.currentUser.value)
        verify(authStateObserver).onChanged(AuthViewModel.AuthState.Idle)
    }

    @Test
    fun testIsUserLoggedInTrue() {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)

        // When
        val isLoggedIn = authViewModel.isUserLoggedIn()

        // Then
        assertTrue(isLoggedIn)
    }

    @Test
    fun testIsUserLoggedInFalse() {
        // Given
        whenever(firebaseAuth.currentUser).thenReturn(null)

        // When
        val isLoggedIn = authViewModel.isUserLoggedIn()

        // Then
        assertFalse(isLoggedIn)
    }
}
