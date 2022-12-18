package com.romankryvolapov.loramessenger.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
  private val fragments: List<Fragment>,
  fragmentManager: FragmentManager,
  lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
  override fun getItemCount() = fragments.size
  override fun createFragment(position: Int) = fragments[position]
}