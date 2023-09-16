package com.gloushkov.dogceo.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gloushkov.dogceo.ListActivity
import com.gloushkov.dogceo.R
import com.gloushkov.dogceo.databinding.FragmentMainBinding
import com.gloushkov.dogceo.ui.main.MainViewModel.SubmitError.ABOVE_RANGE
import com.gloushkov.dogceo.ui.main.MainViewModel.SubmitError.BELLOW_RANGE
import com.gloushkov.dogceo.ui.main.MainViewModel.SubmitError.NOT_A_NUMBER
import com.gloushkov.dogceo.ui.main.MainViewModel.SubmitError.RESOURCE_ERROR

private const val TAG = "MainFragment"

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressSpinner.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.previousButtonEnabled.observe(viewLifecycleOwner) {
            binding.btnPrevious.isEnabled = it
        }

        viewModel.submitError.observe(viewLifecycleOwner) {
            Log.w(TAG, "submitError: $it")
            when (it) {
                NOT_A_NUMBER -> binding.tilCount.error =
                    requireContext().resources.getString(R.string.submit_input_not_integer)

                BELLOW_RANGE, ABOVE_RANGE -> binding.tilCount.error =
                    requireContext().resources.getString(R.string.submit_input_count_range_not_valid)

                RESOURCE_ERROR -> {
                    binding.tilCount.error = null
                    //TODO show some ui error
                }

                null -> binding.tilCount.error = null
            }
        }

        viewModel.currentBitmap.observe(viewLifecycleOwner) {
            binding.ivMain.setImageBitmap(it)
        }

        binding.btnNext.setOnClickListener {
            viewModel.onNext()
        }
        binding.btnPrevious.setOnClickListener {
            viewModel.onPrevious()
        }

        binding.etCount.setOnEditorActionListener { _, actionId, _ ->
            Log.v(TAG, "etCount -- editor action: $actionId")
            if (EditorInfo.IME_ACTION_GO == actionId) {
                viewModel.onSubmit(binding.etCount.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.onSubmit(binding.etCount.text.toString())
        }

        viewModel.redirectToList.observe(viewLifecycleOwner) {
            startActivity(Intent(context, ListActivity::class.java).apply {
                putExtra(ListFragment.PARAM_NUMBER_OF_IMAGES, it)
            })
        }
    }
}