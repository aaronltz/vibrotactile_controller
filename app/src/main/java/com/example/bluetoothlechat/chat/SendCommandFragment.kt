/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bluetoothlechat.chat

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothlechat.bluetooth.Message
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.bluetooth.ChatServer
import com.example.bluetoothlechat.databinding.FragmentBluetoothChatBinding
import com.example.bluetoothlechat.gone
import com.example.bluetoothlechat.visible

private const val TAG = "BluetoothChatFragment"

class BluetoothChatFragment : Fragment() {

    private var _binding: FragmentBluetoothChatBinding? = null
    // this property is valid between onCreateView and onDestroyView.
    private val binding: FragmentBluetoothChatBinding
        get() = _binding!!

    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when(state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                Log.d(TAG, "Gatt connection observer: have device $device")
                //chatWith(device)
                chatWith()
            }
            is DeviceConnectionState.Disconnected -> {
                showDisconnected()
            }
        }

    }

    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        Log.d(TAG, "Connection request observer: have device $device")
        //ChatServer.setCurrentChatConnection(device)
        ChatServer.setChatConnection(device)
    }

    private val messageObserver = Observer<Message> { message ->
        Log.d(TAG, "Have message ${message.text}")
        adapter.addMessage(message)
    }

    private val adapter = MessageAdapter()

    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBluetoothChatBinding.inflate(inflater, container, false)

        Log.d(TAG, "chatWith: set adapter $adapter")
        /**binding.messages.layoutManager = LinearLayoutManager(context)
        binding.messages.adapter = adapter*/

        showDisconnected()

        /**binding.connectDevices.setOnClickListener {
            findNavController().navigate(R.id.action_find_new_device)
        }*/

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.chat_title)
        ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun chatWith() {
        //General Commands
        binding.buttonStop.setOnClickListener {
            ChatServer.sendMessage("f")
        }
        binding.buttonStart.setOnClickListener {
            ChatServer.sendMessage("z")
        }
        binding.buttonTurnAround.setOnClickListener {
            ChatServer.sendMessage("v")
        }
        binding.sliderIntensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
                ChatServer.sendMessage(p0?.progress.toString())
            }
        })

        //Left Commands
        binding.buttonLeft90.setOnClickListener {
            ChatServer.sendMessage("s")
        }
        binding.buttonLeft45.setOnClickListener {
            ChatServer.sendMessage("e")
        }
        binding.buttonToogleLeft.setOnClickListener {
            //binding.buttonToogleLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            binding.sliderIntensity.setProgress(0)
            ChatServer.sendMessage("q")
        }
        /**binding.buttonShelfLeft.setOnClickListener {
            ChatServer.sendMessage("a")
        }*/

        //Right Commands
        binding.buttonRight90.setOnClickListener {
            ChatServer.sendMessage("j")
        }
        binding.buttonRight45.setOnClickListener {
            ChatServer.sendMessage("u")
        }
        binding.buttonToggleRight.setOnClickListener {
            binding.sliderIntensity.setProgress(0)
            ChatServer.sendMessage("p")
        }
        /**binding.buttonShelfRight.setOnClickListener {
            ChatServer.sendMessage("k")
        }*/
    }

    private fun showDisconnected() {
        /**hideKeyboard()
        binding.notConnectedContainer.visible()
        binding.connectedContainer.gone()*/
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}