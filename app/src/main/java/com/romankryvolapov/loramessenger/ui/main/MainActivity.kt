package com.romankryvolapov.loramessenger.ui.main

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.romankryvolapov.loramessenger.R
import com.romankryvolapov.loramessenger.databinding.ActivityMainBinding
import com.romankryvolapov.loramessenger.ui.chats.ChatMessagesFragment
import com.romankryvolapov.loramessenger.ui.priv.PrivateMessagesFragment
import com.romankryvolapov.loramessenger.ui.raw.RawMessagesFragment
import com.romankryvolapov.loramessenger.ui.settings.PermissionRequestListener
import com.romankryvolapov.loramessenger.ui.settings.SettingsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent {

  companion object {
    enum class Tabs(val value: Int, val index: Int) {
      TAB_RAW_MESSAGES(R.id.tab_raw_messages, 0),
      TAB_CHAT_MESSAGES(R.id.tab_chat_messages, 1),
      TAB_PRIVATE_MESSAGES(R.id.tab_private_messages, 2),
      TAB_SETTINGS(R.id.tab_settings, 3),
    }
  }

  private var binding: ActivityMainBinding? = null

  private val viewModel: MainViewModel by viewModel()

  private var pagerAdapter: PagerAdapter? = null

  private var permissionRequestListener: PermissionRequestListener? = null

  private val rawMessagesFragment = RawMessagesFragment.newInstance()
  private val chatMessagesFragment = ChatMessagesFragment.newInstance()
  private val privateMessagesFragment = PrivateMessagesFragment.newInstance()
  private val settingsFragment = SettingsFragment.newInstance()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupView()
    requestPermissions()
  }

  private fun setupView() {
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding?.root)
    if (pagerAdapter == null) {
      pagerAdapter = PagerAdapter(
        lifecycle = lifecycle,
        fragmentManager = supportFragmentManager,
        fragments = listOf(
          rawMessagesFragment,
          chatMessagesFragment,
          privateMessagesFragment,
          settingsFragment,
        )
      )
    }
    binding?.viewPager?.adapter = pagerAdapter
    binding?.viewPager?.offscreenPageLimit = 4
    binding?.viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        when (position) {
          Tabs.TAB_RAW_MESSAGES.index -> openTab(R.id.tab_raw_messages)
          Tabs.TAB_CHAT_MESSAGES.index -> openTab(R.id.tab_chat_messages)
          Tabs.TAB_PRIVATE_MESSAGES.index -> openTab(R.id.tab_private_messages)
          Tabs.TAB_SETTINGS.index -> openTab(R.id.tab_settings)
        }
      }
    })
    binding?.bottomNavigation?.setOnItemSelectedListener { item ->
      when (item.itemId) {
        R.id.tab_raw_messages -> {
          binding?.viewPager?.currentItem = Tabs.TAB_RAW_MESSAGES.index
        }
        R.id.tab_chat_messages -> {
          binding?.viewPager?.currentItem = Tabs.TAB_CHAT_MESSAGES.index
        }
        R.id.tab_private_messages -> {
          binding?.viewPager?.currentItem = Tabs.TAB_PRIVATE_MESSAGES.index
        }
        R.id.tab_settings -> {
          binding?.viewPager?.currentItem = Tabs.TAB_SETTINGS.index
        }
      }
      true
    }
    viewModel.setupBluetoothHelper()
  }

  fun openTab(id: Int) {
    binding?.bottomNavigation?.selectedItemId = id
  }

  private fun requestPermissions() {
    if (ContextCompat.checkSelfPermission(
        this@MainActivity,
        BLUETOOTH_CONNECT
      ) == PackageManager.PERMISSION_DENIED
    ) {
      ActivityCompat.requestPermissions(
        this@MainActivity,
        arrayOf(BLUETOOTH_CONNECT),
        1
      )
    }
    if (ContextCompat.checkSelfPermission(
        this@MainActivity,
        BLUETOOTH_SCAN
      ) == PackageManager.PERMISSION_DENIED
    ) {
      ActivityCompat.requestPermissions(
        this@MainActivity,
        arrayOf(BLUETOOTH_SCAN),
        2
      )
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  fun setPermissionRequestListener(permissionRequestListener: PermissionRequestListener) {
    this.permissionRequestListener = permissionRequestListener
  }

}