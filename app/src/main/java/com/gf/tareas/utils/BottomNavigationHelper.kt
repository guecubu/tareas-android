package com.gf.tareas.utils

import android.app.Activity
import android.content.Intent
import com.gf.tareas.R
import com.gf.tareas.activities.CategoriesActivity
import com.gf.tareas.activities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNavigationHelper {

    fun setupBottomNavigation(
            activity: Activity, bottomNavigationView: BottomNavigationView, activeItemId: Int
                             ) {
        bottomNavigationView.selectedItemId = activeItemId

        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == activeItemId) return@setOnItemSelectedListener true

            when (item.itemId) {
                R.id.navigation_tasks -> {
                    if (activity !is MainActivity) {
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        activity.startActivity(intent)
                        activity.finish()
                    }
                    true
                }

                R.id.navigation_categories -> {
                    if (activity !is CategoriesActivity) {
                        val intent = Intent(activity, CategoriesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        activity.startActivity(intent)
                        activity.finish()
                    }
                    true
                }

                else -> false
            }
        }
    }
}
