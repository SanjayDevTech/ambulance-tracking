package com.sample.hospitaladmin.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.sample.common.BASE_URL
import com.sample.common.HOSPITALADMIN_SECRET
import com.sample.common.HOSPITALID_PREFS
import com.sample.common.snackbar
import com.sample.hospitaladmin.databinding.ActivityAmbulanceHomeBinding
import com.sample.hospitaladmin.databinding.ActivityAmbulanceSignupBinding
import com.sample.hospitaladmin.home.models.Ambulance
import com.sample.hospitaladmin.home.models.AmbulanceSignupPayload
import com.sample.hospitaladmin.retrofit.HospitalService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AmbulanceSignup : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val hospitalService = retrofit.create(HospitalService::class.java)
    private lateinit var binding:ActivityAmbulanceSignupBinding
    private lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityAmbulanceSignupBinding.inflate(layoutInflater)
        sharedPrefs = getSharedPreferences(HOSPITALADMIN_SECRET, Context.MODE_PRIVATE)
        setContentView(binding.root)
        binding.addAmbulanceButton.setOnClickListener{
            var driverName =binding.driverName.text.toString()
            var driverMobile = binding.driverMobile.text.toString()
            var password = binding.password.text.toString()
            var isAvailable = binding.isAvailable.isChecked
            var vehicleNo = binding.vehicleNo.text.toString()
            var hospital = sharedPrefs.getString(HOSPITALID_PREFS,"") as String
            var validationMessage = validateAmbulanceSignupDetails(driverName,driverMobile,password,vehicleNo)

            if(validationMessage == "")
            {
                val ambulance = Ambulance(driverName,driverMobile,password,vehicleNo,isAvailable,hospital)
                val payload = AmbulanceSignupPayload(ambulance)
                try {
                    lifecycleScope.launch(Dispatchers.IO){

                        val ambulanceSignupResponse = hospitalService.signUpAmbulance(payload)
                        if(ambulanceSignupResponse.hasError)
                        {
                            withContext(Dispatchers.Main)
                            {
                                binding.root.snackbar("Error occured while signing up","DISMISS")
                            }
                        }
                        else
                        {
                            withContext(Dispatchers.Main)
                            {
                                val ambulanceHomeIntent =
                                    Intent(binding.root.context, HospitalHomeScreen::class.java)
                                startActivity(ambulanceHomeIntent)
                            }
                        }
                    }
                }
                catch(e:Exception) {
                    e.printStackTrace()
                    binding.root.snackbar("Exception Ocucured :(","DISMISS")
                }

            }
            else
            {
                binding.root.snackbar(validationMessage,"DISMISS")
            }

        }
    }

    private fun validateAmbulanceSignupDetails(driverName:String,driverMobile:String,password:String,vehicleNo:String):String {
        if (driverName.length < 3) {
            return "name shoudl be atleast 3 chars"
        }
        if (driverMobile.length != 10) {
            return "Mobile no should be exactly 10 chars"
        }

        if (password.length < 8) {
            return "password must be greater than 8 chars"
        }
        if (vehicleNo.length < 5) {
            return "vehicle number should be atleast 3 chars"
        }

        return ""
    }
}