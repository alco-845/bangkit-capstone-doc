package com.fashionism.fashionismuserapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.bumptech.glide.Glide
import com.fashionism.fashionismuserapp.R
import com.fashionism.fashionismuserapp.data.db.Product
import com.fashionism.fashionismuserapp.databinding.ActivityDetailFashionBinding
import com.google.android.material.button.MaterialButton
import kotlin.math.roundToInt

class DetailFashionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFashionBinding
    private var isExpanded = false
    private lateinit var gestureDetector: GestureDetectorCompat
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFashionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val data = intent.getParcelableExtra<Product>(EXTRA_FASHION_ITEM)

        binding.tvDetailNameProduct.text = data?.name
        binding.tvDetailPriceProduct.text = data?.price
        binding.tvDescriptionProduct.text = data?.description
        Glide.with(this)
            .load(data?.product_image)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.ivProductImage)

        gestureDetector = GestureDetectorCompat(this, GestureListener())
        setupClickAndTouch()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickAndTouch() {
        binding.ccDetailFashion.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        binding.minimizeAndFullInformationOutfit.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        binding.btnFavoriteProduct.setOnClickListener {
            isFavorite = !isFavorite
            animateFavoriteIcon(binding.btnFavoriteProduct)
        }

        binding.backButtonDetailProduct.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slidefromleft_in, R.anim.slidefromleft_out)
        }

    }


    private fun animateFavoriteIcon(button: MaterialButton) {
        val transition: Transition = TransitionInflater.from(this)
            .inflateTransition(R.transition.favorite_icon_transition)

        val iconResId = if (isFavorite) {
            R.drawable.baseline_favorite_24
        } else {
            R.drawable.baseline_favorite_border_24
        }

        TransitionManager.beginDelayedTransition(button.parent as ViewGroup, transition)
        button.setIconResource(iconResId)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private var initialY = 0f

        override fun onDown(e: MotionEvent): Boolean {
            initialY = e.y
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (isExpanded) {
                // Mengubah tinggi cc_detail_fashion menjadi wrap_content
                collapseView(binding.ccDetailFashion)
                binding.btnMoreDetailProduct.visibility = View.GONE
                binding.backButtonDetailProduct.visibility = View.GONE
                isExpanded = false
            } else {
                // Mengubah tinggi cc_detail_fashion menjadi match_parent
                expandView(binding.ccDetailFashion)
                binding.btnMoreDetailProduct.visibility = View.VISIBLE
                binding.backButtonDetailProduct.visibility = View.VISIBLE
                isExpanded = true
            }
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val diffY = e2.y - initialY

            if (diffY > SWIPE_THRESHOLD) {
                // Swipe down
                if (isExpanded) {
                    // Perform actions for collapsed state
                    expandView(binding.ccDetailFashion)
                    binding.btnMoreDetailProduct.visibility = View.VISIBLE
                    binding.backButtonDetailProduct.visibility = View.VISIBLE
                    isExpanded = false
                }
            } else if (diffY < -SWIPE_THRESHOLD) {
                // Swipe up
                if (!isExpanded) {
                    // Perform actions for expanded state
                    collapseView(binding.ccDetailFashion)
                    binding.btnMoreDetailProduct.visibility = View.GONE
                    binding.backButtonDetailProduct.visibility = View.GONE
                    isExpanded = true
                }
            }
            return true
        }
    }

    private fun expandView(view: View) {
        view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        if (view.layoutParams is MarginLayoutParams) {
            val params = view.layoutParams as MarginLayoutParams
            params.topMargin = 0
            view.layoutParams = params
        }
        animateView(view)
    }

    private fun collapseView(view: View) {
        view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        if (view.layoutParams is MarginLayoutParams) {
            val params = view.layoutParams as MarginLayoutParams
            params.topMargin = 0
            view.layoutParams = params
        }
        animateView(view)
    }

    private fun animateView(view: View) {
        TransitionManager.beginDelayedTransition(view.parent as ViewGroup, AutoTransition())
        view.requestLayout()
    }

    @Suppress("SameParameterValue")
    private fun convertDpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).roundToInt()
    }

    companion object {
        const val EXTRA_FASHION_ITEM = "extra_fashion_item"
        private const val SWIPE_THRESHOLD = 100
    }
}