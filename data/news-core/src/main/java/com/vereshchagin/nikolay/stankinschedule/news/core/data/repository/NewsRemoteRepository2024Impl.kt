package com.vereshchagin.nikolay.stankinschedule.news.core.data.repository

import android.util.Log
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.StankinNews2024API
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import retrofit2.await
import javax.inject.Inject

class NewsRemoteRepository2024Impl @Inject constructor(
    private val newsAPI: StankinNews2024API,
) : NewsRemoteRepository {

    override suspend fun loadPage(
        newsSubdivision: Int,
        page: Int,
        count: Int
    ): List<NewsPost> {
        val text = newsAPI.getNewsPage(page).await()


        val newsBlock = Regex(
            """<a.{0,200}class="(newsItem|importantNewsItem)".*?>.+?</a>""",
            RegexOption.DOT_MATCHES_ALL
        )
        val newsTitle = Regex("""class="name".*?>(.+?)<""", RegexOption.DOT_MATCHES_ALL)

        val newsImage = Regex("""class="imgW".*?src="(.+?)"""", RegexOption.DOT_MATCHES_ALL)
        val importantNewsImage = Regex("""url\((.+?)\)""", RegexOption.DOT_MATCHES_ALL)

        val newsDate =
            Regex("""<span.*?class="date".*?>(.+?)</span>""", RegexOption.DOT_MATCHES_ALL)
        val newsLink = Regex("""href="(.+?)"""", RegexOption.DOT_MATCHES_ALL)

        return newsBlock.findAll(text)
            .asFlow()
            .map { match -> match.value }
            .map { block ->
                NewsPost(
                    id = 0,
                    title = newsTitle.find(block)
                        .getOrThrow(1),
                    previewImageUrl = (newsImage.find(block) ?: importantNewsImage.find(block))
                        ?.let { StankinNews2024API.BASE_URL + it.groupValues[1] },
                    date = processDate(
                        newsDate.find(block)
                            .getOrThrow(1)
                    ),
                    relativeUrl = newsLink.find(block)
                        ?.let { StankinNews2024API.BASE_URL + it.groupValues[1] }
                )
            }
            .catch {
                Log.e("NewsRemoteRepository2024Impl", "Load page $page error", it)
            }
            .toList()
            .also { Log.d("NewsRemoteRepository2024Impl", "loadPage: $it") }
    }

    private fun processDate(text: String): String {
        return try {
            text
                .replace("<br>", "/")
                .replace("\"", "")
                .trim()
                .let {
                    DateTimeFormat.forPattern("dd/MM/yyyy")
                        .parseDateTime(it)
                        .toString(ISODateTimeFormat.date())
                }
        } catch (ignored: Throwable) {
            DateTime.now()
                .toString(ISODateTimeFormat.date())
        }
    }

    private fun MatchResult?.getOrThrow(index: Int): String {
        if (this == null) throw NoSuchElementException("Match is null")
        return groupValues[index]
    }
}