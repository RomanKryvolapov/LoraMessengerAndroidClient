package com.romankryvolapov.loramessenger.ui.raw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.romankryvolapov.loramessenger.databinding.FragmentRawMessagesBinding

class RawMessagesFragment : Fragment() {

  companion object {
    private const val TAG = "RawMessagesFragment"
    fun newInstance() = RawMessagesFragment()
  }

  private var binding: FragmentRawMessagesBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentRawMessagesBinding.inflate(inflater, container, false)
    return binding?.root
  }

}