package com.romankryvolapov.loramessenger.ui.priv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.romankryvolapov.loramessenger.databinding.FragmentPrivateMessagesBinding

class PrivateMessagesFragment: Fragment() {

  companion object {
    private const val TAG = "PrivateMessagesFragment"
    fun newInstance() = PrivateMessagesFragment()
  }

  private var binding: FragmentPrivateMessagesBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentPrivateMessagesBinding.inflate(inflater, container, false)
    return binding?.root
  }

}