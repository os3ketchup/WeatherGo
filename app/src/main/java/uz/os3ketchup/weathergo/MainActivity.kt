package uz.os3ketchup.weathergo

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import uz.os3ketchup.weathergo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       supportActionBar?.hide()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.main_container).navigateUp()
    }
}