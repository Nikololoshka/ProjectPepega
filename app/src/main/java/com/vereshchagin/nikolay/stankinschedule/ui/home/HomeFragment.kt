package com.vereshchagin.nikolay.stankinschedule.ui.home

import android.app.Activity
import android.content.Intent
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

            if (!viewModel.isScheduleDeltaCorrect()) {
                viewModel.updateSchedule()

                return@Observer
            }

            if (data.empty) {
                scheduleStateful.setState(StatefulLayout2.EMPTY)
            } else {
                binding.schedulePager.update(data.titles, data.pairs)
                scheduleStateful.setState(StatefulLayout2.CONTENT)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.change_subgroup) {
            val dialog =  ChangeSubgroupBottomSheet()
            dialog.setTargetFragment(this, REQUEST_SUBGROUP)
            dialog.show(parentFragmentManager, dialog.tag)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }

        if (requestCode == REQUEST_SUBGROUP) {
            viewModel.updateSchedule()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            // расписание
            R.id.schedule_name -> {
                val favorite = SchedulePreference.favorite(requireContext())
                if (favorite != null && favorite.isNotEmpty()) {
                    navigateTo(R.id.to_schedule_view_fragment,
                        ScheduleViewFragment.createBundle(
                            favorite, SchedulePreference.createPath(requireContext(), favorite)
                        ))
                } else {
                    navigateTo(R.id.nav_schedule_fragment)
                }
            }
            // модульный журнал
            R.id.mj_name -> {
                navigateTo(R.id.nav_module_journal_fragment)
            }
            // новости
            R.id.news_name -> {
                navigateTo(R.id.nav_news_fragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _scheduleStateful = null
    }

    companion object {
        private const val TAG = "HomeFragmentLog"

        private const val REQUEST_SUBGROUP = 1
    }
}