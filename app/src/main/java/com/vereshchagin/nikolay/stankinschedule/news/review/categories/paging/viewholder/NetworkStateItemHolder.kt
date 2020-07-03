package com.vereshchagin.nikolay.stankinschedule.news.review.categories.paging.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.ItemNetworkStateBinding
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.network.NetworkState
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.network.Status

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
     * @param position позиция элемента.
     */
    fun bind(networkState: NetworkState?, position: Int) {
        binding.root.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            if (position == 0) {
                LinearLayout.LayoutParams.MATCH_PARENT
            } else {
                LinearLayout.LayoutParams.WRAP_CONTENT
            }
        )

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