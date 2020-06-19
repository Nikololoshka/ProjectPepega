package com.vereshchagin.nikolay.stankinschedule.news.post.paging.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNetworkStateBinding
import com.vereshchagin.nikolay.stankinschedule.news.network.NetworkState
import com.vereshchagin.nikolay.stankinschedule.news.network.Status

/**
 * Элемент, отображающий загрузку данных, ошибку и кнопку "повторить попытку".
 */
class NetworkStateItemHolder(
    itemView: View, private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemNetworkStateBinding.bind(itemView)

    init {
        binding.retryButton.setOnClickListener {
            retryCallback()
        }
    }

    /**
     * Связывает данные с элементом.
     * @param networkState информация о загрузке данных.
     */
    fun bind(networkState: NetworkState?) {
        binding.progressBar.visibility = toVisibility(networkState?.status == Status.RUNNING)
        binding.retryButton.visibility = toVisibility(networkState?.status == Status.FAILED)
        binding.errorMsg.visibility = toVisibility(networkState?.msg != null)
        binding.errorMsg.text = networkState?.msg
    }

    private fun toVisibility(constraint : Boolean): Int {
        return if (constraint) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_network_state, parent, false)
            return NetworkStateItemHolder(view, retryCallback)
        }
    }
}