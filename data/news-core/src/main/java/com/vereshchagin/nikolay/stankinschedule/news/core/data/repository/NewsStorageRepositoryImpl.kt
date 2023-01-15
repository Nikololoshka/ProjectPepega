package com.vereshchagin.nikolay.stankinschedule.news.core.data.repository

import androidx.paging.PagingSource
import androidx.room.withTransaction
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsDatabase
import com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper.toEntity
import com.vereshchagin.nikolay.stankinschedule.news.core.data.mapper.toPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsStorageRepositoryImpl @Inject constructor(
    private val db: NewsDatabase,
    private val dao: NewsDao,
) : NewsStorageRepository {

    override fun news(newsSubdivision: Int): PagingSource<Int, NewsPost> {
        return dao.all(newsSubdivision) // .transform(mapper = { it.toPost() })
    }

    override fun lastNews(newsCount: Int): Flow<List<NewsPost>> {
        return dao.latest(newsCount).map { last -> last.map { it.toPost() } }
    }

    override suspend fun saveNews(newsSubdivision: Int, posts: List<NewsPost>, force: Boolean) {
        db.withTransaction {
            // если обновляем список
            if (force) {
                dao.clear(newsSubdivision)
            }

            // индекс по порядку
            val start = dao.nextIndexInResponse(newsSubdivision)

            // добавление индекса и номера отдела
            val news = posts.mapIndexed { index, news ->
                news.toEntity(start + index, newsSubdivision)
            }

            dao.insert(news)
        }
    }

    override suspend fun clearNews(newsSubdivision: Int) {
        db.withTransaction {
            dao.clear(newsSubdivision)
        }
    }
}