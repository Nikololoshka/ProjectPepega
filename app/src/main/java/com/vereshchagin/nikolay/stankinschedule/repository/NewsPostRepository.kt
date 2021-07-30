package com.vereshchagin.nikolay.stankinschedule.repository

import com.vereshchagin.nikolay.stankinschedule.api.StankinNewsPostsAPI
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsPost
import com.vereshchagin.nikolay.stankinschedule.utils.CacheFolder
import com.vereshchagin.nikolay.stankinschedule.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.await
import javax.inject.Inject

/**
 * Репозиторий для получения поста конкретной новости.
 *
 * @param newsPostsAPI API для загрузки новостей с сайта.
 * @param cacheFolder папка для кэширование новостей.
 */
class NewsPostRepository @Inject constructor(
    private val newsPostsAPI: StankinNewsPostsAPI,
    private val cacheFolder: CacheFolder
) {

    init {
        cacheFolder.addStartedPath(POSTS_FOLDER)
    }

    /**
     * Загружает пост новости. Возвращает flow загрузки новости.
     *
     * @param newsId ID новости для загрузки.
     * @param useCache использовать ли за кэшированные данные.
     */
    suspend fun loadPost(
        newsId: Int,
        useCache: Boolean = true
    ) = flow<State<NewsPost>> {

        emit(State.loading())

        try {
            // загрузка из кэша
            if (useCache) {
                val cache = cacheFolder.loadFromCache(NewsPost::class.java, newsId.toString())
                if (cache != null) {
                    emit(State.success(cache))
                    return@flow
                }
            }

            // загрузка из сети
            val post = loadFromNetwork(newsId)
            emit(State.success(post))

        } catch (e: Exception) {
            emit(State.failed(e))
        }

    }.flowOn(Dispatchers.IO)

    /**
     * Загружает пост из интернета.
     *
     * @param newsId ID новости для загрузки.
     */
    private suspend fun loadFromNetwork(newsId: Int): NewsPost {
        val post = StankinNewsPostsAPI.getNewsPost(newsPostsAPI, newsId).await().data
        cacheFolder.clearCache()
        cacheFolder.saveToCache(post, newsId.toString())
        return post
    }

    companion object {
        /**
         * Папка кэша постов новостей.
         */
        private const val POSTS_FOLDER = "posts"
    }
}