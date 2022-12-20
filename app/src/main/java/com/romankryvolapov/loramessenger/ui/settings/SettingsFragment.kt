package com.romankryvolapov.loramessenger.ui.settings

import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.romankryvolapov.loramessenger.R
import com.romankryvolapov.loramessenger.databinding.FragmentSettingsBinding
import com.romankryvolapov.loramessenger.helpers.launch
import com.romankryvolapov.loramessenger.helpers.showMessageSnackBar
import com.romankryvolapov.loramessenger.ui.main.MainActivity
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(), PermissionRequestListener {

  companion object {
    private const val TAG = "SettingsFragment"
    fun newInstance() = SettingsFragment()
  }

  private var binding: FragmentSettingsBinding? = null
  private val viewModel: SettingsViewModel by viewModel()
  private var bluetoothDevicesAdapter: ArrayAdapter<String>? = null
  private var transmitterReceiverSpeedAdapter: ArrayAdapter<CharSequence>? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentSettingsBinding.inflate(inflater, container, false)
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupView()
    subscribeToViewModel()
    getBluetoothDevices()
  }


  @SuppressLint("ResourceType")
  private fun setupView() {
    (activity as MainActivity).setPermissionRequestListener(this)
    bluetoothDevicesAdapter = ArrayAdapter<String>(
      requireContext(),
      R.layout.list_item_dropdown
    )
    binding?.spinnerBluetoothDevices?.adapter = bluetoothDevicesAdapter
    binding?.spinnerBluetoothDevices?.onItemSelectedListener =
      object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          viewModel.selectBluetoothDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
          // NO
        }
      }
    transmitterReceiverSpeedAdapter = ArrayAdapter.createFromResource(
      requireContext(),
      R.array.transmitter_receiver_speed,
      R.layout.list_item_dropdown
    )
    binding?.spinnerTransmitterReceiverSpeed?.adapter = transmitterReceiverSpeedAdapter
    binding?.spinnerTransmitterReceiverSpeed?.setSelection(2)
    binding?.spinnerTransmitterReceiverSpeed?.onItemSelectedListener =
      object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          viewModel.selectBluetoothDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
          // NO
        }
      }
    binding?.buttonGetLoraSettings?.setOnClickListener {
      viewModel.getLoraSettings()
    }
    binding?.buttonSetDefaultLoraSettings?.setOnClickListener {
      viewModel.setDefaultLoraSettings()
    }
    binding?.buttonConnectToBluetoothDevice?.setOnClickListener {
      viewModel.connectOrDisconnectBluetoothDevice()
    }
    binding?.buttonSearchBluetoothDevices?.setOnClickListener {
      getBluetoothDevices()
    }
    viewModel.subscribeToData()
  }

  private fun subscribeToViewModel() {
    viewModel.loraSettingsFlow.onEach {
      it?.let {

      }
    }.launch(lifecycleScope)
    viewModel.bluetoothDevicesFlow.onEach { list ->
      bluetoothDevicesAdapter?.clear()
      if (list.isNotEmpty()) {
        bluetoothDevicesAdapter?.addAll(list)
      }
    }.launch(lifecycleScope)

    viewModel.bluetoothConnectionStatusFlow.onEach {
      binding?.buttonConnectToBluetoothDevice?.text = if (it) "Disconnect"
      else "Connect"
    }.launch(lifecycleScope)
    viewModel.bluetoothDevicePositionFlow.onEach { position ->
      binding?.spinnerBluetoothDevices?.size?.let {
        if (it <= position) {
          binding?.spinnerBluetoothDevices?.setSelection(position)
        }
      }
    }.launch(lifecycleScope)
    viewModel.connectionLogFlow.onEach { log ->
      binding?.textViewLogMessageLine0?.text = log[2]
      binding?.textViewLogMessageLine1?.text = log[1]
      binding?.textViewLogMessageLine2?.text = log[0]
    }.launch(lifecycleScope)
  }

  override fun onPermissionChanged() {
    getBluetoothDevices()
  }

  private fun getBluetoothDevices() {
    if (ContextCompat.checkSelfPermission(
        requireContext(),
        BLUETOOTH_CONNECT
      ) == PackageManager.PERMISSION_DENIED ||
      ContextCompat.checkSelfPermission(
        requireContext(),
        BLUETOOTH_SCAN
      ) == PackageManager.PERMISSION_DENIED
    ) {
      binding?.root.showMessageSnackBar("No bluetooth permissions")
      return
    }
    viewModel.getBluetoothDevices(requireContext())
  }
}