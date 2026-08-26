package com.example.passwordgenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.passwordgenerator.databinding.ActivityMainBinding
import java.security.SecureRandom

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val secureRandom = SecureRandom()

    private val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowercase = "abcdefghijklmnopqrstuvwxyz"
    private val numbers = "0123456789"
    private val symbols = "!@#$%^&*()-_=+[]{}<>?/|"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        updateLengthText()
        generatePassword()
    }

    private fun setupListeners() {
        binding.lengthSlider.addOnChangeListener { _, _, _ ->
            updateLengthText()
        }

        binding.generateButton.setOnClickListener {
            generatePassword()
        }

        binding.copyButton.setOnClickListener {
            copyPassword()
        }
    }

    private fun updateLengthText() {
        binding.lengthText.text =
            "${binding.lengthSlider.value.toInt()} characters"
    }

    private fun generatePassword() {
        val length = binding.lengthSlider.value.toInt()
        val selectedGroups = mutableListOf<String>()

        if (binding.uppercaseCheck.isChecked) selectedGroups.add(uppercase)
        if (binding.lowercaseCheck.isChecked) selectedGroups.add(lowercase)
        if (binding.numbersCheck.isChecked) selectedGroups.add(numbers)
        if (binding.symbolsCheck.isChecked) selectedGroups.add(symbols)

        if (selectedGroups.isEmpty()) {
            Toast.makeText(
                this,
                "Select at least one character type",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val password = StringBuilder()

        selectedGroups.forEach { group ->
            password.append(group[secureRandom.nextInt(group.length)])
        }

        val characterPool = selectedGroups.joinToString("")

        while (password.length < length) {
            password.append(
                characterPool[secureRandom.nextInt(characterPool.length)]
            )
        }

        val chars = password.toMutableList()

        for (i in chars.indices.reversed()) {
            val j = secureRandom.nextInt(i + 1)
            val temp = chars[i]
            chars[i] = chars[j]
            chars[j] = temp
        }

        binding.passwordText.text = chars.joinToString("")
    }

    private fun copyPassword() {
        val password = binding.passwordText.text.toString()

        if (password.isBlank()) return

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText("Generated Password", password)
        )

        Toast.makeText(
            this,
            "Password copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }
}
