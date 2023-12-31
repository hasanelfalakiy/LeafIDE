package io.github.caimucheng.leaf.ide.util

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.github.caimucheng.leaf.ide.R

fun Fragment.findGlobalNavController() = requireActivity()
    .supportFragmentManager
    .findFragmentById(R.id.fragment_global)!!
    .findNavController()

fun Fragment.findMainFragmentNavController() = childFragmentManager
    .findFragmentById(R.id.fragment_main)!!
    .findNavController()