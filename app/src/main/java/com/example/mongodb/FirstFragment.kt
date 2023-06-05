package com.example.mongodb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mongodb.databinding.FragmentFirstBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val appId = BuildConfig.appId
    private val clientID =
        "890005986853-81e1gm0mfarnnn8pfjin1k907oicejq5.apps.googleusercontent.com"
    private val app = App.Companion.create(appId)
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleSignInResult(task)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.login1.setOnClickListener {
            lifecycleScope.launch {
                val user = app.login(Credentials.anonymous())
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                }
            }
        }

        binding.emailLogin.setOnClickListener {
            val email = binding.userNameETV.text.toString()
            val pass = binding.passNameETV.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = app.login(Credentials.emailPassword(email, pass))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }
                }
            }
        }

        binding.registerLogin.setOnClickListener {
            val email = binding.userNameETV.text.toString()
            val pass = binding.passNameETV.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                lifecycleScope.launch {
                    val user = app.emailPasswordAuth.registerUser(email, pass)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "successfully created", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }


        binding.googleSignInBtn.setOnClickListener {
            loginWithGoogle()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loginWithGoogle() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientID)
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val signInIntent: Intent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            if (completedTask.isSuccessful) {
                val account: GoogleSignInAccount? =
                    completedTask.getResult(ApiException::class.java)
                val token: String = account?.idToken!!
                Log.d("user","user : $token")

                lifecycleScope.launch {
                    val user = app.login(Credentials.google(token, GoogleAuthType.ID_TOKEN))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                    }
                }

            } else {
                Log.e("AUTH", "Google Auth failed: ${completedTask.exception}")
            }
        } catch (e: ApiException) {
            Log.e("AUTH", "Failed to authenticate using Google OAuth: " + e.message);
        }
    }
}