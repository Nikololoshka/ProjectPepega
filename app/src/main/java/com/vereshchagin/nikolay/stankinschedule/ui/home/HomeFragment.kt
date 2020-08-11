package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.databinding.FragmentHomeBinding
import com.vereshchagin.nikolay.stankinschedule.ui.BaseFragment
import com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.ScheduleViewFragment
import com.vereshchagin.nikolay.stankinschedule.ui.settings.SchedulePreference
import com.vereshchagin.nikolay.stankinschedule.utils.StatefulLayout2

/**
 * Фрагмент главной страницы.
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener {

    companion object {
        private const val TAG = "HomeFragmentLog"
    }

    private var _scheduleStateful : StatefulLayout2? = null
    private val scheduleStateful get() = _scheduleStateful!!

    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModel.Factory(activity?.application!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onPostCreateView(savedInstanceState: Bundle?) {
        _scheduleStateful = StatefulLayout2(binding.scheduleLayout,
            StatefulLayout2.LOADING, binding.scheduleLoading.loadingFragment)
        scheduleStateful.addView(StatefulLayout2.EMPTY, binding.noFavoriteSchedule)
        scheduleStateful.addView(StatefulLayout2.CONTENT, binding.schedulePager)

        // нажатие по заголовкам
        binding.scheduleName.setOnClickListener(this)
        binding.mjName.setOnClickListener(this)
        binding.newsName.setOnClickListener(this)

        // установка названия расписания
        val favorite = SchedulePreference.favorite(requireContext())
        if (favorite != null && favorite.isNotEmpty()) {
            binding.scheduleName.text = favorite
        }

        // установка данных в pager
        viewModel.scheduleData.observe(viewLifecycleOwner, Observer {
            val data = it ?: return@Observer

            if (data.empty) {
                scheduleStateful.setState(StatefulLayout2.EMPTY)
            } else {
                binding.schedulePager.update(data.titles, data.pairs)
                scheduleStateful.setState(StatefulLayout2.CONTENT)
            }
        })

        binding.testButton.setOnClickListener {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_subgroup) {
            val dialog =  ChangeSubgroupBottomSheet()
            dialog.show(childFragmentManager, dialog.tag)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // расписание
            R.id.schedule_name -> {
                val favorite = SchedulePreference.favorite(requireContext())
                if (favorite != null && favorite.isNotEmpty()) {
                    navigateTo(R.id.fromHomeFragmentToScheduleViewFragment,
                        ScheduleViewFragment.createBundle(
                            favorite, SchedulePreference.createPath(requireContext(), favorite)
                        ))
                } else {
                    navigateTo(R.id.toScheduleFragment)
                }
            }
            // модульный журнал
            R.id.mj_name -> {
                navigateTo(R.id.toModuleJournalFragment)
            }
            // новости
            R.id.news_name -> {
                navigateTo(R.id.toNewsFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _scheduleStateful = null
    }
}