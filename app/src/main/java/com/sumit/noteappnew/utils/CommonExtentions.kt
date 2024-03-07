package com.sumit.noteappnew.utils

import android.content.Context
import android.content.ContextWrapper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun loge(message: () -> String) {
    Timber.e(message())
}

fun Context.toast(toastLength: Int = Toast.LENGTH_SHORT, message: () -> String) {
    Toast.makeText(this, message(), toastLength).show()
}

fun Context.getActivity(): AppCompatActivity? {
    var currentContext = this

    // Loop through the context wrappers to find the actual Activity
    while (currentContext is ContextWrapper) {
        if (currentContext is AppCompatActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }

    return null // If no Activity is found
}

fun ComponentActivity.addMenuProvider(
    onSelected: (MenuItem) -> Boolean,
    @MenuRes menuRes: Int,
    state: Lifecycle.State = Lifecycle.State.RESUMED
) {
    addMenuProvider(
        object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(menuRes, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return onSelected(menuItem)
            }
        }, this, state
    )
}

fun Fragment.addMenuProvider(
    mContext: Context,
    @MenuRes menuRes: Int,
    state: Lifecycle.State = Lifecycle.State.RESUMED,
    onMenuCreate: (Menu, MenuInflater) -> Unit,
    onSelected: (MenuItem) -> Boolean
) {
    mContext.getActivity()?.addMenuProvider(
        object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(menuRes, menu)
                onMenuCreate(menu, menuInflater)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return onSelected(menuItem)
            }
        }, viewLifecycleOwner, state
    )
}

fun <T> T.modelToJson(): String {
    // Use Gson to serialize the model object into a JSON string
    return Gson().toJson(this)
}

inline fun <reified T> String.jsonToModel(): T {
    // Use Gson to deserialize the JSON string into the specified model object type T
    return Gson().fromJson(this, T::class.java)
}

fun Long?.millisInDate(): String? {
    if (this == null) return null

    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("hh mm a | MMM d, yyyy", Locale.getDefault())
    return simpleDateFormat.format(date)
}

fun View.snackBar(
    message: String,
    snackBarLength: Int = Snackbar.LENGTH_SHORT,
    block: Snackbar.() -> Unit
) {
    Snackbar.make(
        this,
        message,
        snackBarLength
    ).apply {
        block(this)
    }
}