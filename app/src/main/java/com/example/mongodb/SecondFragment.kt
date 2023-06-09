package com.example.mongodb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mongodb.databinding.FragmentSecondBinding
import com.example.mongodb.model.Data
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SecondFragment : Fragment() {

    //realm using as a local database

    private val appId = BuildConfig.appId
    private val app = App.Companion.create(appId)
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //realm db config
        val config = RealmConfiguration.Builder(
            schema = setOf(
                Data::class
            )
        ).build()

        val realm = Realm.open(config)


        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        binding.submitBtn.setOnClickListener {
            val inputString1 = binding.editText.text.toString()
            val inputString2 = binding.editText2.text.toString()
            if (inputString1.isNotEmpty() && inputString2.isNotEmpty()) {

                //insert to db
                lifecycleScope.launch {
                    realm.write {
                        copyToRealm(Data().apply {
                            this.inputString1 = inputString1
                            this.inputString2 = inputString2
                        })

                    }
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "succeed", Toast.LENGTH_SHORT).show()
                        binding.editText.setText("")
                        binding.editText2.setText("")
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