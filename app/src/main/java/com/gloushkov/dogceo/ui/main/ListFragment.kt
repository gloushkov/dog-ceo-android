package com.gloushkov.dogceo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gloushkov.dogceo.ImageListAdapter
import com.gloushkov.dogceo.R
import com.gloushkov.dogceo.databinding.FragmentListBinding
import com.gloushkov.doglib.model.Resource.Status.ERROR
import com.gloushkov.doglib.model.Resource.Status.IDLE
import com.gloushkov.doglib.model.Resource.Status.LOADING
import com.gloushkov.doglib.model.Resource.Status.SUCCESS

class ListFragment : Fragment() {

    companion object {
        const val PARAM_NUMBER_OF_IMAGES = "ListFragment#PARAM_NUMBER_OF_IMAGES"
        fun newInstance(numberOfImages: Int) = ListFragment().apply {
            arguments = Bundle().apply {
                putInt(
                    PARAM_NUMBER_OF_IMAGES, numberOfImages
                )
            }
        }
    }

    private lateinit var binding: FragmentListBinding
    private lateinit var viewModel: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ListViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadImages(requireArguments().getInt(PARAM_NUMBER_OF_IMAGES))

        viewModel.data.observe(viewLifecycleOwner) {
            when (it.status) {
                LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rv.visibility = View.GONE
                }
                SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rv.visibility = View.VISIBLE
                    binding.rv.layoutManager = LinearLayoutManager(context)
                    binding.rv.adapter = ImageListAdapter(it.data!!)
                }
                ERROR -> {
                    Toast.makeText(context, R.string.general_error, Toast.LENGTH_LONG).show()
                }
                IDLE ->{ }
            }
        }
    }
}