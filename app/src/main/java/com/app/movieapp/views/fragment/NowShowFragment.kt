package com.app.movieapp.views.fragment


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.app.movieapp.R
import com.app.movieapp.adapter.MovieAdapter
import com.app.movieapp.baseclass.BaseFragment
import com.app.movieapp.databinding.FragmentNowShowBinding
import com.app.movieapp.retrofit.NetworkState
import com.app.movieapp.utility.KEY_KEYWORD
import com.app.movieapp.utility.NETWORK_ERROR
import com.app.movieapp.utility.NOW_SHOWING
import com.app.movieapp.utility.Utils
import com.app.movieapp.viewmodel.MovieVM
import kotlinx.android.synthetic.main.fragment_now_show.*


class NowShowFragment : BaseFragment() {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieVM: MovieVM
    private lateinit var keyWord: String
    private lateinit var mBinding: FragmentNowShowBinding

    companion object {
        fun newInstance(keyWord: String): NowShowFragment {
            val fragment = NowShowFragment()
            val args = Bundle()
            args.putString(KEY_KEYWORD, keyWord)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(R.layout.fragment_now_show)
        keyWord = arguments?.getString(KEY_KEYWORD) ?: ""
    }


    override fun initVariable() {
        mBinding = getBinding() as FragmentNowShowBinding
        movieVM = setViewModel<MovieVM>() as MovieVM
        movieAdapter = MovieAdapter {
            movieVM.retryClick()
        }
        rv_nowShowing.adapter = movieAdapter

    }

    override fun loadData() {

        setupPagination()


        mBinding.layoutRetry.btnRetry.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            mBinding.layoutRetry.layoutMain.visibility = View.GONE
            setupPagination()
        }
    }

    private fun setupPagination() {

        if (Utils.isNetworkAvailable(requireContext())) {
            movieVM.initPagination(keyWord, NOW_SHOWING)

            movieVM.movies.observe(this, Observer {
                movieAdapter.submitList(it)
            })


            movieVM.networkState.observe(this, Observer {
                if (it == NetworkState.LOADED) {
                    progressbar.visibility = View.GONE
                }
                movieAdapter.setNetworkState(it)
            })


        } else {
            progressbar.visibility = View.GONE
            mBinding.layoutRetry.layoutMain.visibility = View.VISIBLE
            mBinding.layoutRetry.txtError.text = NETWORK_ERROR
        }
    }


}
