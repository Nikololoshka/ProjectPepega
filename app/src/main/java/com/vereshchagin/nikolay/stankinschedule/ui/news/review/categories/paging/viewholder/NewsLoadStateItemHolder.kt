package com.vereshchagin.nikolay.stankinschedule.ui.news.review.categories.paging.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNetworkStateBinding
import com.vereshchagin.nikolay.stankinschedule.utils.extensions.setVisibility

/**
 * Holder для отображения ошибки ил загрузки списка новостей.
 */
class NewsLoadStateItemHolder(
    private val binding: ItemNetworkStateBinding,
    private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retryCallback() }
    }

    /**
     * Связывает текущие состояние с элементом.
     */
    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }

        val isLoading = loadState is LoadState.Loading
        binding.progressBar.setVisibility(isLoading)

        binding.retryButton.setVisibility(!isLoading)
        binding.errorMsg.setVisibility(!isLoading)
    }

    companion object {
        /**
         * Создает holder для отображения ошибки ил загрузки списка новостей.
         */
        @JvmStatic
        fun create(
            parent: ViewGroup, retryCallback: () -> Unit
        ): NewsLoadStateItemHolder {
            return NewsLoadStateItemHolder(
                ItemNetworkStateBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                retryCallback
            )
        }
    }
}