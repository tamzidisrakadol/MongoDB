package com.example.mongodb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mongodb.databinding.FragmentFirstBinding
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    val appId = BuildConfig.appId

    
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = App.Companion.create(appId)


        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.login1.setOnClickListener {
            lifecycleScope.launch {
                val user = app.login(Credentials.anonymous())
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                }
            }
        }

        binding.emailLogin.setOnClickListener {
            val email = binding.userNameETV.text.toString()
            val pass = binding.passNameETV.text.toString()
           if(email.isNotEmpty() && pass.isNotEmpty()){
               lifecycleScope.launch {
                   val user = app.login(Credentials.emailPassword(email,pass))
                   withContext(Dispatchers.Main){
                       Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                       findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                   }
               }
           }
        }

        binding.registerLogin.setOnClickListener {
            val email = binding.userNameETV.text.toString()
            val pass = binding.passNameETV.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()){
                lifecycleScope.launch {
                    val user = app.emailPasswordAuth.registerUser(email,pass)
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "successfully created", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}