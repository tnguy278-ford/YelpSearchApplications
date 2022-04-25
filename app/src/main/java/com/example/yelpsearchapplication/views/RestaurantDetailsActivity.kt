package com.example.yelpsearchapplication.views

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.yelpsearchapplication.configs.KeyConfig
import com.example.yelpsearchapplication.databinding.ActivityRestaurantDetailsBinding
import com.example.yelpsearchapplication.viewmodels.RestaurantDetailsViewModel
import com.squareup.picasso.Picasso

class RestaurantDetailsActivity : AppCompatActivity() {
    lateinit var activityRestaurantDetailsBinding: ActivityRestaurantDetailsBinding
    lateinit var viewModel: RestaurantDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRestaurantDetailsBinding = ActivityRestaurantDetailsBinding.inflate(layoutInflater)
        setContentView(activityRestaurantDetailsBinding.root)
        setupViewModel()
        val id = intent.getStringExtra(KeyConfig.ID)
        viewModel.getRestaurantDetails(id?: "")
        setupObserver()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(RestaurantDetailsViewModel::class.java)
    }

    private fun setupObserver() {
        viewModel.restaurantDetailsLiveData.observe(this) { business ->
            Picasso.get()
                .load(business.image_url)
                .fit()
                .into(activityRestaurantDetailsBinding.restaurantDetailsImageIv)

            activityRestaurantDetailsBinding.restaurantDetailsNameTv.text = business.name
            activityRestaurantDetailsBinding.restaurantDetailsRatingTv.text = "Rating: ${business.rating}"
            var stringCategories = "Categories: "
            for (category in business.categories) {
                stringCategories += " ${category.title}"
            }
            activityRestaurantDetailsBinding.restaurantDetailsCategoriesTv.text = stringCategories
            activityRestaurantDetailsBinding.restaurantDetailsPriceTv.text = business.price
        }

        viewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}