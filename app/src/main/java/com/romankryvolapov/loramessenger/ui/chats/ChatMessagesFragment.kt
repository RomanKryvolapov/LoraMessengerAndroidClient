package com.romankryvolapov.loramessenger.ui.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.romankryvolapov.loramessenger.databinding.FragmentChatMessagesBinding

class ChatMessagesFragment : Fragment() {

  companion object {
    private const val TAG = "ChatMessagesFragment"
    fun newInstance() = ChatMessagesFragment()
  }

  private var binding: FragmentChatMessagesBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentChatMessagesBinding.inflate(inflater, container, false)
    return binding?.root
  }

}