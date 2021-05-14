package com.example.companionpairingsample.ui.main

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanFilter
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.companiondevicepairing.service.RetryService
import com.example.companionpairingsample.R

private const val SELECT_DEVICE_REQUEST_CODE = 0
private const val DISCOVERY_UUID_STRING: String = "XXXX"
private const val TAG: String = "MainFragment"

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private val deviceManager: CompanionDeviceManager by lazy {
        requireContext().getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(requireContext()) {
            Intent(this, RetryService::class.java).also { intent ->
//                startService(intent)
            }
        }

        logAssociations()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SELECT_DEVICE_REQUEST_CODE -> when(resultCode) {
                Activity.RESULT_OK -> {
                    // The user chose to pair the app with a Bluetooth device.
                    val deviceToPair: BluetoothDevice? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)

                    Log.d(TAG, "Associated with device $deviceToPair")

                    deviceToPair?.createBond()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button).setOnClickListener {
            val scanFilter = ScanFilter.Builder()
//                .setServiceUuid(ParcelUuid.fromString(DISCOVERY_UUID_STRING))
                .build()

//            val deviceFilter: BluetoothLeDeviceFilter = BluetoothLeDeviceFilter.Builder()
//                .setScanFilter(scanFilter)
//                .build()

            val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
//                .addServiceUuid(ParcelUuid.fromString(DISCOVERY_UUID_STRING), null)
                .build()

            val pairingRequest: AssociationRequest = AssociationRequest.Builder()
                .addDeviceFilter(deviceFilter)
                .setSingleDevice(false)
                .build()

            // When the app tries to pair with a Bluetooth device, show the
            // corresponding dialog box to the user.
            deviceManager.associate(pairingRequest,
                object : CompanionDeviceManager.Callback() {

                    override fun onDeviceFound(chooserLauncher: IntentSender) {
                        Log.d(TAG, "Device found")
                        startIntentSenderForResult(chooserLauncher,
                            SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0, null)
                    }

                    override fun onFailure(error: CharSequence?) {
                        Toast.makeText(requireContext(), "Device not found", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Device not found")
                        // Handle the failure.
                    }
                }, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        logAssociations()
    }

    private fun logAssociations() {
        Log.d(TAG, "Associations ${deviceManager.associations}")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }
}