package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentHomeBinding
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.home.news.NewsPostLatestAdapter
import com.vereshchagin.nikolay.stankinschedule.ui.news.viewer.NewsViewerActivity
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.utils.DrawableUtils
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2
import com.vereshchagin.nikolay.stankinschedule.utils.delegates.FragmentDelegate
import dagger.hilt.android.AndroidEntryPoint

/**
 * Фрагмент главной страницы.
 */
@AndroidEntryPoint
class HomeFragment
    : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), View.OnClickListener {

    /**
     * StatefulLayout состояний расписания.
     */
    private var scheduleStateful: StatefulLayout2 by FragmentDelegate()

    /**
     * ViewModel фрагмента.
     */
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        scheduleStateful = StatefulLayout2.Builder(binding.scheduleLayout)
            .init(StatefulLayout2.LOADING, binding.scheduleLoading)
            .addView(StatefulLayout2.EMPTY, binding.noFavoriteSchedule)
            .addView(StatefulLayout2.CONTENT, binding.schedulePager)
            .create()

        scheduleStateful.isAnimated = false
        binding.scheduleLoading.setShimmer(DrawableUtils.createShimmer())

        // нажатие по заголовкам
        binding.scheduleName.setOnClickListener(this)
        // binding.mjName.setOnClickListener(this)
        binding.newsName.setOnClickListener(this)

        // установка названия расписания
        val favorite = ScheduleRepository.favorite(requireContext())
        if (favorite != null && favorite.isNotEmpty()) {
            binding.scheduleName.text = favorite
        }

        // установка данных в pager
        viewModel.scheduleData.observe(viewLifecycleOwner, Observer {
            val data = it ?: return@Observer

            if (data.empty) {
                scheduleStateful.setState(StatefulLayout2.EMPTY)
            } else {
                binding.scheduleName.text = data.scheduleName
                binding.schedulePager.update(data.titles, data.pairs)
                scheduleStateful.setState(StatefulLayout2.CONTENT)
            }
        })

        val glide = DrawableUtils.createGlide(this)
        val adapter = NewsPostLatestAdapter(this::onNewsClick, glide)

        binding.newsLatest.adapter = adapter
        binding.newsLatest.setHasFixedSize(true)

        // разделитель элементов
        val itemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        binding.newsLatest.addItemDecoration(itemDecoration)

        // новости
        viewModel.newsData.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        parentFragmentManager.setFragmentResultListener(
            ChangeSubgroupBottomSheet.REQUEST_CHANGE_SUBGROUP, this, this::onScheduleSubgroupChanged
        )

        trackScreen(TAG, MainActivity.TAG)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkScheduleData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_subgroup) {
            val dialog = ChangeSubgroupBottomSheet()
            dialog.show(parentFragmentManager, dialog.tag)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onScheduleSubgroupChanged(key: String, bundle: Bundle) {
        viewModel.updateSchedule()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // расписание
            R.id.schedule_name -> {
                val favorite = ScheduleRepository.favorite(requireContext())
                if (favorite != null && favorite.isNotEmpty()) {
                    navigateTo(
                        R.id.to_schedule_view_fragment,
                        ScheduleViewFragment.createBundle(favorite)
                    )
                } else {
                    navigateTo(R.id.nav_schedule_fragment)
                }
            }
            // модульный журнал
            // R.id.mj_name -> {
            //     navigateTo(R.id.nav_module_journal_fragment)
            // }
            // новости
            R.id.news_name -> {
                navigateTo(R.id.nav_news_fragment)
            }
        }
    }

    private fun onNewsClick(newsId: Int, newsTitle: String?) {
        val intent = NewsViewerActivity.newsIntent(requireContext(), newsId, newsTitle)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}